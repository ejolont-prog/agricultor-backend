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
    private UserSecurityService userSecurity; // Inyectamos tu clase de seguridad

    public List<Pesaje> obtenerPesajesActivos() {
        // 1. Obtenemos el ID del perfil (que ya viene como Long de tu UserSecurityService)
        Long idPerfil = userSecurity.getUserSession().getIdPerfil();

        // 2. Verificación básica
        if (idPerfil == null) {
            return new java.util.ArrayList<>();
        }

        // 3. Ya no hay error porque Long = Long
        return repository.findByIdperfilagricultorAndEliminadoFalse(idPerfil);
    }

    public Pesaje guardarPesaje(Pesaje pesaje) {
        // 1. Obtenemos el contexto (ID Usuario e ID Perfil del token)
        UserSessionContext session = userSecurity.getUserSession();

        // 2. Seteamos los campos de relación con el usuario y perfil
        pesaje.setIdperfilagricultor(session.getIdPerfil());

        if (session.getIdUsuario() != null) {
            pesaje.setCreadopor(session.getIdUsuario().intValue());
            pesaje.setModificadopor(session.getIdUsuario().intValue());
        }

        // 3. Auditoría y estados iniciales
        pesaje.setFechacreacion(LocalDateTime.now());
        pesaje.setFechamodificacion(LocalDateTime.now());
        pesaje.setEliminado(false);

        // 4. Guardar en la base de datos
        return repository.save(pesaje);
    }
}