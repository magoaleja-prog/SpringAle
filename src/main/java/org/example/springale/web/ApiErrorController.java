package org.example.springale.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.springale.exception.ErrorResponseWriter;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
 * Reemplaza al controlador de errores por defecto de Spring Boot para que
 * cualquier error que llegue al endpoint /error (por ejemplo, una variable
 * de ruta vacia o un error del contenedor) tambien se devuelva con la
 * estructura JSON de {@code ApiError}.
 */
@Controller
public class ApiErrorController implements ErrorController {

    private final ErrorResponseWriter errorResponseWriter;

    public ApiErrorController(ErrorResponseWriter errorResponseWriter) {
        this.errorResponseWriter = errorResponseWriter;
    }

    @RequestMapping("${server.error.path:${error.path:/error}}")
    public void error(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpStatus estado = resolverEstado(request);
        errorResponseWriter.escribir(response, estado, mensajePara(estado), ruta(request));
    }

    private HttpStatus resolverEstado(HttpServletRequest request) {
        Object atributo = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (atributo instanceof Integer codigo) {
            HttpStatus estado = HttpStatus.resolve(codigo);
            if (estado != null) {
                return estado;
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String ruta(HttpServletRequest request) {
        Object atributo = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        if (atributo instanceof String ruta && !ruta.isBlank()) {
            return ruta;
        }
        return request.getRequestURI();
    }

    private String mensajePara(HttpStatus estado) {
        return switch (estado.value()) {
            case 400 -> "Solicitud invalida";
            case 401 -> "No autenticado o token invalido";
            case 403 -> "No tiene permisos para realizar esta operacion";
            case 404 -> "Recurso no encontrado";
            case 405 -> "Metodo HTTP no permitido para esta ruta";
            case 406 -> "El formato de respuesta solicitado no es soportado. Use Accept: application/json";
            case 409 -> "Datos duplicados o relacionados con otras tablas";
            case 415 -> "El tipo de contenido enviado no es soportado. Envie el cuerpo como application/json";
            default -> estado.is5xxServerError()
                    ? "Ocurrio un error interno. Consulte los logs del servidor"
                    : "No se pudo procesar la solicitud";
        };
    }
}
