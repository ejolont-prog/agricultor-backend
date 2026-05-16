package com.example.agricultor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecibirEstadoDTO {

    @JsonProperty("noCuenta")
    private String noCuenta;

    @JsonProperty("detalleCatalogo")
    private String detalleCatalogo;

    public RecibirEstadoDTO() {
    }

    public RecibirEstadoDTO(String noCuenta, String detalleCatalogo) {
        this.noCuenta = noCuenta;
        this.detalleCatalogo = detalleCatalogo;
    }

    // Getters y Setters
    public String getNoCuenta() { return noCuenta; }
    public void setNoCuenta(String noCuenta) { this.noCuenta = noCuenta; }
    public String getDetalleCatalogo() { return detalleCatalogo; }
    public void setDetalleCatalogo(String detalleCatalogo) { this.detalleCatalogo = detalleCatalogo; }
}