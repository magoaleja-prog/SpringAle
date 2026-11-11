package org.example.springale.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/** Datos para crear o actualizar un periodo academico. */
public record PeriodoRequest(
        @NotBlank String nombre,
        @NotNull LocalDate fechaInicio,
        @NotNull LocalDate fechaFin,
        Boolean activo
) {
}
