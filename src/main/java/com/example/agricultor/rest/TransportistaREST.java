package com.example.agricultor.rest;

import com.example.agricultor.model.Transportista;
import com.example.agricultor.service.TransportistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transportistas")
public class TransportistaREST {

    @Autowired
    private TransportistaService service;

    @GetMapping
    public ResponseEntity<List<Transportista>> listar(@RequestHeader("Authorization") String token) {
        // El token llega como "Bearer eyJhbG..."
        // El servicio ya se encarga de procesarlo.
        return ResponseEntity.ok(service.listarPorAgricultor(token));
    }

    @PostMapping
    public ResponseEntity<?> crearTransportista(
            @RequestBody Transportista transportista,
            @RequestHeader("Authorization") String token) {

        Transportista nuevo = service.crearTransportista(transportista, token);
        return ResponseEntity.ok(nuevo);
    }
}