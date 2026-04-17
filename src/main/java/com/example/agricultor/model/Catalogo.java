package com.example.agricultor.model;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "catalogos", schema = "agricultor")
public class Catalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Integer idcatalogo;
    private String detallecatalogo;
    private String abreviatura;

    // Constructor vacío (Obligatorio para JPA)
    public Catalogo() {
    }

    // Getters y Setters
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

    public Integer getIdcatalogo() {
        return idcatalogo;
    }

    public void setIdcatalogo(Integer idcatalogo) {
        this.idcatalogo = idcatalogo;
    }

    public String getDetallecatalogo() {
        return detallecatalogo;
    }

    public void setDetallecatalogo(String detallecatalogo) {
        this.detallecatalogo = detallecatalogo;
    }

    public String getAbreviatura() {
        return abreviatura;
    }

    public void setAbreviatura(String abreviatura) {
        this.nombre = abreviatura;
    }

    // Opcional: toString para debugging en consola
    @Override
    public String toString() {
        return "Catalogo{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", idcatalogo=" + idcatalogo +
                '}';
    }
}