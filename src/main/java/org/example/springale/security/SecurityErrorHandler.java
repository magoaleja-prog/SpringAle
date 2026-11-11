package org.example.springale.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.springale.exception.ErrorResponseWriter;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Escribe las respuestas 401 y 403 de Spring Security con el mismo
 * formato JSON que usa el manejador global de excepciones.
 */
@Component
public class SecurityErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ErrorResponseWriter errorResponseWriter;

    public SecurityErrorHandler(ErrorResponseWriter errorResponseWriter) {
        this.errorResponseWriter = errorResponseWriter;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        errorResponseWriter.escribir(response, HttpStatus.UNAUTHORIZED,
                "No autenticado o token invalido", request.getRequestURI());
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        errorResponseWriter.escribir(response, HttpStatus.FORBIDDEN,
                "No tiene permisos para realizar esta operacion", request.getRequestURI());
    }
}
