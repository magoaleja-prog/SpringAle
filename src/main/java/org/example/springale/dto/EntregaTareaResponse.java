package org.example.springale.dto;

import org.example.springale.entity.EntregaTarea;
import org.example.springale.entity.EstadoTarea;

import java.time.LocalDateTime;

/** Representacion de la entrega individual de una tarea. */
public record EntregaTareaResponse(
        Long id,
        Long tareaId,
        Long estudianteId,
        String estudianteNombre,
        EstadoTarea estado,
        LocalDateTime fechaEntrega,
        String comentario,
        Double nota
) {

    public static EntregaTareaResponse from(EntregaTarea entrega) {
        return new EntregaTareaResponse(
                entrega.getId(),
                entrega.getTarea().getId(),
                entrega.getEstudiante().getId(),
                entrega.getEstudiante().getNombreCompleto(),
                entrega.getEstado(),
                entrega.getFechaEntrega(),
                entrega.getComentario(),
                entrega.getNota()
        );
    }
}
