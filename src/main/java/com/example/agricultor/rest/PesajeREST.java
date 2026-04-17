package com.example.agricultor.rest;

import com.example.agricultor.model.Pesaje;
import com.example.agricultor.model.UserSessionContext;
import com.example.agricultor.service.PesajeService;
import com.example.agricultor.security.UserSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/pesajes")
@CrossOrigin(origins = "*") // Importante para que Angular pueda conectar
public class PesajeREST {

    @Autowired
    private PesajeService pesajeService;

    @Autowired
    private UserSecurityService userSecurityService; // Inyectamos el servicio de seguridad

    @GetMapping
    public ResponseEntity<List<Pesaje>> listar() {
        return ResponseEntity.ok(pesajeService.obtenerPesajesActivos());
    }

    @PostMapping("/guardar")
    public ResponseEntity<Pesaje> guardarPesaje(@RequestBody Pesaje pesaje) {
        // 1. Obtener datos de la sesión usando la instancia inyectada
        UserSessionContext session = userSecurityService.getUserSession();

        // 2. Seteamos datos de seguridad y auditoría extraídos del token/BD
        pesaje.setIdperfilagricultor(session.getIdPerfil());

        // Convertimos Long a Integer si tus campos en la entidad son Integer
        if (session.getIdUsuario() != null) {
            pesaje.setCreadopor(session.getIdUsuario().intValue());
            pesaje.setModificadopor(session.getIdUsuario().intValue());
        }

        // 3. Valores por defecto del sistema
        pesaje.setFechacreacion(LocalDateTime.now());
        pesaje.setFechamodificacion(LocalDateTime.now());
        pesaje.setEliminado(false);

        // 4. Guardamos a través del servicio
        Pesaje guardado = pesajeService.guardarPesaje(pesaje);

        return ResponseEntity.ok(guardado);
    }
}