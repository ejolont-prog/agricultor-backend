package com.example.agricultor.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "transportes", schema = "agricultor")
@Data
public class Transporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idtransporte;

    private String placa;
    private Integer marca;
    private Integer color;
    private Integer linea;
    private Integer modelo;
    private Integer estado;
    private Boolean disponible;

    // Auditoría
    private Integer creadopor;
    private Integer modificadopor;
    private LocalDateTime fechacreacion;
    private LocalDateTime fechamodificacion;
    private Boolean eliminado;
}