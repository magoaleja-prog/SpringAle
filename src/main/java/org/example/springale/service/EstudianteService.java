package org.example.springale.service;

import org.example.springale.dto.EstudianteRequest;
import org.example.springale.dto.EstudianteResponse;
import org.example.springale.entity.Estudiante;
import org.example.springale.entity.GrupoEstudio;
import org.example.springale.entity.Rol;
import org.example.springale.entity.Semillero;
import org.example.springale.entity.Usuario;
import org.example.springale.exception.RecursoNoEncontradoException;
import org.example.springale.exception.ReglaNegocioException;
import org.example.springale.repository.EstudianteRepository;
import org.example.springale.repository.GrupoEstudioRepository;
import org.example.springale.repository.SemilleroRepository;
import org.example.springale.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Logica de negocio de estudiantes.
 * Mantiene sincronizado el perfil academico con su usuario de acceso.
 */
@Service
@Transactional(readOnly = true)
public class EstudianteService {

    private static final String PASSWORD_POR_DEFECTO = "Estudiante123*";

    private final EstudianteRepository estudiantes;
    private final SemilleroRepository semilleros;
    private final GrupoEstudioRepository grupos;
    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;

    public EstudianteService(EstudianteRepository estudiantes,
                             SemilleroRepository semilleros,
                             GrupoEstudioRepository grupos,
                             UsuarioRepository usuarios,
                             PasswordEncoder passwordEncoder) {
        this.estudiantes = estudiantes;
        this.semilleros = semilleros;
        this.grupos = grupos;
        this.usuarios = usuarios;
        this.passwordEncoder = passwordEncoder;
    }

    public List<EstudianteResponse> listar() {
        return estudiantes.findAll().stream().map(EstudianteResponse::from).toList();
    }

    public EstudianteResponse buscar(Long id) {
        return EstudianteResponse.from(entidad(id));
    }

    public List<EstudianteResponse> buscarPorTexto(String texto) {
        return estudiantes.buscarPorNombreOApellido(texto).stream()
                .map(EstudianteResponse::from)
                .toList();
    }

    public List<EstudianteResponse> porSemestre(Integer minimo) {
        return estudiantes.findBySemestreGreaterThanEqual(minimo).stream()
                .map(EstudianteResponse::from)
                .toList();
    }

    public List<EstudianteResponse> porSemillero(Long semilleroId) {
        return estudiantes.findBySemilleroId(semilleroId).stream()
                .map(EstudianteResponse::from)
                .toList();
    }

    @Transactional
    public EstudianteResponse crear(EstudianteRequest request) {
        String email = request.email().trim().toLowerCase();
        validarEmailDisponible(email, null);
        if (request.documento() != null
                && estudiantes.findByDocumento(request.documento().trim()).isPresent()) {
            throw new ReglaNegocioException(
                    "Ya existe un estudiante con el documento " + request.documento());
        }

        Usuario usuario = resolverUsuario(request, email);
        Estudiante estudiante = new Estudiante(
                request.documento().trim(),
                request.nombre().trim(),
                request.apellido().trim(),
                email,
                request.semestre(),
                usuario,
                resolverSemillero(request.semilleroId())
        );
        return EstudianteResponse.from(estudiantes.save(estudiante));
    }

    @Transactional
    public EstudianteResponse actualizar(Long id, EstudianteRequest request) {
        Estudiante estudiante = entidad(id);
        String email = request.email().trim().toLowerCase();
        validarEmailDisponible(email, estudiante);

        estudiante.setDocumento(request.documento().trim());
        estudiante.setNombre(request.nombre().trim());
        estudiante.setApellido(request.apellido().trim());
        estudiante.setEmail(email);
        estudiante.setSemestre(request.semestre());
        estudiante.setSemillero(resolverSemillero(request.semilleroId()));

        if (estudiante.getUsuario() == null && request.usuarioId() != null) {
            estudiante.setUsuario(usuario(request.usuarioId()));
        }
        if (estudiante.getUsuario() != null) {
            Usuario usuario = estudiante.getUsuario();
            usuario.setNombre(estudiante.getNombre());
            usuario.setApellido(estudiante.getApellido());
            usuario.setEmail(email);
            usuarios.save(usuario);
        }

        return EstudianteResponse.from(estudiantes.save(estudiante));
    }

    @Transactional
    public void eliminar(Long id) {
        Estudiante estudiante = entidad(id);
        for (GrupoEstudio grupo : grupos.findByMiembrosId(id)) {
            grupo.getMiembros().remove(estudiante);
        }
        estudiantes.delete(estudiante);
    }

    private void validarEmailDisponible(String email, Estudiante actual) {
        estudiantes.findByEmailIgnoreCase(email)
                .filter(otro -> actual == null || !otro.getId().equals(actual.getId()))
                .ifPresent(otro -> {
                    throw new ReglaNegocioException("Ya existe un estudiante con el email " + email);
                });
        usuarios.findByEmailIgnoreCase(email)
                .filter(otro -> actual == null
                        || actual.getUsuario() == null
                        || !otro.getId().equals(actual.getUsuario().getId()))
                .ifPresent(otro -> {
                    throw new ReglaNegocioException("Ya existe un usuario con el email " + email);
                });
    }

    private Usuario resolverUsuario(EstudianteRequest request, String email) {
        if (request.usuarioId() != null) {
            return usuario(request.usuarioId());
        }
        return usuarios.findByEmailIgnoreCase(email).orElseGet(() -> {
            Usuario nuevo = new Usuario(
                    request.nombre().trim(),
                    request.apellido().trim(),
                    email,
                    passwordEncoder.encode(PASSWORD_POR_DEFECTO),
                    Rol.ESTUDIANTE
            );
            return usuarios.save(nuevo);
        });
    }

    private Semillero resolverSemillero(Long semilleroId) {
        if (semilleroId == null) {
            return semilleros.findByNombreIgnoreCase("Semillero General")
                    .orElseGet(() -> semilleros.save(new Semillero(
                            "Semillero General",
                            "Semillero asignado automaticamente")));
        }
        return semilleros.findById(semilleroId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el semillero con id " + semilleroId));
    }

    private Usuario usuario(Long id) {
        return usuarios.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el usuario con id " + id));
    }

    private Estudiante entidad(Long id) {
        return estudiantes.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el estudiante con id " + id));
    }
}
