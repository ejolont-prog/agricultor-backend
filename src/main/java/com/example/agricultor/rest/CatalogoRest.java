package com.example.agricultor.rest;

import com.example.agricultor.model.Catalogo;
import com.example.agricultor.service.CatalogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogos")
@CrossOrigin(origins = "*")
public class CatalogoRest {

    @Autowired
    private CatalogoService catalogoService;

    @GetMapping("/unidades-medida")
    public List<Catalogo> getUnidadesMedida() {
        return catalogoService.obtenerUnidadesMedida();
    }

    @GetMapping("/estados-pesaje")
    public List<Catalogo> getEstadosPesaje() {
        return catalogoService.obtenerEstadosPesajes();
    }

    @GetMapping("/{id}")
    public List<Catalogo> getPorPadre(@PathVariable Long id) {
        return catalogoService.findByPadre(id);
    }
}