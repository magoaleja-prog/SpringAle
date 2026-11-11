package org.example.springale.service.estrategia;

import org.example.springale.entity.MotivoPuntos;
import org.springframework.stereotype.Component;

/** Puntos por participar en una tutoria completada. */
@Component
public class TutoriaCompletadaEstrategia implements EstrategiaPuntos {

    @Override
    public MotivoPuntos motivo() {
        return MotivoPuntos.TUTORIA_COMPLETADA;
    }

    @Override
    public int calcularPuntos() {
        return 15;
    }
}
