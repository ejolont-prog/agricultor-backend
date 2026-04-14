package com.example.agricultor.rest;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.agricultor.model.Estado;
import com.example.agricultor.service.EstadoService;

@RestController
@RequestMapping ("/estados/")
@CrossOrigin(origins = "*")
public class EstadoREST {

    @Autowired
    private EstadoService estadoService;

    @GetMapping ("{id}")
    private ResponseEntity<List<Estado>> getAllEstadosByPais (@PathVariable("id") Long idPais){
        return ResponseEntity.ok(estadoService.findAllByCountry(idPais));

    }

}
