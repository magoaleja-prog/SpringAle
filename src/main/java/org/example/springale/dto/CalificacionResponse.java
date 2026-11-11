package org.example.springale.dto;

import org.example.springale.entity.Calificacion;

import java.time.LocalDate;

/** Representacion de una calificacion con los nombres de sus relaciones. */
public record CalificacionResponse(
        Long id,
        Long estudianteId,
        String estudianteNombre,
        Long materiaId,
        String materiaNombre,
        Long periodoId,
        String periodoNombre,
        String profesorNombre,
        String evaluacion,
        Double nota,
        LocalDate fecha,
        String observacion
) {

    public static CalificacionResponse from(Calificacion calificacion) {
        return new CalificacionResponse(
                calificacion.getId(),
                calificacion.getEstudiante().getId(),
                calificacion.getEstudiante().getNombreCompleto(),
                calificacion.getMateria().getId(),
                calificacion.getMateria().getNombre(),
                calificacion.getPeriodo().getId(),
                calificacion.getPeriodo().getNombre(),
                calificacion.getProfesor().getNombreCompleto(),
                calificacion.getEvaluacion(),
                calificacion.getNota(),
                calificacion.getFecha(),
                calificacion.getObservacion()
        );
    }
}
