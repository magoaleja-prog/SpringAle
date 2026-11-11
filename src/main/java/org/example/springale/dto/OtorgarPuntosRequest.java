package org.example.springale.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.springale.entity.MotivoPuntos;

/** Otorgamiento manual de puntos por parte de un administrador. */
public record OtorgarPuntosRequest(
        @NotNull Long estudianteId,
        @NotNull MotivoPuntos motivo,
        @NotBlank String descripcion,
        Long materiaId
) {
}
