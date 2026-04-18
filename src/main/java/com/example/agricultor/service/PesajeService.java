package com.example.agricultor.service;

import com.example.agricultor.model.Pesaje;
import com.example.agricultor.model.UserSessionContext;
import com.example.agricultor.repository.PesajeRepository;
import com.example.agricultor.security.UserSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class PesajeService {

    @Autowired
    private PesajeRepository repository;

    @Autowired
    private UserSecurityService userSecurity;

    @Autowired
    private com.example.agricultor.repository.CatalogoRepository catalogoRepository; // <--- Inyecta el repo de catálogos

    public List<Pesaje> obtenerPesajesActivos() {
        Long idPerfil = userSecurity.getUserSession().getIdPerfil();e

        if (idPerfil == null) {
            return new java.util.ArrayList<>();
        }

        List<Pesaje> listaPesajes = repository.findByIdperfilagricultorAndEliminadoFalse(idPerfil);

        // Usamos 12L para asegurar que sea Long
        var estadosCatalogo = catalogoRepository.findByIdcatalogo(12L);

        listaPesajes.forEach(pesaje -> {
            // Obtenemos el valor del estado como Long para comparar correctamente
            Long estadoId = (pesaje.getEstado() != null) ? pesaje.getEstado().longValue() : null;

            if (estadoId != null) {
                estadosCatalogo.stream()
                        .filter(cat -> cat.getId().equals(estadoId)) // Aquí comparamos Long con Long
                        .findFirst()
                        .ifPresent(cat -> pesaje.setNombreEstado(cat.getNombre()));
            }
        });

        return listaPesajes;
    }

    public Pesaje guardarPesaje(Pesaje pesaje) {
        UserSessionContext session = userSecurity.getUserSession();
        pesaje.setIdperfilagricultor(session.getIdPerfil());

        if (session.getIdUsuario() != null) {
            pesaje.setCreadopor(session.getIdUsuario().intValue());
            pesaje.setModificadopor(session.getIdUsuario().intValue());
        }

        pesaje.setFechacreacion(LocalDateTime.now());
        pesaje.setFechamodificacion(LocalDateTime.now());
        pesaje.setEliminado(false);

        // Al guardar, JPA devuelve el objeto.
        // Podrías también buscar el nombre del estado aquí si quieres que el front lo vea apenas cree el registro.
        return repository.save(pesaje);
    }
}