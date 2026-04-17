package com.example.agricultor.service;

import com.example.agricultor.model.Catalogo;
import com.example.agricultor.repository.CatalogoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CatalogoService {

    @Autowired
    private CatalogoRepository repository;

    private static final Long ID_UNIDADES_MEDIDA = 11L;
    private static final Long ID_ESTADOS_PESAJES = 12L;

    public List<Catalogo> findByPadre(Long idcatalogo) {
        return repository.findByIdcatalogo(idcatalogo);
    }

    public List<Catalogo> obtenerUnidadesMedida() {
        return repository.findByIdcatalogo(ID_UNIDADES_MEDIDA);
    }

    public List<Catalogo> obtenerEstadosPesajes() {
        return repository.findByIdcatalogo(ID_ESTADOS_PESAJES);
    }

    public List<Catalogo> obtenerActivosPorGrupo(Long idcatalogo) {
        return repository.findByIdcatalogoAndEstadoTrue(idcatalogo);
    }
}