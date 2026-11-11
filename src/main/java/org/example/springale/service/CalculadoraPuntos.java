package org.example.springale.service;

import org.example.springale.entity.MotivoPuntos;
import org.example.springale.exception.ReglaNegocioException;
import org.example.springale.service.estrategia.EstrategiaPuntos;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Calculadora de puntos basada en el patron Strategy.
 * Recibe todas las estrategias por inyeccion de dependencias y las indexa
 * por motivo, evitando condicionales gigantes.
 */
@Service
public class CalculadoraPuntos {

    private final Map<MotivoPuntos, EstrategiaPuntos> estrategias;

    public CalculadoraPuntos(List<EstrategiaPuntos> estrategias) {
        this.estrategias = new EnumMap<>(MotivoPuntos.class);
        estrategias.forEach(estrategia -> this.estrategias.put(estrategia.motivo(), estrategia));
    }

    public int calcular(MotivoPuntos motivo) {
        EstrategiaPuntos estrategia = estrategias.get(motivo);
        if (estrategia == null) {
            throw new ReglaNegocioException("No existe una estrategia para el motivo " + motivo);
        }
        return estrategia.calcularPuntos();
    }
}
