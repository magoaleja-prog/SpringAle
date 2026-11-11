package org.example.springale.dto;

import org.example.springale.entity.TipoPublicacion;

public record PublicacionRequest(
        String titulo,
        String contenido,
        TipoPublicacion tipo,
        Long autorId,
        Long grupoId
) {
}
