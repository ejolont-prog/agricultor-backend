package com.example.agricultor.service;

import com.example.agricultor.security.UserSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

@Service
public class UsuarioService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserSecurityService userSecurityService;

    // 1. INYECTAMOS TU NUEVA CLASE REUTILIZABLE
    @Autowired
    private EmailService emailService;

    @Transactional
    public void crearUsuarioSincronizado(Map<String, Object> data) {
        String urlBeneficio = "http://localhost:8083/api/sincronizar/crear-desde-agricultor";

        // 1. INTENTAR REGISTRO EN BENEFICIO PRIMERO
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(urlBeneficio, data, Map.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Beneficio rechazó la creación del agricultor.");
            }
        } catch (HttpStatusCodeException e) {
            try {
                Map<?, ?> errorMap = new ObjectMapper().readValue(e.getResponseBodyAsString(), Map.class);
                throw new RuntimeException((String) errorMap.get("error"));
            } catch (StringIndexOutOfBoundsException | com.fasterxml.jackson.core.JsonProcessingException ex) {
                throw new RuntimeException("Error en validación de Beneficio: " + e.getResponseBodyAsString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error de comunicación o rechazo en Beneficio: " + e.getMessage());
        }

        // 2. SI BENEFICIO ACEPTÓ, CONTINUAR CON EL INSERT LOCAL EN AGRICULTOR
        Long idUsuarioLogueado = userSecurityService.getCurrentUserId();

        String sql = "INSERT INTO agricultor.usuario (" +
                "usuario, contrasena, idrol, correo, nit, " +
                "estado, idperfil, creadopor, eliminado) " +
                "VALUES (?, ?, ?, ?, ?, 15, 1, ?, false)";

        try {
            jdbcTemplate.update(sql,
                    data.get("usuario"),
                    data.get("contrasena"),
                    data.get("idrol"),
                    data.get("correo"),
                    data.get("nit"),
                    idUsuarioLogueado
            );
        } catch (Exception e) {
            throw new RuntimeException("Error al insertar el usuario localmente en Agricultor: " + e.getMessage());
        }

        // 3. LLAMADA LIMPIA Y REUTILIZABLE A TU SERVICIO DE CORREOS
        // Recuperamos las variables mapeadas directamente desde el JSON enviado por Angular
        String correoDestinatario = (String) data.get("correo");
        String nombreUsuario = (String) data.get("usuario");
        String passwordUsuario = (String) data.get("contrasena");

        String asunto = "¡Bienvenido al sistema, " + nombreUsuario + "!";

        String cuerpoMensaje = "Estimado Agricultor,\n\n" +
                "Se ha creado tu cuenta con éxito en el sistema de logística de café.\n\n" +
                "Tus credenciales de acceso son:\n" +
                "• Usuario: " + nombreUsuario + "\n" +
                "• Contraseña: " + passwordUsuario + "\n\n" +
                "Ya puedes ingresar a la plataforma.";

        // Invocamos el método común pasando únicamente los textos planos
        emailService.enviarCorreo(correoDestinatario, asunto, cuerpoMensaje);
    }
}