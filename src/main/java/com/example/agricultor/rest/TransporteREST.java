package com.example.agricultor.rest;

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
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> payload) {
        try {
            // Pasamos el Map al servicio para que él extraiga los valores manualmente
            return ResponseEntity.ok(service.crearTransporte(payload));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
}