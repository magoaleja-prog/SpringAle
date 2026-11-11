package org.example.springale.service.estrategia;

import org.example.springale.entity.MotivoPuntos;
import org.springframework.stereotype.Component;

/** Puntos por desbloquear un logro. */
@Component
public class LogroEstrategia implements EstrategiaPuntos {

    @Override
    public MotivoPuntos motivo() {
        return MotivoPuntos.LOGRO;
    }

    @Override
    public int calcularPuntos() {
        return 25;
    }
}
