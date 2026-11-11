package org.example.springale.dto;

import org.example.springale.entity.Estudiante;

/** Representacion publica de un estudiante. */
public record EstudianteResponse(
        Long id,
        String documento,
        String nombre,
        String apellido,
        String email,
        Integer semestre,
        Long usuarioId,
        Long semilleroId,
        String semilleroNombre
) {

    public static EstudianteResponse from(Estudiante estudiante) {
        return new EstudianteResponse(
                estudiante.getId(),
                estudiante.getDocumento(),
                estudiante.getNombre(),
                estudiante.getApellido(),
                estudiante.getEmail(),
                estudiante.getSemestre(),
                estudiante.getUsuario() != null ? estudiante.getUsuario().getId() : null,
                estudiante.getSemillero() != null ? estudiante.getSemillero().getId() : null,
                estudiante.getSemillero() != null ? estudiante.getSemillero().getNombre() : null
        );
    }
}
