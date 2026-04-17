package com.example.agricultor.service;

import com.example.agricultor.model.Transportista;
import com.example.agricultor.repository.TransportistaRepository;
import com.example.agricultor.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransportistaService {

    @Autowired
    private TransportistaRepository repository;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Obtiene la lista de transportistas basada en el token del agricultor logueado.
     */
    public List<Transportista> listarPorAgricultor(String token) {
        // 1. Extraemos el idUsuario del Claim que configuramos en el JwtUtil
        Long idUsuario = jwtUtil.getUserIdFromToken(token);

        // 2. Convertimos a Integer ya que tu base de datos usa Integer para creadopor
        Integer idCreador = idUsuario.intValue();

        // 3. Retornamos solo lo que le pertenece a ese ID
        return repository.findByCreadoporAndEliminadoFalse(idCreador);
    }

    public Transportista crearTransportista(Transportista t, String token) {

        // 1. Obtener usuario del token
        Long idUsuario = jwtUtil.getUserIdFromToken(token);

        // 2. Setear campos automáticos
        t.setCreadopor(idUsuario.intValue());
        t.setEstado(28); // como tú definiste
        t.setDisponible(true);
        t.setEliminado(false);
        t.setFechaCreacion(java.time.LocalDateTime.now());

        // 3. Guardar en BD
        return repository.save(t);
    }
}