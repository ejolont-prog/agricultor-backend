package com.example.agricultor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "parcialidades", schema = "agricultor")
public class Parcialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idparcialidad;

    private Integer idpesaje;

    // --- RELACIÓN CON TRANSPORTE ---
    @Column(name = "idtransporte")
    private Integer idtransporte;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idtransporte", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Transporte transporte;

    // --- RELACIÓN CON TRANSPORTISTA ---
    @Column(name = "idtransportista")
    private Integer idtransportista;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idtransportista", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Transportista transportista;

    @Column(name = "pesoestimadoparcialidad", precision = 10, scale = 2)
    private BigDecimal pesoestimadoparcialidad;

    private Integer estadoparcialidad;

    @Column(columnDefinition = "TEXT")
    private String textorechazado;

    @Column(columnDefinition = "TEXT")
    private String qr;

    @Column(precision = 10, scale = 2)
    private BigDecimal faltante;

    @Column(precision = 10, scale = 2)
    private BigDecimal sobrante;

    @Column(name = "fechacreacion", insertable = false, updatable = false)
    private LocalDateTime fechacreacion;

    private Integer creadopor;
    private Integer modificadopor;

    @Column(name = "fechamodificacion")
    private LocalDateTime fechamodificacion;

    private Boolean eliminado = false;

    @Column(name = "tipomedida", length = 50)
    private String tipomedida;
}