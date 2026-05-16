package com.example.agricultor.rest;

import com.example.agricultor.dto.RecibirEstadoDTO;
import com.example.agricultor.service.SincronizacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/externo")
public class SincronizacionREST {

    @Autowired
    private SincronizacionService sincronizacionService;

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
}