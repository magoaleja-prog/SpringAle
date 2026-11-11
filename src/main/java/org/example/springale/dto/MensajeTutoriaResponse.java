package org.example.springale.dto;

import org.example.springale.entity.MensajeTutoria;

import java.time.LocalDateTime;

/** Representacion de un mensaje de tutoria. */
public record MensajeTutoriaResponse(
        Long id,
        Long tutoriaId,
        Long autorId,
        String autorNombre,
        String contenido,
        LocalDateTime fecha
) {

    public static MensajeTutoriaResponse from(MensajeTutoria mensaje) {
        return new MensajeTutoriaResponse(
                mensaje.getId(),
                mensaje.getTutoria().getId(),
                mensaje.getAutor().getId(),
                mensaje.getAutor().getNombreCompleto(),
                mensaje.getContenido(),
                mensaje.getFecha()
        );
    }
}
