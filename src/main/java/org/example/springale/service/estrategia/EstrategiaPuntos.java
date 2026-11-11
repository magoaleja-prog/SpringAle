package org.example.springale.service.estrategia;

import org.example.springale.entity.MotivoPuntos;

/**
 * Patron Strategy: cada motivo de puntos define su propia regla de calculo.
 * Permite agregar nuevos motivos sin modificar la calculadora (principio abierto/cerrado).
 */
public interface EstrategiaPuntos {

    MotivoPuntos motivo();

    int calcularPuntos();
}
