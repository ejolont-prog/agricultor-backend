package com.example.agricultor.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
        String path = request.getServletPath();

        // 1. OMITIR VALIDACIÓN PARA RUTAS PÚBLICAS (Login, Swagger, etc.)
        // Agregué /api/auth/ porque es donde suele estar el login
        if (path.contains("/swagger-ui") ||
                path.contains("/v3/api-docs") ||
                path.contains("/public") ||
                path.startsWith("/api/auth/")) {

            filterChain.doFilter(request, response);
            return;
        }

        // 2. VALIDACIÓN DEL TOKEN
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                if (jwtUtil.isTokenValid(token)) {
                    Claims claims = jwtUtil.getClaims(token);
                    String username = claims.getSubject();

                    // Log de depuración
                    System.out.println("--- TOKEN VÁLIDO PARA: " + username + " ---");

                    // Crear autenticación (Sin roles por ahora, pero con contexto)
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            org.springframework.security.core.authority.AuthorityUtils.createAuthorityList("ROLE_USER")
                    );

                    SecurityContextHolder.getContext().setAuthentication(auth);

                    // IMPORTANTE: Continuar la cadena y salir del método
                    filterChain.doFilter(request, response);
                    return;
                }
            } catch (Exception e) {
                System.out.println("--- ERROR JWT: " + e.getMessage() + " ---");
            }
        }

        // 3. SI NO HAY TOKEN O ES INVÁLIDO (Y NO ES RUTA PÚBLICA)
        // Solo enviamos error si realmente la ruta requiere autenticación
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\": \"Token inválido o ausente\", \"path\": \"" + path + "\"}");
    }
}