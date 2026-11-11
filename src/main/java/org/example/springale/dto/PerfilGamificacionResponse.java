package org.example.springale.dto;

import java.util.List;

/** Perfil de gamificacion de un estudiante: puntos, nivel, logros e historial. */
public record PerfilGamificacionResponse(
        Long estudianteId,
        String estudianteNombre,
        int puntosTotales,
        String nivel,
        int nivelNumero,
        int puntosSiguienteNivel,
        int progresoPorcentaje,
        List<LogroResponse> logrosDesbloqueados,
        List<LogroResponse> logrosPendientes,
        List<EventoPuntosResponse> historial
) {
}
