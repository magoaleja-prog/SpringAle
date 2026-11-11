package org.example.springale.entity;

/** Clasificacion cualitativa del promedio academico. */
public enum EstadoRendimiento {
    EXCELENTE,
    BUENO,
    ACEPTABLE,
    BAJO;

    public static EstadoRendimiento desdePromedio(double promedio) {
        if (promedio >= 4.5) return EXCELENTE;
        if (promedio >= 4.0) return BUENO;
        if (promedio >= 3.0) return ACEPTABLE;
        return BAJO;
    }
}
