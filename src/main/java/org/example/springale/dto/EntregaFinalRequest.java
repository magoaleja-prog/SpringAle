package org.example.springale.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Entrega final consolidada que realiza el lider del equipo. */
public record EntregaFinalRequest(
        @NotBlank(message = "El contenido de la entrega final es obligatorio")
        @Size(max = 2000)
        String contenido
) {
}
