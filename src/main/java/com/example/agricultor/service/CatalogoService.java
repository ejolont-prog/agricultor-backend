package com.example.agricultor.service;

import com.example.agricultor.model.Catalogo;
import java.util.List;

public interface CatalogoService {
    List<Catalogo> findByPadre(Long idPadre);
}