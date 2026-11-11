package org.example.springale.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Datos para crear o actualizar una materia. */
public record MateriaRequest(
        @NotBlank @Size(max = 20) String codigo,
        @NotBlank @Size(max = 100) String nombre,
        @Size(max = 500) String descripcion,
        Long profesorId
) {
}
