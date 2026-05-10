package com.example.agricultor.dto;

public class RespuestaBeneficioDTO {
    private String nocuenta;
    private Long id;
    // Nuevos campos para la actualización en tiempo real
    private Long estado;
    private String nombreEstado;

    // Getters y Setters actuales
    public String getNocuenta() { return nocuenta; }
    public void setNocuenta(String nocuenta) { this.nocuenta = nocuenta; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // NUEVOS Getters y Setters
    public Long getEstado() { return estado; }
    public void setEstado(Long estado) { this.estado = estado; }

    public String getNombreEstado() { return nombreEstado; }
    public void setNombreEstado(String nombreEstado) { this.nombreEstado = nombreEstado; }
}