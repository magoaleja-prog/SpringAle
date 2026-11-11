package org.example.springale.service;

import org.example.springale.dto.CambiarPasswordRequest;
import org.example.springale.dto.UsuarioRequest;
import org.example.springale.dto.UsuarioResponse;
import org.example.springale.entity.Estudiante;
import org.example.springale.entity.Rol;
import org.example.springale.entity.Semillero;
import org.example.springale.entity.Usuario;
import org.example.springale.exception.RecursoNoEncontradoException;
import org.example.springale.exception.ReglaNegocioException;
import org.example.springale.repository.EstudianteRepository;
import org.example.springale.repository.SemilleroRepository;
import org.example.springale.repository.UsuarioRepository;
import org.example.springale.security.SeguridadService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CRUD de usuarios administrado por el rol ADMINISTRADOR.
 * Si se crea un usuario ESTUDIANTE tambien se genera su perfil academico.
 */
@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarios;
    private final EstudianteRepository estudiantes;
    private final SemilleroRepository semilleros;
    private final PasswordEncoder passwordEncoder;
    private final SeguridadService seguridad;

    public UsuarioService(UsuarioRepository usuarios,
                          EstudianteRepository estudiantes,
                          SemilleroRepository semilleros,
                          PasswordEncoder passwordEncoder,
                          SeguridadService seguridad) {
        this.usuarios = usuarios;
        this.estudiantes = estudiantes;
        this.semilleros = semilleros;
        this.passwordEncoder = passwordEncoder;
        this.seguridad = seguridad;
    }

    public List<UsuarioResponse> listar() {
        return usuarios.findAll().stream().map(UsuarioResponse::from).toList();
    }

    public UsuarioResponse buscar(Long id) {
        return UsuarioResponse.from(entidad(id));
    }

    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        String email = request.email().trim().toLowerCase();
        if (usuarios.existsByEmailIgnoreCase(email)) {
            throw new ReglaNegocioException("Ya existe un usuario con el email " + email);
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new ReglaNegocioException("La contrasena es obligatoria para crear un usuario");
        }

        Usuario usuario = new Usuario(
                request.nombre().trim(),
                request.apellido().trim(),
                email,
                passwordEncoder.encode(request.password()),
                request.rol()
        );
        usuario.setActivo(request.activo() == null || request.activo());
        usuario = usuarios.save(usuario);

        if (usuario.getRol() == Rol.ESTUDIANTE) {
            crearPerfilEstudiante(usuario);
        }
        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {
        Usuario usuario = entidad(id);
        String email = request.email().trim().toLowerCase();

        usuarios.findByEmailIgnoreCase(email)
                .filter(otro -> !otro.getId().equals(id))
                .ifPresent(otro -> {
                    throw new ReglaNegocioException("Ya existe otro usuario con el email " + email);
                });

        usuario.setNombre(request.nombre().trim());
        usuario.setApellido(request.apellido().trim());
        usuario.setEmail(email);
        usuario.setRol(request.rol());
        if (request.activo() != null) {
            usuario.setActivo(request.activo());
        }
        if (request.password() != null && !request.password().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.password()));
        }

        usuario = usuarios.save(usuario);
        if (usuario.getRol() == Rol.ESTUDIANTE
                && estudiantes.findByUsuarioId(usuario.getId()).isEmpty()) {
            crearPerfilEstudiante(usuario);
        }
        return UsuarioResponse.from(usuario);
    }

    /** Baja logica: conserva el historial academico del usuario. */
    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = entidad(id);
        if (usuario.getId().equals(seguridad.usuarioActual().getId())) {
            throw new ReglaNegocioException("No puede desactivar su propio usuario");
        }
        usuario.setActivo(false);
        usuarios.save(usuario);
    }

    @Transactional
    public void cambiarPassword(CambiarPasswordRequest request) {
        Usuario usuario = seguridad.usuarioActual();
        if (!passwordEncoder.matches(request.passwordActual(), usuario.getPassword())) {
            throw new ReglaNegocioException("La contrasena actual no es correcta");
        }
        usuario.setPassword(passwordEncoder.encode(request.passwordNueva()));
        usuarios.save(usuario);
    }

    private void crearPerfilEstudiante(Usuario usuario) {
        Semillero semillero = semilleros.findByNombreIgnoreCase("Semillero General")
                .orElseGet(() -> semilleros.save(new Semillero(
                        "Semillero General",
                        "Semillero asignado automaticamente")));

        estudiantes.save(new Estudiante(
                null,
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                1,
                usuario,
                semillero
        ));
    }

    private Usuario entidad(Long id) {
        return usuarios.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el usuario con id " + id));
    }
}
