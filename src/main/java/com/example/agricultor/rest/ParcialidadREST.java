package com.example.agricultor.rest;

import com.example.agricultor.model.Parcialidad;
import com.example.agricultor.repository.ParcialidadRepository;
import com.example.agricultor.service.ParcialidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parcialidades")
@CrossOrigin(origins = "*")
public class ParcialidadREST {

    @Autowired
    private ParcialidadRepository parcialidadRepository;

    @Autowired
    private ParcialidadService parcialidadService;
    // Crear Parcialidad (Flujo Básico)
    @PostMapping("/crear")
    public ResponseEntity<Parcialidad> crearParcialidad(@RequestBody Parcialidad parcialidad) {
        try {
            Parcialidad resultado = parcialidadService.guardarYActualizarDisponibilidad(parcialidad);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Listar por Pesaje (Para el flujo FA01: Ver Detalle)
    @GetMapping("/pesaje/{idpesaje}")
    public List<Parcialidad> listarPorPesaje(@PathVariable Integer idpesaje) {
        return parcialidadRepository.findByIdpesajeAndEliminadoFalse(idpesaje);
    }

    // Obtener una sola para ver detalle individual
    @GetMapping("/{id}")
    public ResponseEntity<Parcialidad> obtenerPorId(@PathVariable Integer id) {
        return parcialidadRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}