package com.example.agricultor.service;

import com.example.agricultor.exception.BusinessException;
import com.example.agricultor.model.Transportista;
import com.example.agricultor.repository.TransportistaRepository;
import com.example.agricultor.security.UserSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class TransportistaService {

    @Autowired
    private TransportistaRepository repository;

    @Autowired
    private UserSecurityService userSecurityService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public Transportista crearTransportista(Transportista t) {
        Long idUsuarioLogueado = userSecurityService.getCurrentUserId();

        // --- 1. VALIDACIONES ---
        if (repository.existsByCui(t.getCui())) {
            throw new BusinessException("Ya existe un transportista registrado con el CUI " + t.getCui());
        }

        if (t.getFechaNacimiento() != null) {
            if (Period.between(t.getFechaNacimiento(), LocalDate.now()).getYears() < 18) {
                throw new BusinessException("El transportista es menor de edad");
            }
        }

        if (t.getFechaVencimientoLicencia() != null) {
            if (t.getFechaVencimientoLicencia().isBefore(LocalDate.now())) {
                throw new BusinessException("La licencia se encuentra vencida");
            }
        }

        // --- MANEJO AUTOMÁTICO DE LICENCIA (ID para Agricultor, Nombre para Beneficio) ---
        Integer idLicencia = null;
        String nombreLicencia = "No especificada";

        if (t.getTipoLicencia() != null) {
            try {
                idLicencia = Integer.parseInt(t.getTipoLicencia());

                // Consultamos el nombre a la BD usando el ID (asumiendo que tu tabla es agricultor.catalogos)
                // Si tu tabla de catálogos tiene otro nombre, cámbialo aquí:
                String sqlBusqueda = "SELECT nombre FROM agricultor.catalogos WHERE id = ?";
                nombreLicencia = jdbcTemplate.queryForObject(sqlBusqueda, String.class, idLicencia);

            } catch (Exception e) {
                // Si no es un número o no lo encuentra, dejamos el valor original
                nombreLicencia = t.getTipoLicencia();
            }
        }

        // --- 2. GUARDAR EN BENEFICIO (Guardamos el TEXTO recuperado) ---
        t.setCreadopor(1);
        t.setEstado(null);
        t.setTipoLicencia(nombreLicencia);
        Transportista guardado = repository.save(t);

        // --- 3. GUARDAR EN AGRICULTOR (Guardamos el ID numérico) ---
        String sql = "INSERT INTO agricultor.transportistas " +
                "(cui, nombrecompleto, fechanacimiento, tipolicencia, fechavencimientolicencia, estado, disponible, creadopor, eliminado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                t.getCui(),
                t.getNombreCompleto(),
                t.getFechaNacimiento(),
                idLicencia, // ID numérico
                t.getFechaVencimientoLicencia(),
                28,
                true,
                idUsuarioLogueado.intValue(),
                false
        );

        return guardado;
    }


    public List<Transportista> listarPorAgricultor() {
        // 1. Obtenemos el ID del usuario logueado
        Long idUsuarioLogueado = userSecurityService.getCurrentUserId();

        // 2. Consulta con doble JOIN:
        // c1 para el Estado, c2 para el Tipo de Licencia
        String sql = "SELECT t.cui, t.nombrecompleto, t.fechavencimientolicencia, t.disponible, " +
                "c1.nombre as nombre_estado, " +
                "c2.nombre as nombre_licencia " +
                "FROM agricultor.transportistas t " +
                "INNER JOIN agricultor.catalogos c1 ON t.estado = c1.id " +
                "INNER JOIN agricultor.catalogos c2 ON t.tipolicencia = c2.id " +
                "WHERE t.creadopor = ? AND t.eliminado = false";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Transportista transportista = new Transportista();

            transportista.setCui(rs.getString("cui"));
            transportista.setNombreCompleto(rs.getString("nombrecompleto"));
            transportista.setFechaVencimientoLicencia(rs.getObject("fechavencimientolicencia", LocalDate.class));

            // Asignamos los nombres descriptivos a los campos temporales (@Transient)
            transportista.setNombreEstado(rs.getString("nombre_estado"));
            transportista.setTipoLicencia(rs.getString("nombre_licencia")); // Aquí ya va "Pesada", "Tipo A", etc.

            return transportista;
        }, idUsuarioLogueado);
    }
}