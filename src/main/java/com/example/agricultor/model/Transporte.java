package com.example.agricultor.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// En com.example.agricultor.model.Transporte
@Entity
@Table(name = "transportes", schema = "agricultor") // Cambiado a agricultor
@Data
public class Transporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idtransporte;

    private String placa;

    // Cambiados a Integer para coincidir con los IDs de la BD (FKs)
    private Integer marca;
    private Integer color;
    private Integer linea;
    private Integer tipoplaca;

    private String modelo;
    private Integer estado;
    private Integer creadopor;
    private Boolean disponible = true;

    @Column(name = "fechacreacion", updatable = false)
    private LocalDateTime fechaCreacion;
}