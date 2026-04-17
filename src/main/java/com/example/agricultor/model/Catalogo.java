package com.example.agricultor.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "catalogos", schema = "agricultor")
@Data
public class Catalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(name = "idcatalogo")
    private Long idcatalogo;

    private Boolean estado;
}