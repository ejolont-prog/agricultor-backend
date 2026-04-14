package com.example.agricultor.service;

import com.example.agricultor.model.Transporte;
import com.example.agricultor.repository.TransporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TransporteService {

    @Autowired
    private TransporteRepository repository;

    public List<Transporte> listarTodos() {
        return repository.findByEliminadoFalse();
    }

    public Transporte guardar(Transporte transporte) {
        transporte.setEliminado(false); // Por defecto no está eliminado
        return repository.save(transporte);
    }
}