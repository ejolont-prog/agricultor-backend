package com.example.agricultor.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ParcialidadConPlacaProjection {
    Integer getIdparcialidad();
    Integer getIdpesaje();
    Integer getIdtransporte();
    String getPlaca();
    Integer getIdtransportista();
    String getNombrecompleto();
    String getTipomedida();
    Integer getEstadoparcialidad();
    BigDecimal getPesoestimadoparcialidad();
    String getTextorechazado();
    String getQr();
    BigDecimal getFaltante();
    BigDecimal getSobrante();
    LocalDateTime getFechacreacion();
    Integer getCreadopor();
    Integer getModificadopor();
    LocalDateTime getFechamodificacion();
    Boolean getEliminado();

    String getNombreEstado();
}