package org.example.springale.dto;

import java.util.List;

/**
 * Vista consolidada del proyecto: el lider (y el profesor) pueden ver
 * todos los aportes del equipo en un solo lugar.
 */
public record ProyectoConsolidadoResponse(
        ProyectoEquipoResponse proyecto,
        List<AporteEquipoResponse> aportes,
        List<EstudianteResumen> integrantesSinAporte,
        boolean listoParaEntregaFinal
) {
}
