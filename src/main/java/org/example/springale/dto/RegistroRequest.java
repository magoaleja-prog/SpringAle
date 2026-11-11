package org.example.springale.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Registro publico de nuevos estudiantes. */
public record RegistroRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80)
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 80)
        String apellido,

        @NotBlank(message = "El documento es obligatorio")
        @Size(max = 30)
        String documento,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato valido")
        @Size(max = 120)
        String email,

        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 8, max = 80, message = "La contrasena debe tener entre 8 y 80 caracteres")
        String password,

        Integer semestre,

        Long semilleroId
) {
}
