package org.example.springale.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Datos para crear o actualizar un estudiante.
 * El usuario asociado es opcional: si no llega, el servicio puede crearlo.
 */
public record EstudianteRequest(
        @NotBlank(message = "El documento es obligatorio")
        @Size(max = 30)
        String documento,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80)
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 80)
        String apellido,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato valido")
        @Size(max = 120)
        String email,

        @NotNull(message = "El semestre es obligatorio")
        @Min(value = 1, message = "El semestre minimo es 1")
        @Max(value = 12, message = "El semestre maximo es 12")
        Integer semestre,

        Long semilleroId,

        Long usuarioId
) {
}
