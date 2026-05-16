package com.example.agricultor.rest;

import com.example.agricultor.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*") // Permite que Angular se conecte sin errores de CORS
public class UsuarioREST {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Obtiene los roles para llenar el select del formulario
     * Endpoint: GET http://localhost:8081/api/usuarios/roles
     */
    @GetMapping("/roles")
    public ResponseEntity<?> getRoles() {
        try {
            // Tu consulta específica
            String sql = "SELECT id, nombre FROM agricultor.catalogos WHERE idcatalogo = 2";

            List<Map<String, Object>> roles = jdbcTemplate.queryForList(sql);
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener roles: " + e.getMessage());
        }
    }

 
    @PostMapping("/crear-sincronizado")
    public ResponseEntity<?> crearSincronizado(@RequestBody Map<String, Object> data) {
        try {
            usuarioService.crearUsuarioSincronizado(data);
            return ResponseEntity.ok(Map.of("message", "Agricultor creado y sincronizado con éxito"));
        } catch (Exception e) {
            // Retornamos el mensaje de error para que mat-snack-bar lo muestre
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
