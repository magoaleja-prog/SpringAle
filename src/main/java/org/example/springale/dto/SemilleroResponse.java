package org.example.springale.dto;

import org.example.springale.entity.Semillero;

public record SemilleroResponse(Long id, String nombre, String descripcion, long totalEstudiantes) {

    public static SemilleroResponse from(Semillero semillero, long totalEstudiantes) {
        return new SemilleroResponse(
                semillero.getId(),
                semillero.getNombre(),
                semillero.getDescripcion(),
                totalEstudiantes
        );
    }
}
