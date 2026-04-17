package com.example.agricultor.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transportistas", schema = "agricultor")
@Data
public class Transportista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idtransportista;

    private String cui;

    @Column(name = "nombrecompleto")
    private String nombreCompleto;

    @Column(name = "fechanacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "tipolicencia")
    private Integer tipoLicencia;

    @Column(name = "fechavencimientolicencia")
    private LocalDate fechaVencimientoLicencia;

    private Integer estado;

    private Boolean disponible;

    // Relación con el usuario que lo creó
    @Column(name = "creadorpor")
    private Integer creadopor;

    @Column(name = "fechacreacion")
    private LocalDateTime fechaCreacion;

    private Boolean eliminado;
}