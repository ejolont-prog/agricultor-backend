package com.example.agricultor.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pesajes", schema = "agricultor")
public class Pesaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idpesaje;

    @Column(length = 50)
    private String nocuenta;

    @Column(precision = 10, scale = 2)
    private BigDecimal pesototalestimado;

    private Integer idunidadmedida;

    private Integer cantidadparcialidades;

    private Long idperfilagricultor;

    private Integer estado;

    private Integer creadopor;

    private Integer modificadopor;

    private LocalDateTime fechacreacion;

    private LocalDateTime fechamodificacion;

    private Boolean eliminado;


    @Transient // Importante: no se mapea a la tabla pesaje
    private String nombreEstado;
    // Constructor vacío

    public Pesaje() {}

    // --- GETTERS Y SETTERS ---

    public Long getIdpesaje() {
        return idpesaje;
    }

    public void setIdpesaje(Long idpesaje) {
        this.idpesaje = idpesaje;
    }

    public String getNocuenta() {
        return nocuenta;
    }

    public void setNocuenta(String nocuenta) {
        this.nocuenta = nocuenta;
    }

    public BigDecimal getPesototalestimado() {
        return pesototalestimado;
    }

    public void setPesototalestimado(BigDecimal pesototalestimado) {
        this.pesototalestimado = pesototalestimado;
    }

    public Integer getIdunidadmedida() {
        return idunidadmedida;
    }

    public void setIdunidadmedida(Integer idunidadmedida) {
        this.idunidadmedida = idunidadmedida;
    }

    public Integer getCantidadparcialidades() {
        return cantidadparcialidades;
    }

    public void setCantidadparcialidades(Integer cantidadparcialidades) {
        this.cantidadparcialidades = cantidadparcialidades;
    }

    public Long getIdperfilagricultor() {
        return idperfilagricultor;
    }

    public void setIdperfilagricultor(Long idperfilagricultor) {
        this.idperfilagricultor = idperfilagricultor;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Integer getCreadopor() {
        return creadopor;
    }

    public void setCreadopor(Integer creadopor) {
        this.creadopor = creadopor;
    }

    public Integer getModificadopor() {
        return modificadopor;
    }

    public void setModificadopor(Integer modificadopor) {
        this.modificadopor = modificadopor;
    }

    public LocalDateTime getFechacreacion() {
        return fechacreacion;
    }

    public void setFechacreacion(LocalDateTime fechacreacion) {
        this.fechacreacion = fechacreacion;
    }

    public LocalDateTime getFechamodificacion() {
        return fechamodificacion;
    }

    public void setFechamodificacion(LocalDateTime fechamodificacion) {
        this.fechamodificacion = fechamodificacion;
    }

    public Boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }
    // Agrega el getter y setter para nombreEstado
    public String getNombreEstado() { return nombreEstado; }
    public void setNombreEstado(String nombreEstado) { this.nombreEstado = nombreEstado; }
}