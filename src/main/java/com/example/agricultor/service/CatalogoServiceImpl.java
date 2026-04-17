package com.example.agricultor.service;

import com.example.agricultor.model.Catalogo;
import com.example.agricultor.repository.CatalogoRepository;
import com.example.agricultor.service.CatalogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CatalogoServiceImpl implements CatalogoService {

    @Autowired
    private CatalogoRepository catalogoRepository;

    @Override
    public List<Catalogo> findByPadre(Long idPadre) {
        // Este nombre debe coincidir con el de tu CatalogoRepository
        return catalogoRepository.findByIdCatalogoAndEstadoTrue(idPadre);
    }
}