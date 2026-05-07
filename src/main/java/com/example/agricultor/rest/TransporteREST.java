package com.example.agricultor.rest;

import com.example.agricultor.dto.TransporteRequestDTO;
import com.example.agricultor.exception.BusinessException;
import com.example.agricultor.model.Transporte;
import com.example.agricultor.model.Transportista;
import com.example.agricultor.service.TransporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/transportes")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4500"}, allowedHeaders = "*")
public class TransporteREST {

    @Autowired
    private TransporteService service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/crear")
    public ResponseEntity<?> crear(@RequestBody TransporteRequestDTO payload) {
        try {
            return ResponseEntity.ok(service.crearTransporte(payload));
        } catch (BusinessException e) {
            // Retornamos el mensaje específico de nuestra excepción de negocio
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Para errores inesperados, imprimimos en consola para que TÚ lo veas
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error interno: " + e.getMessage());
        }
    }

    @GetMapping("/mis-transportes")
    public ResponseEntity<List<Map<String, Object>>> listar() {
        return ResponseEntity.ok(service.listarTransportesPorUsuario());
    }

    // ENDPOINT PARA LA CASCADA DE CATÁLOGOS
    @GetMapping("/catalogos-jerarquia")
    public ResponseEntity<List<Map<String, Object>>> getCatalogos(
            @RequestParam Integer idCatalogo,
            @RequestParam(required = false) Integer transicion) {

        String sql = "SELECT id, nombre FROM agricultor.catalogos WHERE idcatalogo = ?";
        if (transicion != null) {
            sql += " AND transicion = " + transicion;
            return ResponseEntity.ok(jdbcTemplate.queryForList(sql, idCatalogo));
        }
        return ResponseEntity.ok(jdbcTemplate.queryForList(sql, idCatalogo));
    }



    @GetMapping("/disponibles")
    public ResponseEntity<List<Transporte>> listarDisponibles() {
        return ResponseEntity.ok(service.listarDisponibles());
    }

    @PutMapping("/sincronizar-estado")
    public ResponseEntity<?> sincronizarEstado(@RequestBody Map<String, Object> payload) {
        try {
            // Obtenemos los valores del mapa enviado por Beneficio
            String placa = (String) payload.get("placa");
            String nombreEstado = (String) payload.get("nombreEstado");

            if (placa == null || nombreEstado == null) {
                return ResponseEntity.badRequest().body("Datos incompletos: placa o nombreEstado nulos.");
            }

            service.sincronizarEstadoDesdeBeneficio(placa, nombreEstado);

            return ResponseEntity.ok().body(Map.of("status", "Sincronización exitosa en Agricultor"));
        } catch (Exception e) {
            e.printStackTrace(); // Esto te mostrará el error real en la consola de IntelliJ
            return ResponseEntity.status(500).body("Error en Agricultor: " + e.getMessage());
        }
    }
}