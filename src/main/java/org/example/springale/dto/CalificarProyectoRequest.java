package org.example.springale.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Calificacion y comentario que registra el profesor sobre el trabajo completo. */
public record CalificarProyectoRequest(
        @NotNull @DecimalMin("0.0") @DecimalMax("5.0") Double calificacion,
        @Size(max = 1000) String comentario
) {
}
