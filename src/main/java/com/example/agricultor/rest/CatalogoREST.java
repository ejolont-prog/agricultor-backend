package com.example.agricultor.rest;
import com.example.agricultor.service.CatalogoService;
import com.example.agricultor.model.Catalogo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/catalogos")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CatalogoREST {

    @Autowired
    private CatalogoService catalogoService;

    @GetMapping("/{idPadre}")
    public ResponseEntity<List<Catalogo>> getCatalogosPorPadre(@PathVariable Long idPadre) {
        return ResponseEntity.ok(catalogoService.findByPadre(idPadre));
    }
}