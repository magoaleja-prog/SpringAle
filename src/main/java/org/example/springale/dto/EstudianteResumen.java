package org.example.springale.dto;

import org.example.springale.entity.Estudiante;

/** Resumen minimo de estudiante usado en listas anidadas. */
public record EstudianteResumen(
        Long id,
        String nombreCompleto,
        String documento,
        String email
) {

    public static EstudianteResumen from(Estudiante estudiante) {
        return new EstudianteResumen(
                estudiante.getId(),
                estudiante.getNombreCompleto(),
                estudiante.getDocumento(),
                estudiante.getEmail()
        );
    }
}
