package org.example.springale.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Set;

/** Datos para crear un proyecto grupal. */
public record ProyectoEquipoRequest(
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 150) String nombre,
        @NotBlank(message = "La descripcion es obligatoria") @Size(max = 1500) String descripcion,
        @NotNull(message = "La materia es obligatoria") Long materiaId,
        @NotNull(message = "La fecha limite es obligatoria")
        @Future(message = "La fecha limite debe ser futura")
        LocalDateTime fechaLimite,
        @NotNull(message = "El lider es obligatorio") Long liderId,
        @NotEmpty(message = "Debe incluir al menos un integrante") Set<Long> miembrosIds
) {
}
