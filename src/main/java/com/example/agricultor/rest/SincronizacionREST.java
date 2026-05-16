package com.example.agricultor.rest;

import com.example.agricultor.dto.RecibirEstadoDTO;
import com.example.agricultor.service.SincronizacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.agricultor.dto.NotificacionEstadoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/externo")
public class SincronizacionREST {

    @Autowired
    private SincronizacionService sincronizacionService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/actualizar-estado")
    public ResponseEntity<?> recibirCambioEstado(@RequestBody RecibirEstadoDTO dto) {
        // 🔍 LOG DE CONTROL: Esto te pintará en la consola de Agricultor si la petición entró y qué trae
        System.out.println("====== PETICIÓN RECIBIDA DESDE BENEFICIO ======");
        System.out.println("noCuenta recibido: " + dto.getNoCuenta());
        System.out.println("detalleCatalogo recibido: " + dto.getDetalleCatalogo());
        System.out.println("===============================================");

        try {
            sincronizacionService.actualizarEstadoDesdeBeneficio(dto);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("mensaje", "Estado del pesaje actualizado correctamente en el módulo de Agricultor.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error procesando la sincronización: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/actualizar-parcialidad")
    public ResponseEntity<?> sincronizarEstado(@RequestBody NotificacionEstadoDTO dto) {
        try {
            // Evaluamos a qué ID de catálogo pasarlo en el entorno de Agricultor
            // 139 para ACEPTADO, 140 para RECHAZADO
            int nuevoEstadoId = dto.getResultado().equals("ACEPTADO") ? 139 : 140;

            // 🚨 CORREGIDO: Cambiamos "idpesaje" por "idparcialidad" en el WHERE para usar la llave primaria real
            String sql = "UPDATE agricultor.parcialidades SET estadoparcialidad = ? WHERE idparcialidad = ?";

            // Convertimos el String a Integer o Long si tu noparcialidad viene como texto numérico
            int idClave = Integer.parseInt(dto.getNoparcialidad());

            int filasAfectadas = jdbcTemplate.update(sql, nuevoEstadoId, idClave);

            if (filasAfectadas > 0) {
                return ResponseEntity.ok("{\"mensaje\": \"Estado sincronizado en Agricultor con éxito\"}");
            } else {
                return ResponseEntity.badRequest().body("{\"error\": \"No se encontró la parcialidad número: " + dto.getNoparcialidad() + "\"}");
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"El formato del campo noparcialidad no es un número válido: " + dto.getNoparcialidad() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\": \"Error al sincronizar: " + e.getMessage() + "\"}");
        }
    }
}