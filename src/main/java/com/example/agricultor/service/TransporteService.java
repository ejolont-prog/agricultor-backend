package com.example.agricultor.service;// --- IMPORTS DE SPRING FRAMEWORK ---
import com.example.agricultor.dto.TransporteRequestDTO;
import com.example.agricultor.model.Transportista;
import com.example.agricultor.repository.CatalogoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// --- IMPORTS DE JAVA UTIL ---
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

// --- IMPORTS DE TU PROYECTO (Verifica que los paquetes coincidan) ---
import com.example.agricultor.exception.BusinessException;
import com.example.agricultor.model.Transporte;
import com.example.agricultor.repository.TransporteRepository;
import com.example.agricultor.security.UserSecurityService;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


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

    public List<Transporte> listarDisponibles() {
        // Nota: Asegúrate de que el esquema sea 'beneficio' o 'agricultor' según tu DB real
        String sql = "SELECT * FROM agricultor.transportes " +
                "WHERE disponible = true AND eliminado = false";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Transporte t = new Transporte();
            t.setIdtransporte(rs.getLong("idtransporte"));
            t.setPlaca(rs.getString("placa"));
            t.setMarca(rs.getString("marca"));
            t.setColor(rs.getString("color"));
            t.setLinea(rs.getString("linea"));
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
        String nitDelEmisor = obtenerNitUsuarioLogueado(idUsuarioLogueado);
        dto.setNitAgricultor(nitDelEmisor);

        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            String token = (attrs != null) ? attrs.getRequest().getHeader("Authorization") : null;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (token != null) headers.set("Authorization", token);

            // --- IMPORTANTE: Aquí debes asegurarte que dto.getNombreMarca(), etc.
            // vengan llenos desde el Frontend (Angular).


            HttpEntity<TransporteRequestDTO> entity = new HttpEntity<>(dto, headers);
            ResponseEntity<Object> respuesta = restTemplate.postForEntity(urlBeneficio, entity, Object.class);

            if (respuesta.getStatusCode().is2xxSuccessful()) {
                String sql = "INSERT INTO agricultor.transportes " +
                        "(placa, tipoplaca, marca, color, linea, modelo, estado, disponible, creadopor) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                String modeloString = (dto.getIdModelo() != null) ? String.valueOf(dto.getIdModelo()) : "N/A";
                String tipoPlacaString = (dto.getIdTipoPlaca() != null) ? String.valueOf(dto.getIdTipoPlaca()) : null;

                // Guardamos IDs en la base local de Agricultor
                jdbcTemplate.update(sql,
                        dto.getPlaca(),
                        tipoPlacaString,
                        dto.getIdMarca(),
                        dto.getIdColor(),
                        dto.getIdLinea(),
                        modeloString,
                        28,
                        true,
                        idUsuarioLogueado.intValue()
                );

                return respuesta.getBody();
            }
        } catch (HttpStatusCodeException e) {
            throw new BusinessException(e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("Error: " + e.getMessage());
        }
        throw new BusinessException("No se pudo completar el registro.");
    }

    public List<Map<String, Object>> listarTransportesPorUsuario() {
        Long idUsuarioLogueado = userSecurityService.getCurrentUserId();
        String sql = "SELECT t.placa, " +
                "c_marca.nombre as marca, " +
                "c_color.nombre as color, " +
                "c_linea.nombre as linea, " +
                "t.modelo, " + // Esto traerá el año (varchar) directamente
                "c_estado.nombre as estado, " +
                "t.disponible " +
                "FROM agricultor.transportes t " +
                "LEFT JOIN agricultor.catalogos c_marca ON CAST(t.marca AS INTEGER) = c_marca.id " +
                "LEFT JOIN agricultor.catalogos c_color ON CAST(t.color AS INTEGER) = c_color.id " +
                "LEFT JOIN agricultor.catalogos c_linea ON CAST(t.linea AS INTEGER) = c_linea.id " +
                "LEFT JOIN agricultor.catalogos c_estado ON t.estado = c_estado.id " +
                "WHERE t.creadopor = ?";
        return jdbcTemplate.queryForList(sql, idUsuarioLogueado.intValue());
    }

    private String obtenerNombreCatalogo(Object id) {
        if (id == null || id.toString().isEmpty()) return "N/A";
        try {
            String sql = "SELECT nombre FROM agricultor.catalogos WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, String.class, id);
        } catch (Exception e) {
            return "N/A";
        }
    }

    @Transactional
    public void sincronizarEstadoDesdeBeneficio(String placa, String nombreEstado) {
        System.out.println("Sincronizando Agricultor - Placa: " + placa + " Estado: " + nombreEstado);

        // 1. Buscar el ID
        Integer nuevoIdEstado = repositoryCatalogo.findIdByNombreAndCatalogoCuatro(nombreEstado);
        System.out.println("ID encontrado en Catálogo 4 de Agricultor: " + nuevoIdEstado);

        if (nuevoIdEstado == null) {
            throw new BusinessException("No se encontró el estado '" + nombreEstado + "' en el catálogo de Agricultor (IDCATALOGO=4).");
        }

        // 2. Ejecutar Update con modificadopor fijo y fecha actual de la DB
        // Usamos CURRENT_TIMESTAMP o NOW() directamente en el SQL para asegurar precisión
        String sql = "UPDATE agricultor.transportes SET estado = ?, modificadopor = 1, fechamodificacion = CURRENT_TIMESTAMP WHERE placa = ?";

        int filas = jdbcTemplate.update(sql, nuevoIdEstado, placa);
        System.out.println("Filas actualizadas en Agricultor: " + filas);

        if (filas == 0) {
            throw new BusinessException("No se encontró registro para la placa: " + placa);
        }
    }

    private String obtenerNitUsuarioLogueado(Long idUsuario) {
        try {
            // Consultamos el nit en la tabla de usuarios del esquema agricultor
            String sql = "SELECT nit FROM agricultor.usuario WHERE idusuario = ?";
            return jdbcTemplate.queryForObject(sql, String.class, idUsuario);
        } catch (Exception e) {
            return "N/A"; // Valor por defecto si no se encuentra
        }
    }
}