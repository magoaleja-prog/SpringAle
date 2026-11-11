package org.example.springale.dto;

import org.example.springale.entity.EntregaTarea;
import org.example.springale.entity.EstadoTarea;
import org.example.springale.entity.Tarea;

import java.time.LocalDateTime;

/** Vista de una tarea desde la perspectiva de un estudiante. */
public record TareaEstudianteResponse(
        Long tareaId,
        String titulo,
        String descripcion,
        String materia,
        String profesor,
        LocalDateTime fechaLimite,
        EstadoTarea estado,
        LocalDateTime fechaEntrega,
        String comentario,
        Double nota,
        boolean entregada
) {

    public static TareaEstudianteResponse desde(Tarea tarea, EntregaTarea entrega) {
        EstadoTarea estado = entrega != null
                ? entrega.getEstado()
                : (LocalDateTime.now().isAfter(tarea.getFechaLimite())
                    ? EstadoTarea.ATRASADA : EstadoTarea.PENDIENTE);

        return new TareaEstudianteResponse(
                tarea.getId(),
                tarea.getTitulo(),
                tarea.getDescripcion(),
                tarea.getMateria().getNombre(),
                tarea.getProfesor().getNombreCompleto(),
                tarea.getFechaLimite(),
                estado,
                entrega != null ? entrega.getFechaEntrega() : null,
                entrega != null ? entrega.getComentario() : null,
                entrega != null ? entrega.getNota() : null,
                entrega != null && entrega.getFechaEntrega() != null
        );
    }
}
