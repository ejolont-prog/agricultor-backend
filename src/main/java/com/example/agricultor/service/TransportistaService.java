package com.example.agricultor.service;

import com.example.agricultor.dto.TransportistaRequestDTO;
import com.example.agricultor.exception.BusinessException;
import com.example.agricultor.model.Transportista;
import com.example.agricultor.repository.CatalogoRepository;
import com.example.agricultor.repository.TransportistaRepository;
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

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransportistaService {

    @Autowired
    private TransportistaRepository repository;

    @Autowired
    private UserSecurityService userSecurityService;

    @Autowired
    private CatalogoRepository repositoryCatalogo;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // <--- NUEVO

    public List<Transportista> listarDisponibles() {
        String sql = "SELECT * FROM agricultor.transportistas " +
                "WHERE estado = 28 AND disponible = true AND eliminado = false";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Transportista t = new Transportista();
            t.setIdtransportista(rs.getLong("idtransportista"));
            t.setNombreCompleto(rs.getString("nombrecompleto"));
            t.setCui(rs.getString("cui"));
            t.setFechaVencimientoLicencia(rs.getObject("fechavencimientolicencia", LocalDate.class));
            return t;
        });
    }

    @Transactional
    public Object crearTransportista(TransportistaRequestDTO dto) {
        RestTemplate restTemplate = new RestTemplate();
        String urlBeneficio = "http://localhost:8083/api/transportistas-beneficio/validar-y-crear";
        Long idUsuarioLogueado = userSecurityService.getCurrentUserId();
        dto.setNitAgricultor(obtenerNitUsuarioLogueado(idUsuarioLogueado));

        if (dto.getFechaVencimientoLicencia() != null) {
            if (dto.getFechaVencimientoLicencia().isBefore(LocalDate.now())) {
                throw new BusinessException("La licencia se encuentra vencida");
            }
        }

        if (dto.getFechaNacimiento() != null && Period.between(dto.getFechaNacimiento(), LocalDate.now()).getYears() < 18) {
            throw new BusinessException("El transportista es menor de edad");
        }

        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            String token = (attrs != null) ? attrs.getRequest().getHeader("Authorization") : null;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (token != null) headers.set("Authorization", token);

            HttpEntity<TransportistaRequestDTO> entity = new HttpEntity<>(dto, headers);
            ResponseEntity<Object> respuesta = restTemplate.postForEntity(urlBeneficio, entity, Object.class);

            if (respuesta.getStatusCode().is2xxSuccessful()) {
                String sql = "INSERT INTO agricultor.transportistas " +
                        "(cui, nombrecompleto, fechanacimiento, tipolicencia, fechavencimientolicencia, estado, disponible, creadopor, eliminado) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                jdbcTemplate.update(sql,
                        dto.getCui(),
                        dto.getNombreCompleto(),
                        dto.getFechaNacimiento(),
                        dto.getIdTipoLicencia(),
                        dto.getFechaVencimientoLicencia(),
                        28,
                        true,
                        idUsuarioLogueado.intValue(),
                        false
                );

                // --- NOTIFICAR CREACIÓN ---
                notificarCambioTransportista(dto.getCui(), "REGISTRADO", true, null);

                return respuesta.getBody();
            }
        } catch (HttpStatusCodeException e) {
            throw new BusinessException(e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new BusinessException("Error: " + e.getMessage());
        }
        throw new BusinessException("No se pudo completar el registro.");
    }

    public List<Map<String, Object>> listarPorAgricultor() {
        Long idUsuarioLogueado = userSecurityService.getCurrentUserId();

        String sql = "SELECT t.cui, t.nombrecompleto, t.fechavencimientolicencia, t.disponible, " +
                "c1.nombre as nombre_estado, " +
                "c2.nombre as nombre_licencia, " +
                "(SELECT p.nocuenta FROM agricultor.parcialidades parc " +
                " JOIN agricultor.pesajes p ON parc.idpesaje = p.idpesaje " +
                " WHERE parc.idtransportista = t.idtransportista AND parc.eliminado = false " +
                " ORDER BY parc.fechacreacion DESC LIMIT 1) as nocuenta " +
                "FROM agricultor.transportistas t " +
                "INNER JOIN agricultor.catalogos c1 ON t.estado = c1.id " +
                "INNER JOIN agricultor.catalogos c2 ON t.tipolicencia = c2.id " +
                "WHERE t.creadopor = ? AND t.eliminado = false";

        return jdbcTemplate.queryForList(sql, idUsuarioLogueado.intValue());
    }

    @Transactional
    public void sincronizarEstadoDesdeBeneficio(String cui, String nombreEstado) {
        Integer nuevoIdEstado = repositoryCatalogo.findIdByNombreAndCatalogoCuatro(nombreEstado);
        if (nuevoIdEstado == null) {
            throw new BusinessException("No existe estado: " + nombreEstado);
        }

        String sql = "UPDATE agricultor.transportistas SET estado = ?, modificadopor = 1, fechamodificacion = CURRENT_TIMESTAMP WHERE cui = ?";
        int filas = jdbcTemplate.update(sql, nuevoIdEstado, cui);

        if (filas == 0) {
            throw new BusinessException("No existe transportista con CUI: " + cui);
        }

        // --- NOTIFICAR CAMBIO DE ESTADO ---
        notificarCambioTransportista(cui, nombreEstado, null, null);
    }

    // MÉTODO AUXILIAR DE NOTIFICACIÓN
    private void notificarCambioTransportista(String cui, String nombreEstado, Boolean disponible, String nocuenta) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("cui", cui);
        payload.put("nombreEstado", nombreEstado);
        if (disponible != null) payload.put("disponible", disponible);
        if (nocuenta != null) payload.put("nocuenta", nocuenta);

        messagingTemplate.convertAndSend("/topic/actualizacion-transportista", payload);
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