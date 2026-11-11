package org.example.springale.exception;

/** Se lanza cuando el usuario no tiene permisos sobre un recurso. */
public class AccesoDenegadoException extends RuntimeException {

    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
