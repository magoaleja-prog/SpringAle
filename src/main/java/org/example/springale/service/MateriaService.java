package org.example.springale.service;

import org.example.springale.dto.EstudianteResumen;
import org.example.springale.dto.MateriaRequest;
import org.example.springale.dto.MateriaResponse;
import org.example.springale.entity.Estudiante;
import org.example.springale.entity.Materia;
import org.example.springale.entity.Rol;
import org.example.springale.entity.Usuario;
import org.example.springale.exception.RecursoNoEncontradoException;
import org.example.springale.exception.ReglaNegocioException;
import org.example.springale.repository.EstudianteRepository;
import org.example.springale.repository.MateriaRepository;
import org.example.springale.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Logica de negocio de materias e inscripciones. */
@Service
@Transactional(readOnly = true)
public class MateriaService {

    private final MateriaRepository materias;
    private final EstudianteRepository estudiantes;
    private final UsuarioRepository usuarios;

    public MateriaService(MateriaRepository materias,
                          EstudianteRepository estudiantes,
                          UsuarioRepository usuarios) {
        this.materias = materias;
        this.estudiantes = estudiantes;
        this.usuarios = usuarios;
    }

    public List<MateriaResponse> listar() {
        return materias.findAll().stream().map(MateriaResponse::from).toList();
    }

    public MateriaResponse buscar(Long id) {
        return MateriaResponse.from(entidad(id));
    }

    public List<MateriaResponse> porProfesor(Long profesorId) {
        return materias.findByProfesorId(profesorId).stream()
                .map(MateriaResponse::from)
                .toList();
    }

    public List<EstudianteResumen> estudiantesDe(Long materiaId) {
        return entidad(materiaId).getEstudiantes().stream()
                .map(EstudianteResumen::from)
                .toList();
    }

    @Transactional
    public MateriaResponse crear(MateriaRequest request) {
        if (materias.existsByCodigoIgnoreCase(request.codigo().trim())) {
            throw new ReglaNegocioException("Ya existe una materia con el codigo " + request.codigo());
        }
        Usuario profesor = resolverProfesor(request.profesorId());
        Materia materia = new Materia(
                request.codigo().trim().toUpperCase(),
                request.nombre().trim(),
                request.descripcion(),
                profesor
        );
        return MateriaResponse.from(materias.save(materia));
    }

    @Transactional
    public MateriaResponse actualizar(Long id, MateriaRequest request) {
        Materia materia = entidad(id);
        materias.findByCodigoIgnoreCase(request.codigo().trim())
                .filter(otra -> !otra.getId().equals(id))
                .ifPresent(otra -> {
                    throw new ReglaNegocioException(
                            "Ya existe otra materia con el codigo " + request.codigo());
                });

        materia.setCodigo(request.codigo().trim().toUpperCase());
        materia.setNombre(request.nombre().trim());
        materia.setDescripcion(request.descripcion());
        materia.setProfesor(resolverProfesor(request.profesorId()));
        return MateriaResponse.from(materias.save(materia));
    }

    @Transactional
    public MateriaResponse inscribir(Long materiaId, Long estudianteId) {
        Materia materia = entidad(materiaId);
        Estudiante estudiante = estudiante(estudianteId);
        materia.getEstudiantes().add(estudiante);
        return MateriaResponse.from(materias.save(materia));
    }

    @Transactional
    public MateriaResponse retirar(Long materiaId, Long estudianteId) {
        Materia materia = entidad(materiaId);
        Estudiante estudiante = estudiante(estudianteId);
        materia.getEstudiantes().remove(estudiante);
        return MateriaResponse.from(materias.save(materia));
    }

    @Transactional
    public void eliminar(Long id) {
        materias.delete(entidad(id));
    }

    private Usuario resolverProfesor(Long profesorId) {
        if (profesorId == null) {
            return null;
        }
        Usuario profesor = usuarios.findById(profesorId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el usuario con id " + profesorId));
        if (profesor.getRol() != Rol.PROFESOR && profesor.getRol() != Rol.ADMINISTRADOR) {
            throw new ReglaNegocioException("El responsable de la materia debe ser profesor");
        }
        return profesor;
    }

    private Estudiante estudiante(Long id) {
        return estudiantes.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el estudiante con id " + id));
    }

    private Materia entidad(Long id) {
        return materias.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la materia con id " + id));
    }
}
