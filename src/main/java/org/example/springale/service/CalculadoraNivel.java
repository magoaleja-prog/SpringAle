package org.example.springale.service;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Calculadora de niveles del sistema de gamificacion.
 *
 * <p>Se declara explicitamente como Singleton porque sus reglas son
 * inmutables y no mantiene estado por usuario. Spring ya crea los beans
 * como singleton por defecto; la anotacion hace la intencion explicita.</p>
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CalculadoraNivel {

    public NivelInfo calcular(int puntos) {
        if (puntos >= 1000) {
            return new NivelInfo("Maestro", 5, 0, 100);
        }
        if (puntos >= 600) {
            return nivel("Experto", 4, 600, 1000, puntos);
        }
        if (puntos >= 300) {
            return nivel("Avanzado", 3, 300, 600, puntos);
        }
        if (puntos >= 100) {
            return nivel("Aprendiz", 2, 100, 300, puntos);
        }
        return nivel("Novato", 1, 0, 100, puntos);
    }

    private NivelInfo nivel(String nombre, int numero, int base, int siguiente, int puntos) {
        int progreso = (int) Math.round(((double) (puntos - base) / (siguiente - base)) * 100);
        return new NivelInfo(nombre, numero, siguiente, Math.max(0, Math.min(100, progreso)));
    }

    /** Resultado inmutable del calculo de nivel. */
    public record NivelInfo(String nombre, int numero, int puntosSiguienteNivel, int progresoPorcentaje) {
    }
}
