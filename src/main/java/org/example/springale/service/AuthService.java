package org.example.springale.service;

import org.example.springale.dto.AuthResponse;
import org.example.springale.dto.LoginRequest;
import org.example.springale.dto.RegistroRequest;
import org.example.springale.entity.Estudiante;
import org.example.springale.entity.Rol;
import org.example.springale.entity.Semillero;
import org.example.springale.entity.Usuario;
import org.example.springale.exception.ReglaNegocioException;
import org.example.springale.repository.EstudianteRepository;
import org.example.springale.repository.SemilleroRepository;
import org.example.springale.repository.UsuarioRepository;
import org.example.springale.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Servicio de autenticacion y registro publico.
 * El registro publico siempre crea usuarios con rol ESTUDIANTE; los roles
 * administrativos solo pueden ser creados por un administrador.
 */
@Service
@Transactional(readOnly = true)
public class AuthService {

    private static final String SEMILLERO_POR_DEFECTO = "Semillero General";

    private final UsuarioRepository usuarios;
    private final EstudianteRepository estudiantes;
    private final SemilleroRepository semilleros;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarios,
                       EstudianteRepository estudiantes,
                       SemilleroRepository semilleros,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.usuarios = usuarios;
        this.estudiantes = estudiantes;
        this.semilleros = semilleros;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse registrar(RegistroRequest request) {
        String email = request.email().trim().toLowerCase();
        if (usuarios.existsByEmailIgnoreCase(email)) {
            throw new ReglaNegocioException("Ya existe un usuario con el email " + email);
        }
        if (request.documento() != null && estudiantes.findByDocumento(request.documento().trim()).isPresent()) {
            throw new ReglaNegocioException("Ya existe un estudiante con el documento " + request.documento());
        }

        Usuario usuario = new Usuario(
                request.nombre().trim(),
                request.apellido().trim(),
                email,
                passwordEncoder.encode(request.password()),
                Rol.ESTUDIANTE
        );
        usuario = usuarios.save(usuario);

        Semillero semillero = resolverSemillero(request.semilleroId());
        Estudiante estudiante = new Estudiante(
                request.documento().trim(),
                usuario.getNombre(),
                usuario.getApellido(),
                email,
                request.semestre() != null ? request.semestre() : 1,
                usuario,
                semillero
        );
        estudiantes.save(estudiante);

        return construirRespuesta(usuario);
    }

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarios.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new ReglaNegocioException("Credenciales incorrectas"));

        if (!usuario.isActivo()) {
            throw new ReglaNegocioException("El usuario esta inactivo");
        }
        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new ReglaNegocioException("Credenciales incorrectas");
        }

        return construirRespuesta(usuario);
    }

    public AuthResponse construirRespuesta(Usuario usuario) {
        String token = jwtService.generarToken(usuario);
        Long estudianteId = estudiantes.findByUsuarioId(usuario.getId())
                .map(Estudiante::getId)
                .orElse(null);

        return new AuthResponse(
                token,
                "Bearer",
                usuario.getId(),
                usuario.getNombreCompleto(),
                usuario.getEmail(),
                usuario.getRol(),
                estudianteId,
                jwtService.getExpiracionMs()
        );
    }

    private Semillero resolverSemillero(Long semilleroId) {
        if (semilleroId != null) {
            return semilleros.findById(semilleroId)
                    .orElseThrow(() -> new ReglaNegocioException(
                            "No existe el semillero con id " + semilleroId));
        }
        return Optional.ofNullable(semilleros.findByNombreIgnoreCase(SEMILLERO_POR_DEFECTO).orElse(null))
                .orElseGet(() -> semilleros.save(new Semillero(
                        SEMILLERO_POR_DEFECTO,
                        "Semillero asignado automaticamente a nuevos estudiantes")));
    }
}
