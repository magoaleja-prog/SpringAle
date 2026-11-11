package org.example.springale.dto;

import org.example.springale.entity.EstadoTutoria;
import org.example.springale.entity.Tutoria;

import java.time.LocalDate;

/** Representacion de una tutoria entre estudiantes. */
public record TutoriaResponse(
        Long id,
        String tema,
        String descripcion,
        Long materiaId,
        String materiaNombre,
        EstadoTutoria estado,
        LocalDate fechaSolicitud,
        LocalDate fechaTutoria,
        EstudianteResumen tutor,
        EstudianteResumen aprendiz,
        int totalMensajes,
        Double notaAntes
) {

    public static TutoriaResponse from(Tutoria tutoria) {
        return new TutoriaResponse(
                tutoria.getId(),
                tutoria.getTema(),
                tutoria.getDescripcion(),
                tutoria.getMateria().getId(),
                tutoria.getMateria().getNombre(),
                tutoria.getEstado(),
                tutoria.getFechaSolicitud(),
                tutoria.getFechaTutoria(),
                EstudianteResumen.from(tutoria.getEstudianteTutor()),
                EstudianteResumen.from(tutoria.getEstudianteAprendiz()),
                tutoria.getMensajes().size(),
                tutoria.getNotaAntes()
        );
    }
}
