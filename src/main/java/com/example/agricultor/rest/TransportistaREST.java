package com.example.agricultor.rest;

import com.example.agricultor.exception.BusinessException;
import com.example.agricultor.model.Transportista;
import com.example.agricultor.service.TransportistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transportistas")
public class TransportistaREST {

    @Autowired
    private TransportistaService service;

    @GetMapping
    public ResponseEntity<List<Transportista>> listar() {
        // Ahora que el service no pide el token, esto ya no saldrá en rojo
        return ResponseEntity.ok(service.listarPorAgricultor());
    }

    @PostMapping
    public ResponseEntity<?> crearTransportista(@RequestBody Transportista transportista) {
        try {
            Transportista nuevo = service.crearTransportista(transportista);
            return ResponseEntity.ok(nuevo);
        } catch (BusinessException e) {
            // Esto envía el texto exacto ("El transportista es menor de edad", etc.)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Error genérico por si falla la base de datos
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error técnico: " + e.getMessage());
        }
    }
}