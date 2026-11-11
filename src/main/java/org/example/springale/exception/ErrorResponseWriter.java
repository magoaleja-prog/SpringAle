package org.example.springale.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

/**
 * Escribe un {@link ApiError} directamente en la respuesta HTTP.
 * Se usa cuando la negociacion de contenido no puede aplicarse (por
 * ejemplo, un 406) o cuando el error ocurre fuera de un controlador
 * (filtros de seguridad y el endpoint /error).
 */
@Component
public class ErrorResponseWriter {

    private final JsonMapper jsonMapper;

    public ErrorResponseWriter(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    public void escribir(HttpServletResponse response,
                         HttpStatus estado,
                         String mensaje,
                         String ruta) throws IOException {
        response.setStatus(estado.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        jsonMapper.writeValue(response.getWriter(), ApiError.de(
                estado.value(),
                estado.getReasonPhrase(),
                mensaje,
                ruta
        ));
    }
}
