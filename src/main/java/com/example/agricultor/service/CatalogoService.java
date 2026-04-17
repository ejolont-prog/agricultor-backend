package com.example.agricultor.service;

import com.example.agricultor.model.Catalogo;
import com.example.agricultor.repository.CatalogoRepository;
import com.example.agricultor.repository.TransporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CatalogoService {
    @Autowired
    private CatalogoRepository repository;

    private static final Integer ID_UNIDADES_MEDIDA = 11;
    private static final Integer ID_ESTADOS_PESAJES = 12;

    public List<Catalogo> obtenerUnidadesMedida() {
        return repository.findByIdcatalogo(ID_UNIDADES_MEDIDA);
    }

    public List<Catalogo> obtenerEstadosPesajes() {
        return repository.findByIdcatalogo(ID_ESTADOS_PESAJES);
    }
}