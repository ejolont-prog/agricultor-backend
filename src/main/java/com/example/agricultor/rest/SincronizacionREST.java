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
            // 1. Evaluamos a qué ID de catálogo pasarlo en el entorno de Agricultor (139 o 140)
            int nuevoEstadoId = dto.getResultado().equals("ACEPTADO") ? 139 : 140;

            // Convertimos el idparcialidad entrante a número entero (serial4/int4)
            int idClave = Integer.parseInt(dto.getNoparcialidad());

            // 2. 🔍 OBTENER EL idpesaje ASOCIADO: Buscamos en parcialidades usando la columna exacta de la imagen
            String sqlBuscarPesaje = "SELECT idpesaje FROM agricultor.parcialidades WHERE idparcialidad = ? LIMIT 1";
            Integer idPesajeAsociado = jdbcTemplate.queryForObject(sqlBuscarPesaje, Integer.class, idClave);

            // 3. 🔄 ACTUALIZAR PARCIALIDAD: Usamos "estadoparcialidad" e "idparcialidad" exactos de tu tabla
            String sqlParcialidad = "UPDATE agricultor.parcialidades SET estadoparcialidad = ? WHERE idparcialidad = ?";
            int filasAfectadas = jdbcTemplate.update(sqlParcialidad, nuevoEstadoId, idClave);

            if (filasAfectadas > 0) {

                // 4. 🚀 ACTUALIZAR TABLA PESAJES: Si encontramos el idpesaje padre, cambiamos su campo "estado" al ID 165
                if (idPesajeAsociado != null) {
                    String sqlPesajePadre = "UPDATE agricultor.pesajes SET estado = 165 WHERE idpesaje = ?";
                    jdbcTemplate.update(sqlPesajePadre, idPesajeAsociado);
                    System.out.println(" Sincronización exitosa: Tabla pesajes (idpesaje: " + idPesajeAsociado + ") actualizada al estado 165");
                }

                return ResponseEntity.ok("{\"mensaje\": \"Estado de parcialidad y pesaje principal sincronizados con éxito\"}");
            } else {
                return ResponseEntity.badRequest().body("{\"error\": \"No se encontró la parcialidad número: " + dto.getNoparcialidad() + "\"}");
            }

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"El formato del campo noparcialidad no es un número válido: " + dto.getNoparcialidad() + "\"}");
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"No se encontró un registro de pesaje asociado para la parcialidad proporcionada.\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\": \"Error al sincronizar en Agricultor: " + e.getMessage() + "\"}");
        }
    }
}