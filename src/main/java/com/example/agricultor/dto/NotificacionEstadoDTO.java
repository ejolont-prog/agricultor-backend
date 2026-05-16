package com.example.agricultor.dto;

public class NotificacionEstadoDTO {
    private String noparcialidad;
    private String resultado; // "ACEPTADO" o "RECHAZADO"

    // Constructor vacío obligatorio para Jackson
    public NotificacionEstadoDTO() {}

    public NotificacionEstadoDTO(String noparcialidad, String resultado) {
        this.noparcialidad = noparcialidad;
        this.resultado = resultado;
    }

    // Getters y Setters
    public String getNoparcialidad() { return noparcialidad; }
    public void setString(String noparcialidad) { this.noparcialidad = noparcialidad; }
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
}