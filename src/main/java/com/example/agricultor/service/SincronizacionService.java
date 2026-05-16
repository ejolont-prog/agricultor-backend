package com.example.agricultor.service;

import com.example.agricultor.dto.RecibirEstadoDTO;
import com.example.agricultor.model.Catalogo;
import com.example.agricultor.model.Pesaje;
import com.example.agricultor.repository.CatalogoRepository;
import com.example.agricultor.repository.PesajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate; //
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SincronizacionService {

    @Autowired
    private CatalogoRepository catalogoRepository;

    @Autowired
    private PesajeRepository pesajeRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // 👈 INYECTADO PARA ACTUALIZAR LA VISTA EN TIEMPO REAL

    @Transactional
    public void actualizarEstadoDesdeBeneficio(RecibirEstadoDTO dto) {

        // 1. Buscamos el catálogo por el nombre recibido en el JSON enviado desde beneficio
        Catalogo catalogo = catalogoRepository.findByNombre(dto.getDetalleCatalogo())
                .orElseThrow(() -> new RuntimeException("No se encontró el estado '" + dto.getDetalleCatalogo() + "' en el catálogo."));

        Integer idEstadoNuevo = catalogo.getId().intValue();

        // 2. Buscamos el registro en la tabla pesajes mediante el campo nocuenta
        Pesaje pesaje = pesajeRepository.findByNocuenta(dto.getNoCuenta())
                .orElseThrow(() -> new RuntimeException("No se encontró ningún pesaje asociado a la cuenta: " + dto.getNoCuenta()));

        // 3. Actualizamos el campo estado localmente
        pesaje.setEstado(idEstadoNuevo);
        pesaje.setFechamodificacion(LocalDateTime.now());

        // Setear el nombre en el atributo temporal para que el Front lo dibuje directo
        pesaje.setNombreEstado(catalogo.getNombre());

        // 4. Guardamos los cambios físicos en la base de datos
        Pesaje pesajeActualizado = pesajeRepository.save(pesaje);

        // =========================================================================
        // 5. ¡WEBSOCKET EN ACCIÓN! Notificamos a la vista de Angular de inmediato
        // =========================================================================
        // Usamos el mismo canal/tópico que ya escucha tu frontend para no reescribir código allá
        messagingTemplate.convertAndSend("/topic/actualizacion-pesaje", pesajeActualizado);

        System.out.println("🚀 ¡Sincronización y WebSocket exitosos! Cuenta #" + dto.getNoCuenta() +
                " avanzó a estado [" + catalogo.getNombre() + "]");
    }
}