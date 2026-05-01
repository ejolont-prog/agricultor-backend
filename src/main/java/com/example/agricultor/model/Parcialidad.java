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


    // Getters y Setters Manuales

    public Integer getIdparcialidad() {
        return idparcialidad;
    }

    public void setIdparcialidad(Integer idparcialidad) {
        this.idparcialidad = idparcialidad;
    }

    public Integer getIdpesaje() {
        return idpesaje;
    }

    public void setIdpesaje(Integer idpesaje) {
        this.idpesaje = idpesaje;
    }

    public Integer getIdtransporte() {
        return idtransporte;
    }

    public void setIdtransporte(Integer idtransporte) {
        this.idtransporte = idtransporte;
    }

    public Transporte getTransporte() {
        return transporte;
    }

    public void setTransporte(Transporte transporte) {
        this.transporte = transporte;
    }

    public Integer getIdtransportista() {
        return idtransportista;
    }

    public void setIdtransportista(Integer idtransportista) {
        this.idtransportista = idtransportista;
    }

    public Transportista getTransportista() {
        return transportista;
    }

    public void setTransportista(Transportista transportista) {
        this.transportista = transportista;
    }

    public BigDecimal getPesoestimadoparcialidad() {
        return pesoestimadoparcialidad;
    }

    public void setPesoestimadoparcialidad(BigDecimal pesoestimadoparcialidad) {
        this.pesoestimadoparcialidad = pesoestimadoparcialidad;
    }

    public Integer getEstadoparcialidad() {
        return estadoparcialidad;
    }

    public void setEstadoparcialidad(Integer estadoparcialidad) {
        this.estadoparcialidad = estadoparcialidad;
    }

    public String getTextorechazado() {
        return textorechazado;
    }

    public void setTextorechazado(String textorechazado) {
        this.textorechazado = textorechazado;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public BigDecimal getFaltante() {
        return faltante;
    }

    public void setFaltante(BigDecimal faltante) {
        this.faltante = faltante;
    }

    public BigDecimal getSobrante() {
        return sobrante;
    }

    public void setSobrante(BigDecimal sobrante) {
        this.sobrante = sobrante;
    }

    public LocalDateTime getFechacreacion() {
        return fechacreacion;
    }

    public void setFechacreacion(LocalDateTime fechacreacion) {
        this.fechacreacion = fechacreacion;
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

    public String getTipomedida() {
        return tipomedida;
    }

    public void setTipomedida(String tipomedida) {
        this.tipomedida = tipomedida;
    }
}