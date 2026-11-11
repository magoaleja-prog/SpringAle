package org.example.springale.service.estrategia;

import org.example.springale.entity.MotivoPuntos;
import org.springframework.stereotype.Component;

/** Puntos por obtener un buen desempeno academico. */
@Component
public class BuenDesempenoEstrategia implements EstrategiaPuntos {

    @Override
    public MotivoPuntos motivo() {
        return MotivoPuntos.BUEN_DESEMPENO;
    }

    @Override
    public int calcularPuntos() {
        return 20;
    }
}
