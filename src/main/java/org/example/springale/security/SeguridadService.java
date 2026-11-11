package org.example.springale.security;

import org.example.springale.entity.Estudiante;
import org.example.springale.entity.Rol;
import org.example.springale.entity.Usuario;
import org.example.springale.exception.AccesoDenegadoException;
import org.example.springale.exception.RecursoNoEncontradoException;
import org.example.springale.repository.EstudianteRepository;
import org.example.springale.repository.UsuarioRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Provee el usuario autenticado y utilidades de autorizacion.
 * Centraliza las validaciones de propiedad para no repetirlas en los controladores.
 */
@Service
public class SeguridadService {

    private final UsuarioRepository usuarios;
    private final EstudianteRepository estudiantes;

    public SeguridadService(UsuarioRepository usuarios, EstudianteRepository estudiantes) {
        this.usuarios = usuarios;
        this.estudiantes = estudiantes;
    }

    public Usuario usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new AccesoDenegadoException("No hay un usuario autenticado");
        }
        return usuarios.findByEmailIgnoreCase(auth.getName())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el usuario autenticado " + auth.getName()));
    }

    public boolean tieneRol(Rol rol) {
        return usuarioActual().getRol() == rol;
    }

    public boolean esAdministrador() {
        return tieneRol(Rol.ADMINISTRADOR);
    }

    public boolean esProfesor() {
        return tieneRol(Rol.PROFESOR);
    }

    public boolean esEstudiante() {
        return tieneRol(Rol.ESTUDIANTE);
    }

    public Estudiante estudianteActual() {
        Usuario usuario = usuarioActual();
        return estudiantes.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El usuario autenticado no tiene perfil de estudiante"));
    }

    /** Valida que el estudiante autenticado solo consulte su propia informacion. */
    public void validarAccesoEstudiante(Long estudianteId) {
        if (esEstudiante() && !estudianteActual().getId().equals(estudianteId)) {
            throw new AccesoDenegadoException(
                    "Un estudiante solo puede consultar su propia informacion");
        }
    }

    /** Valida que el usuario sea el profesor dueno de la materia o un administrador. */
    public void validarProfesorDeMateria(Long profesorId) {
        if (esAdministrador()) {
            return;
        }
        if (!esProfesor() || !usuarioActual().getId().equals(profesorId)) {
            throw new AccesoDenegadoException("No tiene permisos sobre esta materia");
        }
    }
}
