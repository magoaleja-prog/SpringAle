package org.example.springale.dto;

/** Posicion de un estudiante en la tabla general o por materia. */
public record RankingResponse(
        int posicion,
        Long estudianteId,
        String estudianteNombre,
        int puntos,
        String nivel
) {
}
