package com.example.agricultor.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "transportes", schema = "beneficio")
@Data
public class Transporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idtransporte;

    private String placa;

    // Estos se guardarán como String (Nombre) en Beneficio
    private String marca;
    private String color;
    private String linea;
    private String modelo;

    private Integer estado; // Permitirá null por el comando SQL anterior

    @Column(name = "creadopor")
    private Integer creadopor;

    private Boolean disponible = true;

    @Column(name = "fechacreacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.disponible == null) this.disponible = true;
    }

    // --- CAMPOS PARA LA VISTA (No se guardan en beneficio.transportes) ---
    @Transient
    private String nombreEstado;

    @Transient
    private String tipoPlacaNombre; // Para el combo de tipos de placa



}