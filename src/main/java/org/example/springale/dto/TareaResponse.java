package org.example.springale.dto;

import org.example.springale.entity.Tarea;

import java.time.LocalDateTime;

/** Representacion de una tarea creada por un profesor. */
public record TareaResponse(
        Long id,
        String titulo,
        String descripcion,
        Long materiaId,
        String materiaNombre,
        Long profesorId,
        String profesorNombre,
        LocalDateTime fechaLimite,
        LocalDateTime fechaCreacion,
        boolean activa
) {

    public static TareaResponse from(Tarea tarea) {
        return new TareaResponse(
                tarea.getId(),
                tarea.getTitulo(),
                tarea.getDescripcion(),
                tarea.getMateria().getId(),
                tarea.getMateria().getNombre(),
                tarea.getProfesor().getId(),
                tarea.getProfesor().getNombreCompleto(),
                tarea.getFechaLimite(),
                tarea.getFechaCreacion(),
                tarea.isActiva()
        );
    }
}
