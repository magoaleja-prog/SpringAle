package org.example.springale.dto;

import org.example.springale.entity.PeriodoAcademico;

import java.time.LocalDate;

/** Representacion de un periodo academico. */
public record PeriodoResponse(
        Long id,
        String nombre,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        boolean activo
) {

    public static PeriodoResponse from(PeriodoAcademico periodo) {
        return new PeriodoResponse(
                periodo.getId(),
                periodo.getNombre(),
                periodo.getFechaInicio(),
                periodo.getFechaFin(),
                periodo.isActivo()
        );
    }
}
