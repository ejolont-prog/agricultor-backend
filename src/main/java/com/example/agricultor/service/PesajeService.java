package com.example.agricultor.service;

import com.example.agricultor.dto.PesajeExternoDTO;
import com.example.agricultor.dto.RespuestaBeneficioDTO;
import com.example.agricultor.model.Pesaje;
import com.example.agricultor.model.UserSessionContext;
import com.example.agricultor.repository.PesajeRepository;
import com.example.agricultor.security.UserSecurityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate; // <--- NUEVA IMPORTACIÓN
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PesajeService {

    @Autowired
    private PesajeRepository repository;

    @Autowired
    private UserSecurityService userSecurity;

    @Autowired
    private com.example.agricultor.repository.CatalogoRepository catalogoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // <--- INYECTADO PARA WEBSOCKET

    @Value("${api.agricultor.key}")
    private String apiKey;

    public List<Pesaje> obtenerPesajesActivos() {
        Long idPerfil = userSecurity.getUserSession().getIdPerfil();
        if (idPerfil == null) return new java.util.ArrayList<>();

        List<Pesaje> listaPesajes = repository.findByIdperfilagricultorAndEliminadoFalse(idPerfil);
        var estadosCatalogo = catalogoRepository.findByIdcatalogo(12L);

        listaPesajes.forEach(pesaje -> {
            Long estadoId = (pesaje.getEstado() != null) ? pesaje.getEstado().longValue() : null;
            if (estadoId != null) {
                estadosCatalogo.stream()
                        .filter(cat -> cat.getId().equals(estadoId))
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

        // 1. Guardar localmente
        Pesaje guardado = repository.save(pesaje);

        // 2. Ejecutar proceso de envío asíncrono
        enviarAPuerto(guardado);

        return guardado;
    }

    private void enviarAPuerto(Pesaje pesaje) {
        try {
            // 3. Buscar el NIT y el nombre de la unidad de medida
            String sqlNit = "SELECT nit FROM agricultor.perfilagricultor WHERE idperfil = ?";
            String nit = jdbcTemplate.queryForObject(sqlNit, String.class, pesaje.getIdperfilagricultor());

            // Buscamos el nombre en la tabla catalogos usando el ID que viene en el pesaje
            String sqlUnidad = "SELECT nombre FROM agricultor.catalogos WHERE id = ?";
            String nombreUnidad = jdbcTemplate.queryForObject(sqlUnidad, String.class, pesaje.getIdunidadmedida());

            // 4. Construir el DTO de envío
            PesajeExternoDTO dto = new PesajeExternoDTO();
            dto.setNitagricultor(nit);
            dto.setPesototalesperado(pesaje.getPesototalestimado());
            dto.setIdPesaje(pesaje.getIdpesaje());
            // Agregamos el nombre que acabamos de consultar
            dto.setUnidadpeso(nombreUnidad);

            try {
                ObjectMapper mapper = new ObjectMapper();
                String jsonFormat = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto);
                System.out.println("📤 Enviando JSON a Beneficio:\n" + jsonFormat);
            } catch (Exception e) {
                System.err.println("Error al serializar DTO para log: " + e.getMessage());
            }

            // 5. Enviar por WebClient (Mantenemos toda tu lógica de suscripción)
            webClientBuilder.build()
                    .post()
                    .uri("http://localhost:8083/api/recepcion-pesaje/guardar-externo")
                    .header("X-API-KEY", apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(RespuestaBeneficioDTO.class)
                    .subscribe(
                            res -> {
                                System.out.println("✅ Beneficio respondió. Cuenta: " + res.getNocuenta());

                                // 1. ACTUALIZACIÓN EN BASE DE DATOS
                                actualizarPesajeLocal(res.getNocuenta(), res.getId());

                                // 2. MODIFICAMOS EL DTO PARA EL FRONTEND
                                res.setEstado(163L);
                                res.setNombreEstado("Cuenta Creada");

                                // 3. ACTUALIZACIÓN EN TIEMPO REAL (WEBSOCKET)
                                messagingTemplate.convertAndSend("/topic/actualizacion-pesaje", res);

                                System.out.println("🚀 Notificación enviada al socket con estado 163");
                            },
                            error -> {
                                System.err.println("❌ Error en Beneficio: " + error.getMessage());
                            }
                    );

        } catch (Exception e) {
            System.err.println("Error en el proceso: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void actualizarPesajeLocal(String noCuenta, Long idPesaje) {
        try {
            // Modificamos el SQL para actualizar también el campo estado
            String sql = "UPDATE agricultor.pesajes SET nocuenta = ?, estado = ? WHERE idpesaje = ?";

            // Ejecutamos la actualización pasando: noCuenta, el ID de estado (163) e idPesaje
            jdbcTemplate.update(sql, noCuenta, 163L, idPesaje);

            System.out.println("✅ Agricultor actualizado: Pesaje " + idPesaje +
                    " ahora tiene cuenta " + noCuenta + " y estado 163");

        } catch (Exception e) {
            System.err.println("❌ Error actualizando localmente: " + e.getMessage());
        }
    }
}