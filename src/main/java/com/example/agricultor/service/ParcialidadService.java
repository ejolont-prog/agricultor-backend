package com.example.agricultor.service;

import com.example.agricultor.dto.ParcialidadEnvioDTO;
import com.example.agricultor.model.Parcialidad;
import com.example.agricultor.model.UserSessionContext;
import com.example.agricultor.repository.ParcialidadRepository;
import com.example.agricultor.repository.PesajeRepository;
import com.example.agricultor.repository.TransporteRepository;
import com.example.agricultor.repository.TransportistaRepository;
import com.example.agricultor.security.UserSecurityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ParcialidadService {

    @Autowired
    private ParcialidadRepository parcialidadRepository;

    @Autowired
    private PesajeRepository pesajeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TransporteRepository transporteRepository;

    @Autowired
    private TransportistaRepository transportistaRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${api.agricultor.key}")
    private String apiKey;

    @Autowired
    private UserSecurityService userSecurity;

    @Transactional
    public Parcialidad guardarYActualizarDisponibilidad(Parcialidad parcialidad) {
        UserSessionContext session = userSecurity.getUserSession();

        if (session != null && session.getIdUsuario() != null) {
            parcialidad.setCreadopor(session.getIdUsuario().intValue());
            parcialidad.setModificadopor(session.getIdUsuario().intValue());
        }


        parcialidad.setEstadoparcialidad(137);

        parcialidad.setEliminado(false);
        Parcialidad nuevaParcialidad = parcialidadRepository.save(parcialidad);

        if (parcialidad.getIdpesaje() != null) {
            pesajeRepository.incrementarContadorParcialidades(parcialidad.getIdpesaje());
        }

        if (parcialidad.getIdtransporte() != null) {
            transporteRepository.marcarComoNoDisponible(parcialidad.getIdtransporte().longValue());
        }

        if (parcialidad.getIdtransportista() != null) {
            transportistaRepository.marcarComoNoDisponible(parcialidad.getIdtransportista().longValue());
        }

        enviarParcialidadABeneficio(nuevaParcialidad);

        return nuevaParcialidad;
    }

    private void enviarParcialidadABeneficio(Parcialidad p) {
        try {
            // SQL Nativo para obtener NoCuenta y NIT
            String sqlInfoBase = "SELECT p.nocuenta, perf.nit " +
                    "FROM agricultor.pesajes p " +
                    "JOIN agricultor.perfilagricultor perf ON p.idperfilagricultor = perf.idperfil " +
                    "WHERE p.idpesaje = ?";

            Map<String, Object> infoBase = jdbcTemplate.queryForMap(sqlInfoBase, p.getIdpesaje());
            String noCuenta = (String) infoBase.get("nocuenta");
            String nit = (String) infoBase.get("nit");

            // SQL para Placa
            String sqlPlaca = "SELECT placa FROM agricultor.transportes WHERE idtransporte = ?";
            String placa = jdbcTemplate.queryForObject(sqlPlaca, String.class, p.getIdtransporte());

            // SQL para CUI
            String sqlCui = "SELECT cui FROM agricultor.transportistas WHERE idtransportista = ?";
            String cui = jdbcTemplate.queryForObject(sqlCui, String.class, p.getIdtransportista());

            // Lógica de Medida (Nombre o ID)
            String nombreMedida;
            try {
                Integer idMedida = Integer.parseInt(p.getTipomedida());
                String sqlMedida = "SELECT nombre FROM agricultor.catalogos WHERE id = ?";
                nombreMedida = jdbcTemplate.queryForObject(sqlMedida, String.class, idMedida);
            } catch (Exception e) {
                nombreMedida = p.getTipomedida();
            }

            // Construcción del DTO
            ParcialidadEnvioDTO dto = new ParcialidadEnvioDTO();
            dto.setIdParcialidad(p.getIdparcialidad());
            dto.setNoCuenta(noCuenta);
            dto.setTransporte(placa);
            dto.setTransportista(cui);
            dto.setPesoParcial(p.getPesoestimadoparcialidad());
            dto.setMedida(nombreMedida);
            dto.setNitAgricultor(nit);
            dto.setFechaEnvio(LocalDateTime.now());

            // Log del JSON que se enviará
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            System.out.println("📤 Enviando Parcialidad a Beneficio:\n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto));

            // LLAMADA AL BENEFICIO USANDO WEBCLIENT
            webClientBuilder.build()
                    .post()
                    .uri("http://localhost:8083/api/recepcion-parcialidad/guardar-externo")
                    .header("X-API-KEY", apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(
                            res -> System.out.println("✅ Beneficio confirmó recepción: " + res),
                            err -> System.err.println("❌ Error enviando parcialidad a Beneficio: " + err.getMessage())
                    );

        } catch (Exception e) {
            System.err.println("❌ Error preparando envío de parcialidad: " + e.getMessage());
        }
    }
}