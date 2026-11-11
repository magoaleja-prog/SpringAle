package org.example.springale.service.estrategia;

import org.example.springale.entity.MotivoPuntos;
import org.springframework.stereotype.Component;

/** Puntos por entregar una tarea antes de la fecha limite. */
@Component
public class EntregaATiempoEstrategia implements EstrategiaPuntos {

    @Override
    public MotivoPuntos motivo() {
        return MotivoPuntos.ENTREGA_TAREA_A_TIEMPO;
    }

    @Override
    public int calcularPuntos() {
        return 10;
    }
}
