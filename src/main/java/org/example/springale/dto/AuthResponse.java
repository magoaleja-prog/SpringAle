package org.example.springale.dto;

import org.example.springale.entity.Rol;

/** Respuesta de autenticacion con el token y los datos basicos del usuario. */
public record AuthResponse(
        String token,
        String tipo,
        Long usuarioId,
        String nombreCompleto,
        String email,
        Rol rol,
        Long estudianteId,
        long expiraEnMs
) {
}
