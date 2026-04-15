package com.example.agricultor.service;

import com.example.agricultor.model.Pesaje;
import com.example.agricultor.repository.PesajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PesajeService {

    @Autowired
    private PesajeRepository repository;

    public List<Pesaje> obtenerPesajesActivos() {
        return repository.findByEliminadoFalse();
    }

    public Pesaje guardarPesaje(Pesaje pesaje) {
        pesaje.setEliminado(false); // RN04 inicialización [cite: 104]
        return repository.save(pesaje);
    }
}