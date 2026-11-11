package org.example.springale.dto;

import org.example.springale.entity.Estudiante;
import org.example.springale.entity.GrupoEstudio;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public record GrupoEstudioResponse(
        Long id,
        String nombre,
        String descripcion,
        String tema,
        LocalDate fechaCreacion,
        int totalMiembros,
        List<EstudianteResumen> miembros
) {

    public static GrupoEstudioResponse from(GrupoEstudio grupo) {
        List<EstudianteResumen> miembros = grupo.getMiembros().stream()
                .sorted(Comparator.comparing(Estudiante::getNombre).thenComparing(Estudiante::getApellido))
                .map(EstudianteResumen::from)
                .toList();

        return new GrupoEstudioResponse(
                grupo.getId(),
                grupo.getNombre(),
                grupo.getDescripcion(),
                grupo.getTema(),
                grupo.getFechaCreacion(),
                miembros.size(),
                miembros
        );
    }
}
