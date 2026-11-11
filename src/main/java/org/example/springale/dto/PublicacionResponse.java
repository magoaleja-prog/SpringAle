package org.example.springale.dto;

import org.example.springale.entity.Publicacion;
import org.example.springale.entity.TipoPublicacion;

import java.time.LocalDateTime;

public record PublicacionResponse(
        Long id,
        String titulo,
        String contenido,
        TipoPublicacion tipo,
        LocalDateTime fecha,
        Long autorId,
        String autorNombre,
        Long grupoId,
        String grupoNombre
) {

    public static PublicacionResponse from(Publicacion publicacion) {
        return new PublicacionResponse(
                publicacion.getId(),
                publicacion.getTitulo(),
                publicacion.getContenido(),
                publicacion.getTipo(),
                publicacion.getFecha(),
                publicacion.getAutor().getId(),
                publicacion.getAutor().getNombre() + " " + publicacion.getAutor().getApellido(),
                publicacion.getGrupo().getId(),
                publicacion.getGrupo().getNombre()
        );
    }
}
