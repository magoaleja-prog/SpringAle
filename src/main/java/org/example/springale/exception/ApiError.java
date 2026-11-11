package org.example.springale.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Estructura uniforme de error para toda la API.
 * Se usa tanto en el manejador global de excepciones como en los
 * entry points de Spring Security para que el cliente reciba siempre
 * el mismo formato JSON.
 */
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String ruta,
        Map<String, String> campos
) {

    public static ApiError de(int status, String error, String mensaje, String ruta) {
        return new ApiError(LocalDateTime.now(), status, error, mensaje, ruta, Map.of());
    }
}
