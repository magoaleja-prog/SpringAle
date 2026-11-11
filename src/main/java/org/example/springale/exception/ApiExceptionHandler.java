package org.example.springale.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Manejo global de excepciones.
 * Convierte cualquier error en una respuesta JSON con la misma estructura
 * (timestamp, status, error, mensaje, ruta y campos) sin exponer detalles
 * internos de la aplicacion.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private final ErrorResponseWriter errorResponseWriter;

    public ApiExceptionHandler(ErrorResponseWriter errorResponseWriter) {
        this.errorResponseWriter = errorResponseWriter;
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiError> noEncontrado(RecursoNoEncontradoException ex,
                                                 HttpServletRequest request) {
        return respuesta(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> noEncontradoLegacy(NoSuchElementException ex,
                                                       HttpServletRequest request) {
        return respuesta(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ApiError> reglaNegocio(ReglaNegocioException ex,
                                                 HttpServletRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<ApiError> accesoDenegado(AccesoDenegadoException ex,
                                                   HttpServletRequest request) {
        return respuesta(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> accesoDenegadoSpring(AccessDeniedException ex,
                                                         HttpServletRequest request) {
        return respuesta(HttpStatus.FORBIDDEN,
                "No tiene permisos para realizar esta operacion", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validacion(MethodArgumentNotValidException ex,
                                               HttpServletRequest request) {
        return respuestaConCampos(HttpStatus.BAD_REQUEST,
                "Datos de entrada invalidos",
                "Revise los campos enviados",
                request,
                camposDe(ex));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiError> enlaceInvalido(BindException ex,
                                                   HttpServletRequest request) {
        return respuestaConCampos(HttpStatus.BAD_REQUEST,
                "Datos de entrada invalidos",
                "Revise los campos enviados",
                request,
                camposDe(ex));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> validacionParametros(ConstraintViolationException ex,
                                                         HttpServletRequest request) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(violacion -> campos.put(
                violacion.getPropertyPath().toString(),
                violacion.getMessage()));

        return respuestaConCampos(HttpStatus.BAD_REQUEST,
                "Datos de entrada invalidos",
                "Revise los parametros enviados",
                request,
                campos);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> cuerpoInvalido(HttpMessageNotReadableException ex,
                                                   HttpServletRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud no es valido o tiene un formato incorrecto", request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> parametroInvalido(MethodArgumentTypeMismatchException ex,
                                                      HttpServletRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST,
                "Valor invalido para el parametro " + ex.getName(), request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> parametroFaltante(MissingServletRequestParameterException ex,
                                                      HttpServletRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST,
                "Falta el parametro requerido: " + ex.getParameterName(), request);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ApiError> variableRutaFaltante(MissingPathVariableException ex,
                                                         HttpServletRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST,
                "Falta el valor de la variable de ruta: " + ex.getVariableName(), request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> metodoNoSoportado(HttpRequestMethodNotSupportedException ex,
                                                      HttpServletRequest request) {
        String permitidos = ex.getSupportedHttpMethods() == null
                ? ""
                : ex.getSupportedHttpMethods().stream()
                        .map(HttpMethod::name)
                        .sorted()
                        .collect(Collectors.joining(", "));

        String mensaje = "El metodo HTTP " + ex.getMethod()
                + " no esta permitido para esta ruta";
        if (!permitidos.isBlank()) {
            mensaje += ". Metodos permitidos: " + permitidos;
        }
        return respuesta(HttpStatus.METHOD_NOT_ALLOWED, mensaje, request);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> tipoContenidoNoSoportado(
            HttpMediaTypeNotSupportedException ex,
            HttpServletRequest request) {
        String tipo = ex.getContentType() == null
                ? "enviado"
                : ex.getContentType().toString();
        return respuesta(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "El tipo de contenido " + tipo
                        + " no es soportado. Envie el cuerpo como application/json",
                request);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public void tipoRespuestaNoAceptable(
            HttpMediaTypeNotAcceptableException ex,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        errorResponseWriter.escribir(response, HttpStatus.NOT_ACCEPTABLE,
                "El formato de respuesta solicitado no es soportado. Use Accept: application/json",
                request.getRequestURI());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> recursoNoEncontrado(NoResourceFoundException ex,
                                                        HttpServletRequest request) {
        return respuesta(HttpStatus.NOT_FOUND, "Recurso no encontrado", request);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> rutaNoEncontrada(NoHandlerFoundException ex,
                                                     HttpServletRequest request) {
        return respuesta(HttpStatus.NOT_FOUND, "No existe la ruta solicitada", request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> conflicto(DataIntegrityViolationException ex,
                                              HttpServletRequest request) {
        return respuesta(HttpStatus.CONFLICT,
                "Datos duplicados o relacionados con otras tablas", request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> argumentoInvalido(IllegalArgumentException ex,
                                                      HttpServletRequest request) {
        String mensaje = ex.getMessage() == null || ex.getMessage().isBlank()
                ? "Los datos enviados no son validos"
                : ex.getMessage();
        return respuesta(HttpStatus.BAD_REQUEST, mensaje, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> errorGeneral(Exception ex,
                                                 HttpServletRequest request) {
        log.error("Error no controlado en {} {}", request.getMethod(),
                request.getRequestURI(), ex);
        return respuesta(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrio un error interno. Consulte los logs del servidor", request);
    }

    private ResponseEntity<ApiError> respuesta(HttpStatus estado, String mensaje,
                                               HttpServletRequest request) {
        return ResponseEntity.status(estado)
                .body(ApiError.de(estado.value(), estado.getReasonPhrase(),
                        mensaje, request.getRequestURI()));
    }

    private ResponseEntity<ApiError> respuestaConCampos(HttpStatus estado,
                                                        String error,
                                                        String mensaje,
                                                        HttpServletRequest request,
                                                        Map<String, String> campos) {
        return ResponseEntity.status(estado).body(new ApiError(
                LocalDateTime.now(),
                estado.value(),
                error,
                mensaje,
                request.getRequestURI(),
                campos
        ));
    }

    private Map<String, String> camposDe(BindException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                campos.put(error.getField(), error.getDefaultMessage()));
        return campos;
    }
}
