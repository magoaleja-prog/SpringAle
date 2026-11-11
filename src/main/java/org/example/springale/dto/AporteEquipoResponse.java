package org.example.springale.dto;

import org.example.springale.entity.AporteEquipo;
import org.example.springale.entity.TipoAporte;

import java.time.LocalDateTime;

/** Representacion de un aporte individual de equipo. */
public record AporteEquipoResponse(
        Long id,
        Long proyectoId,
        Long estudianteId,
        String estudianteNombre,
        TipoAporte tipo,
        String contenido,
        String comentario,
        LocalDateTime fecha
) {

    public static AporteEquipoResponse from(AporteEquipo aporte) {
        return new AporteEquipoResponse(
                aporte.getId(),
                aporte.getProyecto().getId(),
                aporte.getEstudiante().getId(),
                aporte.getEstudiante().getNombreCompleto(),
                aporte.getTipo(),
                aporte.getContenido(),
                aporte.getComentario(),
                aporte.getFecha()
        );
    }
}
