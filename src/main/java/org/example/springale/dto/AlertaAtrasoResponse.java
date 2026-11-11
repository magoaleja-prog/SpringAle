package org.example.springale.dto;

import java.util.List;

/** Alerta de tareas atrasadas o pendientes de un estudiante. */
public record AlertaAtrasoResponse(
        Long estudianteId,
        String estudianteNombre,
        long tareasAtrasadas,
        long tareasPendientes,
        List<String> tareas
) {
}
