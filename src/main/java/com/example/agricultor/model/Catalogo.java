package com.example.agricultor.model;

import jakarta.persistence.*;

@Entity
@Table(name = "catalogos", schema = "agricultor")
public class Catalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(name = "idcatalogo")
    private Long idcatalogo;

    private Boolean estado;

    // =========================================================================
    // GETTERS Y SETTERS
    // =========================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getIdcatalogo() {
        return idcatalogo;
    }

    public void setIdcatalogo(Long idcatalogo) {
        this.idcatalogo = idcatalogo;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}