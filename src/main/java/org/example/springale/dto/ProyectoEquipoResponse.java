package org.example.springale.dto;

import org.example.springale.entity.EstadoProyecto;
import org.example.springale.entity.ProyectoEquipo;

import java.time.LocalDateTime;
import java.util.List;

/** Representacion de un proyecto grupal y sus integrantes. */
public record ProyectoEquipoResponse(
        Long id,
        String nombre,
        String descripcion,
        Long materiaId,
        String materiaNombre,
        String profesorNombre,
        LocalDateTime fechaLimite,
        LocalDateTime fechaCreacion,
        EstadoProyecto estado,
        EstudianteResumen lider,
        List<EstudianteResumen> miembros,
        int totalAportes,
        Double calificacion,
        String comentarioProfesor,
        LocalDateTime fechaEntregaFinal,
        String entregaFinalContenido
) {

    public static ProyectoEquipoResponse from(ProyectoEquipo proyecto) {
        return new ProyectoEquipoResponse(
                proyecto.getId(),
                proyecto.getNombre(),
                proyecto.getDescripcion(),
                proyecto.getMateria().getId(),
                proyecto.getMateria().getNombre(),
                proyecto.getProfesor().getNombreCompleto(),
                proyecto.getFechaLimite(),
                proyecto.getFechaCreacion(),
                proyecto.getEstado(),
                EstudianteResumen.from(proyecto.getLider()),
                proyecto.getMiembros().stream().map(EstudianteResumen::from).toList(),
                proyecto.getAportes().size(),
                proyecto.getCalificacion(),
                proyecto.getComentarioProfesor(),
                proyecto.getFechaEntregaFinal(),
                proyecto.getEntregaFinalContenido()
        );
    }
}
