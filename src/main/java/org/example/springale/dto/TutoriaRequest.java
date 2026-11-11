package org.example.springale.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** Datos para solicitar una tutoria a un companero tutor. */
public record TutoriaRequest(
        @NotNull(message = "El tutor es obligatorio") Long tutorId,
        @NotNull(message = "La materia es obligatoria") Long materiaId,
        @NotBlank(message = "El tema es obligatorio") @Size(max = 100) String tema,
        @Size(max = 600) String descripcion,
        @FutureOrPresent(message = "La fecha de la tutoria no puede ser pasada")
        LocalDate fechaTutoria
) {
}
