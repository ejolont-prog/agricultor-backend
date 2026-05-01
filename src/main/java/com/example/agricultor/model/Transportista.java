package com.example.agricultor.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transportistas", schema = "beneficio")
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
    private String tipoLicencia; // Se guarda como String en beneficio

    @Column(name = "fechavencimientolicencia")
    private LocalDate fechaVencimientoLicencia;

    private Integer estado; // Se guardará como NULL según pediste

    @Column(name = "creadopor")
    private Integer creadopor; // Se guardará como 1 siempre

    @Column(name = "fechacreacion", updatable = false)
    private LocalDateTime fechaCreacion;

    private Boolean eliminado = false;





    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (eliminado == null) eliminado = false;
    }

    @Transient
    private String nombreEstado;
}