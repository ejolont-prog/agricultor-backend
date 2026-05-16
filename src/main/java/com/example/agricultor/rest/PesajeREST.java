package com.example.agricultor.rest;

import com.example.agricultor.model.Pesaje;
import com.example.agricultor.model.UserSessionContext;
import com.example.agricultor.service.PesajeService;
import com.example.agricultor.security.UserSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pesajes")
@CrossOrigin(origins = "*") // Importante para que Angular pueda conectar
public class PesajeREST {

    @Autowired
    private PesajeService pesajeService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserSecurityService userSecurityService; // Inyectamos el servicio de seguridad

    @GetMapping
    public ResponseEntity<List<Pesaje>> listar() {
        return ResponseEntity.ok(pesajeService.obtenerPesajesActivos());
    }

    @PostMapping("/guardar")
    public ResponseEntity<Pesaje> guardarPesaje(@RequestBody Pesaje pesaje) {
        // 1. Obtener datos de la sesión usando la instancia inyectada
        UserSessionContext session = userSecurityService.getUserSession();

        // 2. Seteamos datos de seguridad y auditoría extraídos del token/BD
        pesaje.setIdperfilagricultor(session.getIdPerfil());

        // Convertimos Long a Integer si tus campos en la entidad son Integer
        if (session.getIdUsuario() != null) {
            pesaje.setCreadopor(session.getIdUsuario().intValue());
            pesaje.setModificadopor(session.getIdUsuario().intValue());
        }

        // 3. Valores por defecto del sistema
        pesaje.setFechacreacion(LocalDateTime.now());
        pesaje.setFechamodificacion(LocalDateTime.now());
        pesaje.setEliminado(false);

        // 4. Guardamos a través del servicio
        Pesaje guardado = pesajeService.guardarPesaje(pesaje);

        return ResponseEntity.ok(guardado);
    }

    @PostMapping("/cuentas/actualizar-estado")
    public ResponseEntity<?> actualizarEstadoDesdeBeneficio(@RequestBody Map<String, Object> payload) {
        try {
            // 1. Extraemos los parámetros enviados desde el módulo del Beneficio
            if (payload.get("nocuenta") == null || payload.get("estado") == null) {
                return ResponseEntity.badRequest().body("{\"error\": \"Faltan parámetros obligatorios en el payload.\"}");
            }

            String nocuenta = payload.get("nocuenta").toString();
            Integer nuevoEstado = Integer.parseInt(payload.get("estado").toString());

            // 2. Sentencia SQL dirigida al esquema 'agricultor', tabla 'pesajes'
            // Modificamos el campo 'estado' basándonos en el 'nocuenta' recibido
            String sqlUpdate = "UPDATE agricultor.pesajes SET estado = ? WHERE nocuenta = ?";

            // 3. Ejecutamos la actualización de manera segura
            int filasAfectadas = jdbcTemplate.update(sqlUpdate, nuevoEstado, nocuenta);

            if (filasAfectadas == 0) {
                return ResponseEntity.status(404).body("{\"error\": \"No se encontró ningún pesaje con el No. Cuenta: " + nocuenta + " en el esquema agricultor.\"}");
            }

            return ResponseEntity.ok("{\"status\": \"Estado actualizado correctamente en Agricultor a [" + nuevoEstado + "]\"}");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\": \"Error al actualizar el estado en Agricultor: " + e.getMessage() + "\"}");
        }
    }
}