package com.example.agricultor.rest;

import com.example.agricultor.model.Catalogo; // Asegúrate de tener la entidad creada
import com.example.agricultor.repository.CatalogoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalogos")
public class CatalogoRest {

    @Autowired
    private CatalogoRepository catalogoRepository;

    // Obtener Unidades de Medida (idcatalogo = 11)
    @GetMapping("/unidades-medida")
    public List<Catalogo> getUnidadesMedida() {
        return catalogoRepository.findByIdcatalogo(11);
    }

    // Obtener Estados de Pesaje (idcatalogo = 12)
    @GetMapping("/estados-pesaje")
    public List<Catalogo> getEstadosPesaje() {
        return catalogoRepository.findByIdcatalogo(12);
    }
}