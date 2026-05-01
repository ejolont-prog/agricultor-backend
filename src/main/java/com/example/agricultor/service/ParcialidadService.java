package com.example.agricultor.service;

import com.example.agricultor.model.Parcialidad;
import com.example.agricultor.repository.ParcialidadRepository;
import com.example.agricultor.repository.TransporteRepository;
import com.example.agricultor.repository.TransportistaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParcialidadService {

    @Autowired
    private ParcialidadRepository parcialidadRepository;

    @Autowired
    private TransporteRepository transporteRepository;

    @Autowired
    private TransportistaRepository transportistaRepository;

    @Transactional
    public Parcialidad guardarYActualizarDisponibilidad(Parcialidad parcialidad) {

        // 1. Guardamos la parcialidad en el esquema actual (agricultor)
        parcialidad.setEliminado(false);
        Parcialidad nuevaParcialidad = parcialidadRepository.save(parcialidad);

        // 2. Actualizamos el Transporte en el esquema 'beneficio' mediante SQL Nativo
        if (parcialidad.getIdtransporte() != null) {
            transporteRepository.marcarComoNoDisponible(parcialidad.getIdtransporte().longValue());
        }

        // 3. Actualizamos el Transportista en el esquema 'beneficio' mediante SQL Nativo
        if (parcialidad.getIdtransportista() != null) {
            transportistaRepository.marcarComoNoDisponible(parcialidad.getIdtransportista().longValue());
        }

        return nuevaParcialidad;
    }
}