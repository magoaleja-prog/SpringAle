package org.example.springale.service.estrategia;

import org.example.springale.entity.MotivoPuntos;
import org.springframework.stereotype.Component;

/** Puntos por aportar a un trabajo en equipo. */
@Component
public class AporteEquipoEstrategia implements EstrategiaPuntos {

    @Override
    public MotivoPuntos motivo() {
        return MotivoPuntos.APORTE_EQUIPO;
    }

    @Override
    public int calcularPuntos() {
        return 8;
    }
}
