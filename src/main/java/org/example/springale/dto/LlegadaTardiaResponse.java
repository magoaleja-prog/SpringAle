package org.example.springale.dto;

import org.example.springale.entity.LlegadaTardia;
import org.example.springale.entity.MetodoRegistro;

import java.time.LocalDate;
import java.time.LocalTime;

/** Representacion de una llegada tardia registrada. */
public record LlegadaTardiaResponse(
        Long id,
        Long estudianteId,
        String estudianteNombre,
        String documento,
        LocalDate fecha,
        LocalTime hora,
        String observacion,
        MetodoRegistro metodo,
        String registradoPor
) {

    public static LlegadaTardiaResponse from(LlegadaTardia llegada) {
        return new LlegadaTardiaResponse(
                llegada.getId(),
                llegada.getEstudiante().getId(),
                llegada.getEstudiante().getNombreCompleto(),
                llegada.getEstudiante().getDocumento(),
                llegada.getFecha(),
                llegada.getHora(),
                llegada.getObservacion(),
                llegada.getMetodo(),
                llegada.getRegistradoPor().getNombreCompleto()
        );
    }
}
