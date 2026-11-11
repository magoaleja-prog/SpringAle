package org.example.springale.dto;

import org.example.springale.entity.EstadoRendimiento;

import java.util.Map;

/** Resumen del rendimiento academico de un estudiante. */
public record RendimientoResponse(
        Long estudianteId,
        String estudianteNombre,
        Double promedio,
        long totalCalificaciones,
        EstadoRendimiento estado,
        Map<String, Double> promedioPorMateria
) {
}
