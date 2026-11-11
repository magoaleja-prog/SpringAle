package org.example.springale.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/** Datos para crear o actualizar una tarea. */
public record TareaRequest(
        @NotBlank(message = "El titulo es obligatorio") @Size(max = 150) String titulo,
        @NotBlank(message = "La descripcion es obligatoria") @Size(max = 1500) String descripcion,
        @NotNull(message = "La materia es obligatoria") Long materiaId,
        @NotNull(message = "La fecha limite es obligatoria")
        @Future(message = "La fecha limite debe ser futura")
        LocalDateTime fechaLimite
) {
}
