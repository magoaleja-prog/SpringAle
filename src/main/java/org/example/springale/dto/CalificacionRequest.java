package org.example.springale.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** Datos para registrar o actualizar una nota. */
public record CalificacionRequest(
        @NotNull(message = "El estudiante es obligatorio") Long estudianteId,
        @NotNull(message = "La materia es obligatoria") Long materiaId,
        @NotNull(message = "El periodo es obligatorio") Long periodoId,
        @NotBlank(message = "La evaluacion es obligatoria") @Size(max = 120) String evaluacion,
        @NotNull(message = "La nota es obligatoria")
        @DecimalMin(value = "0.0", message = "La nota minima es 0.0")
        @DecimalMax(value = "5.0", message = "La nota maxima es 5.0")
        Double nota,
        LocalDate fecha,
        @Size(max = 500) String observacion
) {
}
