package org.example.springale.dto;

import org.example.springale.entity.Rol;
import org.example.springale.entity.Usuario;

import java.time.LocalDateTime;

/** Representacion publica de un usuario (sin contrasena). */
public record UsuarioResponse(
        Long id,
        String nombre,
        String apellido,
        String email,
        Rol rol,
        boolean activo,
        LocalDateTime fechaRegistro
) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.isActivo(),
                usuario.getFechaRegistro()
        );
    }
}
