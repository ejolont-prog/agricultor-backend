package com.example.agricultor.rest;

import com.example.agricultor.dto.TransportistaRequestDTO;
import com.example.agricultor.exception.BusinessException;
import com.example.agricultor.model.Transportista;
import com.example.agricultor.service.TransportistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transportistas")
public class TransportistaREST {

    @Autowired
    private TransportistaService service;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar() {
        // Al devolver Map<String, Object>, Jackson (Spring) convertirá
        // automáticamente el resultado a un JSON que Angular entenderá.
        return ResponseEntity.ok(service.listarPorAgricultor());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody TransportistaRequestDTO dto) {
        try {
            // Llamamos al método del servicio que acabamos de actualizar
            Object resultado = service.crearTransportista(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } catch (BusinessException e) {
            // Errores de validación (CUI duplicado, menor de edad, etc.)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al crear transportista: " + e.getMessage());
        }
    }


    @GetMapping("/disponibles")
    public ResponseEntity<List<Transportista>> listarDisponibles() {
        return ResponseEntity.ok(service.listarDisponibles());
    }


    @PutMapping("/sincronizar-estado")
    public ResponseEntity<?> sincronizarEstado(
            @RequestBody Map<String, Object> payload) {

        try {

            String cui = (String) payload.get("cui");

            String nombreEstado =
                    (String) payload.get("nombreEstado");

            service.sincronizarEstadoDesdeBeneficio(
                    cui,
                    nombreEstado
            );

            return ResponseEntity.ok(
                    Map.of("status", "Sincronización exitosa")
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(500)
                    .body(e.getMessage());
        }
    }
}