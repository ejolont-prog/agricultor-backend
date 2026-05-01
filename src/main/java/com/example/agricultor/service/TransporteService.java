package com.example.agricultor.service;// --- IMPORTS DE SPRING FRAMEWORK ---
import com.example.agricultor.model.Transportista;
import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class TransporteService {

    @Autowired
    private TransporteRepository repository;

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
    public Transporte crearTransporte(Map<String, Object> payload) {
        Long idUsuarioLogueado = userSecurityService.getCurrentUserId();

        // 1. EXTRAER DATOS DEL PAYLOAD
        String placa = (String) payload.get("placa");

        // Obtenemos el modelo (año) directamente como String
        Object modeloObj = payload.get("idModelo");
        String anioModelo = (modeloObj != null) ? modeloObj.toString() : "N/A";

        // IDs para buscar nombres de catálogos (Marca, Linea, Color, TipoPlaca)
        Object idTipoPlaca = payload.get("idTipoPlaca");
        Object idMarca = payload.get("idMarca");
        Object idLinea = payload.get("idLinea");
        Object idColor = payload.get("idColor");

        // --- 2. VALIDACIÓN DE PLACA ---
        if (repository.existsByPlaca(placa)) {
            throw new BusinessException("Ya existe un transporte registrado con la placa " + placa);
        }

        // --- 3. RECUPERAR NOMBRES PARA EL ESQUEMA BENEFICIO ---
        String nombreTipoPlaca = obtenerNombreCatalogo(idTipoPlaca);
        String nombreMarca = obtenerNombreCatalogo(idMarca);
        String nombreLinea = obtenerNombreCatalogo(idLinea);
        String nombreColor = obtenerNombreCatalogo(idColor);

        // --- 4. GUARDAR EN BENEFICIO (JPA - Entidad Transporte) ---
        Transporte t = new Transporte();
        t.setPlaca(placa);
        t.setMarca(nombreMarca);
        t.setLinea(nombreLinea);
        t.setColor(nombreColor);

        // AQUÍ ESTÁ EL CAMBIO CLAVE: Asignamos el año directamente al campo modelo
        t.setModelo(anioModelo);

        t.setEstado(28); // ID fijo para el estado inicial
        t.setCreadopor(idUsuarioLogueado.intValue());
        t.setDisponible(true);

        Transporte guardado = repository.save(t);

        // --- 5. GUARDAR EN AGRICULTOR (JDBC - IDs y Año) ---
        String sql = "INSERT INTO agricultor.transportes " +
                "(placa, tipoplaca, marca, color, linea, modelo, estado, disponible, creadopor) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                placa,
                (idTipoPlaca != null) ? idTipoPlaca.toString() : null,
                idMarca,
                idColor,
                idLinea,
                anioModelo, // Se guarda como varchar en agricultor también
                28,
                true,
                idUsuarioLogueado.intValue()
        );

        return guardado;
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
}