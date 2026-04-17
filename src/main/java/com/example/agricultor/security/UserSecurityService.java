package com.example.agricultor.security;

import com.example.agricultor.model.UserSessionContext; // Importas tu modelo
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserSecurityService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Método para sacar solo el ID del token (útil para cosas rápidas)
    public Long getCurrentUserId() {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return jwtUtil.getUserIdFromToken(authHeader);
        }
        throw new RuntimeException("No se encontró token");
    }

    // TRAER ID Y IDPERFIL
    public UserSessionContext getUserSession() {
        Long idUsuario = getCurrentUserId();

        // Consultamos el idperfil basado en tu diagrama de BD
        String sql = "SELECT idperfil FROM agricultor.usuario WHERE idusuario = ?";

        try {
            Long idPerfil = jdbcTemplate.queryForObject(sql, Long.class, idUsuario);

            // Retornamos tu modelo con la data completa
            return new UserSessionContext(idUsuario, idPerfil);
        } catch (Exception e) {
            // Si el usuario no tiene perfil, devolvemos el ID y null en perfil
            return new UserSessionContext(idUsuario, null);
        }
    }
}