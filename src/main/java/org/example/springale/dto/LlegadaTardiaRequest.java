package org.example.springale.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

/** Datos para registrar manualmente una llegada tardia (solo administrador). */
public record LlegadaTardiaRequest(
        @NotNull(message = "El estudiante es obligatorio") Long estudianteId,
        @NotNull(message = "La fecha es obligatoria") LocalDate fecha,
        @NotNull(message = "La hora es obligatoria") LocalTime hora,
        @Size(max = 500) String observacion
) {
}
