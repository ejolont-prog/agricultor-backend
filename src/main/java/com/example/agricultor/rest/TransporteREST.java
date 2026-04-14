package com.example.agricultor.rest;

import com.example.agricultor.model.Transporte;
import com.example.agricultor.service.TransporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transportes")
public class TransporteREST {

    @Autowired
    private TransporteService service;

    @GetMapping
    public ResponseEntity<List<Transporte>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @PostMapping
    public ResponseEntity<Transporte> crear(@RequestBody Transporte transporte) {
        return ResponseEntity.ok(service.guardar(transporte));
    }
}