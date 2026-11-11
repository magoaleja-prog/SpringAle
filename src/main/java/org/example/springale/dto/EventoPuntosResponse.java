package org.example.springale.dto;

import org.example.springale.entity.EventoPuntos;
import org.example.springale.entity.MotivoPuntos;

import java.time.LocalDateTime;

/** Movimiento de puntos ganados por un estudiante. */
public record EventoPuntosResponse(
        Long id,
        Long estudianteId,
        MotivoPuntos motivo,
        int puntos,
        String descripcion,
        LocalDateTime fecha,
        Long materiaId,
        String materiaNombre
) {

    public static EventoPuntosResponse from(EventoPuntos evento) {
        return new EventoPuntosResponse(
                evento.getId(),
                evento.getEstudiante().getId(),
                evento.getMotivo(),
                evento.getPuntos(),
                evento.getDescripcion(),
                evento.getFecha(),
                evento.getMateria() != null ? evento.getMateria().getId() : null,
                evento.getMateria() != null ? evento.getMateria().getNombre() : null
        );
    }
}
