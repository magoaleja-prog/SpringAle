package org.example.springale.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.springale.entity.Rol;

/** Datos para que un administrador cree o actualice usuarios. */
public record UsuarioRequest(
        @NotBlank @Size(max = 80) String nombre,
        @NotBlank @Size(max = 80) String apellido,
        @NotBlank @Email @Size(max = 120) String email,
        @Size(min = 8, max = 80) String password,
        @NotNull Rol rol,
        Boolean activo
) {
}
