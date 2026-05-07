package com.example.agricultor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransporteRequestDTO {
    private String placa;
    private Long idTipoPlaca;
    private Long idMarca;
    private Long idLinea;
    private Long idColor;
    private Integer idModelo;
    private String nitAgricultor;

    @JsonProperty("nombreMarca")
    private String nombreMarca;

    @JsonProperty("nombreLinea")
    private String nombreLinea;

    @JsonProperty("nombreColor")
    private String nombreColor;

}