package org.example.springale.service.estrategia;

import org.example.springale.entity.MotivoPuntos;
import org.springframework.stereotype.Component;

/** Bonificacion adicional por entregar con anticipacion. */
@Component
public class BonificacionAnticipadaEstrategia implements EstrategiaPuntos {

    @Override
    public MotivoPuntos motivo() {
        return MotivoPuntos.BONIFICACION_ANTICIPADA;
    }

    @Override
    public int calcularPuntos() {
        return 5;
    }
}
