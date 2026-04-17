package com.example.agricultor.service;

import com.example.agricultor.model.Pesaje;
import com.example.agricultor.model.UserSessionContext;
import com.example.agricultor.repository.PesajeRepository;
import com.example.agricultor.security.UserSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        // 1. Obtenemos el contexto (ID Usuario e ID Perfil)
        UserSessionContext session = userSecurity.getUserSession();

    /*
        pesaje.setIdusuario(session.getIdUsuario()); // Quién lo creó
        pesaje.setIdperfil(session.getIdPerfil());   // A qué perfil pertenece
*/
        pesaje.setEliminado(false);

        return repository.save(pesaje);
    }
}