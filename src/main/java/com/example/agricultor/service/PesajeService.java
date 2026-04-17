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

        // 1. Obtienes la lista de pesajes (que solo traen el ID en el campo 'estado')
        List<Pesaje> listaPesajes = repository.findByIdperfilagricultorAndEliminadoFalse(idPerfil);

        // 2. Traes la lista de descripciones del catálogo (ID 12 es Estados Pesaje)
        var estadosCatalogo = catalogoRepository.findByIdcatalogo(12);

        // 3. Cruzamos los datos: por cada pesaje, buscamos su nombre en el catálogo
        listaPesajes.forEach(pesaje -> {
            estadosCatalogo.stream()
                    .filter(cat -> cat.getId().equals(pesaje.getEstado().longValue()))
                    .findFirst()
                    .ifPresent(cat -> pesaje.setNombreEstado(cat.getNombre())); // Llenamos el campo @Transient
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