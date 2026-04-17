package com.example.agricultor.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                if (jwtUtil.isTokenValid(token)) {
                    Claims claims = jwtUtil.getClaims(token);
                    String username = claims.getSubject();

                    // Creamos la autenticación con un rol genérico para asegurar acceso
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );

                    // IMPORTANTE: Establecer la autenticación en el contexto
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    System.out.println("DEBUG: Usuario autenticado correctamente: " + username);
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Error validando token: " + e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}