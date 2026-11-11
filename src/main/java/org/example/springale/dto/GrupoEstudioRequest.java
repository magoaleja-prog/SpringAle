package org.example.springale.dto;

import java.util.Set;

public record GrupoEstudioRequest(
        String nombre,
        String descripcion,
        String tema,
        Set<Long> miembroIds
) {
}
