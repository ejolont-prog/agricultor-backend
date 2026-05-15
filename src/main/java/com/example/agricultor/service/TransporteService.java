package com.example.agricultor.service;

import com.example.agricultor.dto.TransporteRequestDTO;
import com.example.agricultor.exception.BusinessException;
import com.example.agricultor.model.Transporte;
import com.example.agricultor.repository.CatalogoRepository;
import com.example.agricultor.repository.TransporteRepository;
import com.example.agricultor.security.UserSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate; // <--- NUEVO
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransporteService {

    @Autowired
    private TransporteRepository repository;

    @Autowired
    private CatalogoRepository repositoryCatalogo;

    @Autowired
    private UserSecurityService userSecurityService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // <--- NUEVO: Para enviar mensajes a WebSockets

    public List<Transporte> listarDisponibles() {
        String sql = "SELECT * FROM agricultor.transportes " +
                "WHERE disponible = true AND eliminado = false";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Transporte t = new Transporte();
            t.setIdtransporte(rs.getLong("idtransporte"));
            t.setPlaca(rs.getString("placa"));
            t.setMarca(rs.getInt("marca"));
            t.setColor(rs.getInt("color"));
            t.setLinea(rs.getInt("linea"));
            t.setModelo(rs.getString("modelo"));
            return t;
        });
    }

    @Transactional
    public Object crearTransporte(TransporteRequestDTO dto) {
        RestTemplate restTemplate = new RestTemplate();
        String urlBeneficio = "http://localhost:8083/api/transportes-beneficio/validar-y-crear";

        Long idUsuarioLogueado = userSecurityService.getCurrentUserId();
        dto.setNitAgricultor(obtenerNitUsuarioLogueado(idUsuarioLogueado));

        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            String token = (attrs != null) ? attrs.getRequest().getHeader("Authorization") : null;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (token != null) headers.set("Authorization", token);

            HttpEntity<TransporteRequestDTO> entity = new HttpEntity<>(dto, headers);

            ResponseEntity<Object> respuesta = restTemplate.postForEntity(urlBeneficio, entity, Object.class);

            if (respuesta.getStatusCode().is2xxSuccessful()) {
                String sql = "INSERT INTO agricultor.transportes " +
                        "(placa, tipoplaca, marca, color, linea, modelo, estado, disponible, creadopor) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                jdbcTemplate.update(sql,
                        dto.getPlaca().toUpperCase(),
                        dto.getIdTipoPlaca().intValue(),
                        dto.getIdMarca().intValue(),
                        dto.getIdColor().intValue(),
                        dto.getIdLinea().intValue(),
                        String.valueOf(dto.getIdModelo()),
                        28,
                        true,
                        idUsuarioLogueado.intValue()
                );

                // --- NUEVO: NOTIFICAR CREACIÓN VIA WEBSOCKET ---
                notificarCambioTransporte(dto.getPlaca().toUpperCase(), "REGISTRADO", true, null);

                return respuesta.getBody();
            }
        } catch (HttpStatusCodeException e) {
            throw new BusinessException(e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new BusinessException("Error en comunicación: " + e.getMessage());
        }
        return null;
    }

    public List<Map<String, Object>> listarTransportesPorUsuario() {
        Long idUsuarioLogueado = userSecurityService.getCurrentUserId();
        String sql = "SELECT t.idtransporte, t.placa, " +
                "c_marca.nombre as marca, " +
                "c_color.nombre as color, " +
                "c_linea.nombre as linea, " +
                "t.modelo, " +
                "c_estado.nombre as estado, " +
                "t.disponible, " +
                "(SELECT p.nocuenta FROM agricultor.parcialidades parc " +
                " JOIN agricultor.pesajes p ON parc.idpesaje = p.idpesaje " +
                " WHERE parc.idtransporte = t.idtransporte AND parc.eliminado = false " +
                " ORDER BY parc.fechacreacion DESC LIMIT 1) as nocuenta " +
                "FROM agricultor.transportes t " +
                "LEFT JOIN agricultor.catalogos c_marca ON t.marca = c_marca.id " +
                "LEFT JOIN agricultor.catalogos c_color ON t.color = c_color.id " +
                "LEFT JOIN agricultor.catalogos c_linea ON t.linea = c_linea.id " +
                "LEFT JOIN agricultor.catalogos c_estado ON t.estado = c_estado.id " +
                "WHERE t.creadopor = ? AND t.eliminado = false";

        return jdbcTemplate.queryForList(sql, idUsuarioLogueado.intValue());
    }

    @Transactional
    public void sincronizarEstadoDesdeBeneficio(String placa, String nombreEstado) {
        System.out.println("Sincronizando Agricultor - Placa: " + placa + " Estado: " + nombreEstado);

        Integer nuevoIdEstado = repositoryCatalogo.findIdByNombreAndCatalogoCuatro(nombreEstado);

        if (nuevoIdEstado == null) {
            throw new BusinessException("No se encontró el estado '" + nombreEstado + "' en el catálogo de Agricultor.");
        }

        String sql = "UPDATE agricultor.transportes SET estado = ?, modificadopor = 1, fechamodificacion = CURRENT_TIMESTAMP WHERE placa = ?";

        int filas = jdbcTemplate.update(sql, nuevoIdEstado, placa);

        if (filas == 0) {
            throw new BusinessException("No se encontró registro para la placa: " + placa);
        }

        // --- NUEVO: NOTIFICAR CAMBIO DE ESTADO VIA WEBSOCKET ---
        // Aquí enviamos el nombre del estado para que el front lo pinte directamente
        notificarCambioTransporte(placa, nombreEstado, null, null);
    }

    /**
     * MÉTODO AUXILIAR PARA ENVIAR LA NOTIFICACIÓN
     */
    private void notificarCambioTransporte(String placa, String estado, Boolean disponible, String nocuenta) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("placa", placa);
        payload.put("estado", estado);
        if (disponible != null) payload.put("disponible", disponible);
        if (nocuenta != null) payload.put("nocuenta", nocuenta);

        // Enviamos al tópico que configuramos en el Front de Angular
        messagingTemplate.convertAndSend("/topic/actualizacion-transporte", payload);
    }

    private String obtenerNitUsuarioLogueado(Long idUsuario) {
        try {
            String sql = "SELECT nit FROM agricultor.usuario WHERE idusuario = ?";
            return jdbcTemplate.queryForObject(sql, String.class, idUsuario);
        } catch (Exception e) {
            return "N/A";
        }
    }
}