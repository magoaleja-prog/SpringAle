package org.example.springale.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Mensaje simple dentro del chat de una tutoria aceptada. */
public record MensajeTutoriaRequest(
        @NotBlank(message = "El mensaje no puede estar vacio")
        @Size(max = 1000)
        String contenido
) {
}
