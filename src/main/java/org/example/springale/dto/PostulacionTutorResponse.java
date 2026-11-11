package org.example.springale.dto;

import org.example.springale.entity.EstadoPostulacion;
import org.example.springale.entity.PostulacionTutor;

import java.time.LocalDate;

/** Representacion de una postulacion de tutor. */
public record PostulacionTutorResponse(
        Long id,
        Long estudianteId,
        String estudianteNombre,
        Long materiaId,
        String materiaNombre,
        String disponibilidad,
        EstadoPostulacion estado,
        LocalDate fechaPostulacion
) {

    public static PostulacionTutorResponse from(PostulacionTutor postulacion) {
        return new PostulacionTutorResponse(
                postulacion.getId(),
                postulacion.getEstudiante().getId(),
                postulacion.getEstudiante().getNombreCompleto(),
                postulacion.getMateria().getId(),
                postulacion.getMateria().getNombre(),
                postulacion.getDisponibilidad(),
                postulacion.getEstado(),
                postulacion.getFechaPostulacion()
        );
    }
}
