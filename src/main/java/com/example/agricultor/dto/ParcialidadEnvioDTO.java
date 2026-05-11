package com.example.agricultor.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ParcialidadEnvioDTO {
    private String noCuenta;
    private String transporte;    // Placa
    private String transportista; // CUI
    private BigDecimal pesoParcial;

    // CAMBIO: De Integer a String para recibir el nombre del catálogo
    private String medida;

    private String nitAgricultor;
    private LocalDateTime fechaEnvio;
    private Integer idParcialidad;

    // Getters y Setters
    public String getNoCuenta() { return noCuenta; }
    public void setNoCuenta(String noCuenta) { this.noCuenta = noCuenta; }
    public String getTransporte() { return transporte; }
    public void setTransporte(String transporte) { this.transporte = transporte; }
    public String getTransportista() { return transportista; }
    public void setTransportista(String transportista) { this.transportista = transportista; }
    public BigDecimal getPesoParcial() { return pesoParcial; }
    public void setPesoParcial(BigDecimal pesoParcial) { this.pesoParcial = pesoParcial; }

    // Getter y Setter actualizados para String
    public String getMedida() { return medida; }
    public void setMedida(String medida) { this.medida = medida; }

    public String getNitAgricultor() { return nitAgricultor; }
    public void setNitAgricultor(String nitAgricultor) { this.nitAgricultor = nitAgricultor; }
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }
    public Integer getIdParcialidad() { return idParcialidad; }
    public void setIdParcialidad(Integer idParcialidad) { this.idParcialidad = idParcialidad; }
}