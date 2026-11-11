package org.example.springale.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Postulacion de un estudiante para ser tutor de una materia. */
public record PostulacionTutorRequest(
        @NotNull(message = "La materia es obligatoria") Long materiaId,
        @NotBlank(message = "La disponibilidad es obligatoria")
        @Size(max = 300)
        String disponibilidad
) {
}
