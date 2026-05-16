package com.example.agricultor.service;

import com.example.agricultor.dto.RecibirEstadoDTO;
import com.example.agricultor.model.Catalogo;
import com.example.agricultor.model.Pesaje;
import com.example.agricultor.repository.CatalogoRepository;
import com.example.agricultor.repository.PesajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SincronizacionService {

    @Autowired
    private CatalogoRepository catalogoRepository;

    @Autowired
    private PesajeRepository pesajeRepository;

    @Transactional
    public void actualizarEstadoDesdeBeneficio(RecibirEstadoDTO dto) {

        // 1. Buscamos el catálogo por el nombre recibido en el JSON
        Catalogo catalogo = catalogoRepository.findByNombre(dto.getDetalleCatalogo())
                .orElseThrow(() -> new RuntimeException("No se encontró el estado '" + dto.getDetalleCatalogo() + "' en los catálogos del agricultor."));

        // Como el ID del catálogo es Long y tu campo estado en Pesaje es Integer, lo convertimos
        Integer idEstadoNuevo = catalogo.getId().intValue();

        // 2. Buscamos el registro en la tabla pesajes mediante el campo nocuenta
        Pesaje pesaje = pesajeRepository.findByNocuenta(dto.getNoCuenta())
                .orElseThrow(() -> new RuntimeException("No se encontró ningún pesaje asociado a la cuenta: " + dto.getNoCuenta()));

        // 3. Actualizamos el campo estado y la fecha de modificación
        pesaje.setEstado(idEstadoNuevo);
        pesaje.setFechamodificacion(LocalDateTime.now());

        // 4. Guardamos los cambios en la base de datos
        pesajeRepository.save(pesaje);

        System.out.println("Sincronización Completa: Pesaje de Cuenta #" + dto.getNoCuenta() + " actualizado al estado ID: " + idEstadoNuevo);
    }
}