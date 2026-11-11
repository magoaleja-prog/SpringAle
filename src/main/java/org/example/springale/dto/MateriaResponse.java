package org.example.springale.dto;

import org.example.springale.entity.Materia;

/** Representacion de una materia con datos utiles para el cliente. */
public record MateriaResponse(
        Long id,
        String codigo,
        String nombre,
        String descripcion,
        Long profesorId,
        String profesorNombre,
        int totalEstudiantes
) {

    public static MateriaResponse from(Materia materia) {
        return new MateriaResponse(
                materia.getId(),
                materia.getCodigo(),
                materia.getNombre(),
                materia.getDescripcion(),
                materia.getProfesor() != null ? materia.getProfesor().getId() : null,
                materia.getProfesor() != null ? materia.getProfesor().getNombreCompleto() : null,
                materia.getEstudiantes().size()
        );
    }
}
