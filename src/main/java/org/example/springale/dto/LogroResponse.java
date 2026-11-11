package org.example.springale.dto;

import org.example.springale.entity.Logro;

/** Logro simple del sistema de gamificacion. */
public record LogroResponse(
        Long id,
        String nombre,
        String descripcion,
        int puntosRequeridos,
        String icono,
        boolean desbloqueado
) {

    public static LogroResponse from(Logro logro, boolean desbloqueado) {
        return new LogroResponse(
                logro.getId(),
                logro.getNombre(),
                logro.getDescripcion(),
                logro.getPuntosRequeridos(),
                logro.getIcono(),
                desbloqueado
        );
    }
}
