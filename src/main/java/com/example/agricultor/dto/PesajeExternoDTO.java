package com.example.agricultor.dto;

import java.math.BigDecimal;

public class PesajeExternoDTO {
    private String nitagricultor;
    private BigDecimal pesototalesperado;
    private Long idPesaje;

    // Constructor vacío (necesario para la serialización de JSON)
    public PesajeExternoDTO() {
    }

    // Getters y Setters corregidos para BigDecimal
    public String getNitagricultor() {
        return nitagricultor;
    }

    public void setNitagricultor(String nitagricultor) {
        this.nitagricultor = nitagricultor;
    }

    public BigDecimal getPesototalesperado() {
        return pesototalesperado;
    }

    public void setPesototalesperado(BigDecimal pesototalesperado) {
        this.pesototalesperado = pesototalesperado;
    }

    public Long getIdPesaje() {
        return idPesaje;
    }

    public void setIdPesaje(Long idPesaje) {
        this.idPesaje = idPesaje;
    }
}