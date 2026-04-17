package com.example.agricultor.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pesajes", schema = "agricultor")
@Data
public class Pesaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idpesaje; // serial4

    @Column(length = 50)
    private String nocuenta; // varchar(50)

    @Column(precision = 10, scale = 2)
    private BigDecimal pesototalestimado; // numeric(10, 2)

    private Integer idunidadmedida; // int4

    private Integer cantidadparcialidades; // int4

    private Long idperfilagricultor; // int4

    private Integer estado; // int4

    // Auditoría y Control
    private Integer creadopor; // int4

    private Integer modificadopor; // int4

    private LocalDateTime fechacreacion; // timestamp

    private LocalDateTime fechamodificacion; // timestamp

    private Boolean eliminado; // bool
}