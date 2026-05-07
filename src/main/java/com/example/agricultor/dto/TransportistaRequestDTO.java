
package com.example.agricultor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TransportistaRequestDTO {
    private String cui;
    private String nitAgricultor;

    @JsonProperty("nombreCompleto")
    private String nombreCompleto;

    @JsonProperty("fechaNacimiento")
    private LocalDate fechaNacimiento;

    @JsonProperty("idTipoLicencia")
    private Long idTipoLicencia;

    @JsonProperty("nombreTipoLicencia")
    private String nombreTipoLicencia;

    @JsonProperty("fechaVencimientoLicencia")
    private LocalDate fechaVencimientoLicencia;
}