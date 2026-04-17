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

    @Column(name = "idcatalogo")
    private Long idCatalogo; // Este es el "9" que usaremos para licencias

    private String nombre;

    private String descripcion;

    private Boolean estado;
}