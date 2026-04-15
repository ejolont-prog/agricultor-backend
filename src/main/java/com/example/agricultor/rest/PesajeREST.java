package com.example.agricultor.rest;

import com.example.agricultor.model.Pesaje;
import com.example.agricultor.service.PesajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pesajes")
public class PesajeREST {

    @Autowired
    private PesajeService service;

    // Endpoint para cargar la tabla principal del agricultor [cite: 94]
    @GetMapping
    public ResponseEntity<List<Pesaje>> listar() {
        return ResponseEntity.ok(service.obtenerPesajesActivos());
    }

    // Endpoint para el formulario de "Crear Pesaje" [cite: 102, 112]
    @PostMapping
    public ResponseEntity<Pesaje> crear(@RequestBody Pesaje pesaje) {
        return ResponseEntity.ok(service.guardarPesaje(pesaje));
    }
}