package org.example.springale.dto;

import jakarta.validation.constraints.NotNull;
import org.example.springale.entity.EstadoTutoria;

/** Cambio de estado solicitado por el tutor o el aprendiz. */
public record CambiarEstadoTutoriaRequest(
        @NotNull(message = "El estado es obligatorio") EstadoTutoria estado
) {
}
