package org.example.springale.service;

import org.example.springale.dto.CalificacionRequest;
import org.example.springale.dto.CalificacionResponse;
import org.example.springale.dto.RendimientoResponse;
import org.example.springale.entity.Calificacion;
import org.example.springale.entity.EstadoRendimiento;
import org.example.springale.entity.Estudiante;
import org.example.springale.entity.Materia;
import org.example.springale.entity.MotivoPuntos;
import org.example.springale.entity.PeriodoAcademico;
import org.example.springale.entity.Rol;
import org.example.springale.entity.TipoNotificacion;
import org.example.springale.entity.Usuario;
import org.example.springale.exception.AccesoDenegadoException;
import org.example.springale.exception.RecursoNoEncontradoException;
import org.example.springale.repository.CalificacionRepository;
import org.example.springale.repository.EstudianteRepository;
import org.example.springale.repository.MateriaRepository;
import org.example.springale.repository.PeriodoAcademicoRepository;
import org.example.springale.security.SeguridadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Logica de negocio de notas.
 * Reglas clave:
 * - Un profesor solo califica materias que tiene asignadas.
 * - Un estudiante solo consulta sus propias notas.
 * - Una nota alta otorga puntos de gamificacion y genera una notificacion.
 */
@Service
@Transactional(readOnly = true)
public class CalificacionService {

    private final CalificacionRepository calificaciones;
    private final EstudianteRepository estudiantes;
    private final MateriaRepository materias;
    private final PeriodoAcademicoRepository periodos;
    private final SeguridadService seguridad;
    private final GamificacionService gamificacion;
    private final NotificacionService notificaciones;

    public CalificacionService(CalificacionRepository calificaciones,
                               EstudianteRepository estudiantes,
                               MateriaRepository materias,
                               PeriodoAcademicoRepository periodos,
                               SeguridadService seguridad,
                               GamificacionService gamificacion,
                               NotificacionService notificaciones) {
        this.calificaciones = calificaciones;
        this.estudiantes = estudiantes;
        this.materias = materias;
        this.periodos = periodos;
        this.seguridad = seguridad;
        this.gamificacion = gamificacion;
        this.notificaciones = notificaciones;
    }

    public List<CalificacionResponse> listar(Long estudianteId, Long materiaId) {
        Long filtroEstudiante = estudianteId;
        if (seguridad.esEstudiante()) {
            filtroEstudiante = seguridad.estudianteActual().getId();
        }

        List<Calificacion> resultado;
        if (filtroEstudiante != null && materiaId != null) {
            resultado = calificaciones
                    .findByEstudianteIdAndMateriaIdOrderByFechaDesc(filtroEstudiante, materiaId);
        } else if (filtroEstudiante != null) {
            resultado = calificaciones.findByEstudianteIdOrderByFechaDesc(filtroEstudiante);
        } else if (materiaId != null) {
            resultado = calificaciones.findByMateriaIdOrderByFechaDesc(materiaId);
        } else {
            resultado = calificaciones.findAllByOrderByFechaDesc();
        }
        return resultado.stream().map(CalificacionResponse::from).toList();
    }

    public CalificacionResponse buscar(Long id) {
        Calificacion calificacion = entidad(id);
        seguridad.validarAccesoEstudiante(calificacion.getEstudiante().getId());
        return CalificacionResponse.from(calificacion);
    }

    public List<CalificacionResponse> historial(Long estudianteId, Long materiaId) {
        seguridad.validarAccesoEstudiante(estudianteId);
        List<Calificacion> resultado = materiaId != null
                ? calificaciones.findByEstudianteIdAndMateriaIdOrderByFechaDesc(estudianteId, materiaId)
                : calificaciones.findByEstudianteIdOrderByFechaDesc(estudianteId);
        return resultado.stream().map(CalificacionResponse::from).toList();
    }

    public RendimientoResponse rendimiento(Long estudianteId) {
        seguridad.validarAccesoEstudiante(estudianteId);
        Estudiante estudiante = estudiante(estudianteId);
        List<Calificacion> lista = calificaciones.findByEstudianteIdOrderByFechaDesc(estudianteId);

        double promedio = lista.stream()
                .mapToDouble(Calificacion::getNota)
                .average()
                .orElse(0.0);

        Map<String, Double> promedioPorMateria = new LinkedHashMap<>();
        lista.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        calificacion -> calificacion.getMateria().getNombre(),
                        LinkedHashMap::new,
                        java.util.stream.Collectors.averagingDouble(Calificacion::getNota)))
                .forEach((materia, valor) -> promedioPorMateria.put(
                        materia, Math.round(valor * 100.0) / 100.0));

        return new RendimientoResponse(
                estudiante.getId(),
                estudiante.getNombreCompleto(),
                Math.round(promedio * 100.0) / 100.0,
                lista.size(),
                EstadoRendimiento.desdePromedio(promedio),
                promedioPorMateria
        );
    }

    @Transactional
    public CalificacionResponse crear(CalificacionRequest request) {
        Estudiante estudiante = estudiante(request.estudianteId());
        Materia materia = materia(request.materiaId());
        PeriodoAcademico periodo = periodo(request.periodoId());
        Usuario profesor = resolverProfesor(materia);

        if (!materia.getEstudiantes().contains(estudiante)) {
            throw new AccesoDenegadoException(
                    "El estudiante no esta inscrito en la materia " + materia.getNombre());
        }

        LocalDate fecha = request.fecha() != null ? request.fecha() : LocalDate.now();
        Calificacion calificacion = new Calificacion(
                estudiante,
                materia,
                periodo,
                profesor,
                request.evaluacion().trim(),
                request.nota(),
                fecha,
                request.observacion()
        );
        calificacion = calificaciones.save(calificacion);

        if (request.nota() >= 4.5) {
            gamificacion.otorgar(estudiante, MotivoPuntos.BUEN_DESEMPENO,
                    "Buen desempeno en " + materia.getNombre() + ": " + request.nota(),
                    materia);
        }
        notificaciones.crearParaEstudiante(
                estudiante,
                "Nueva calificacion registrada",
                "Se registro la nota " + request.nota() + " en " + materia.getNombre()
                        + " (" + request.evaluacion() + ").",
                TipoNotificacion.CALIFICACION,
                calificacion.getId()
        );

        return CalificacionResponse.from(calificacion);
    }

    @Transactional
    public CalificacionResponse actualizar(Long id, CalificacionRequest request) {
        Calificacion calificacion = entidad(id);
        validarPropietario(calificacion);

        Estudiante estudiante = estudiante(request.estudianteId());
        Materia materia = materia(request.materiaId());
        PeriodoAcademico periodo = periodo(request.periodoId());
        Usuario profesor = resolverProfesor(materia);

        calificacion.setEstudiante(estudiante);
        calificacion.setMateria(materia);
        calificacion.setPeriodo(periodo);
        calificacion.setProfesor(profesor);
        calificacion.setEvaluacion(request.evaluacion().trim());
        calificacion.setNota(request.nota());
        calificacion.setFecha(request.fecha() != null ? request.fecha() : LocalDate.now());
        calificacion.setObservacion(request.observacion());

        return CalificacionResponse.from(calificaciones.save(calificacion));
    }

    @Transactional
    public void eliminar(Long id) {
        Calificacion calificacion = entidad(id);
        validarPropietario(calificacion);
        calificaciones.delete(calificacion);
    }

    /** Promedio usado por tutorias para medir el desempeno antes y despues. */
    public Double promedioEnMateria(Long estudianteId, Long materiaId) {
        return calificaciones.promedioEstudianteEnMateria(estudianteId, materiaId);
    }

    private Usuario resolverProfesor(Materia materia) {
        Usuario actual = seguridad.usuarioActual();
        if (actual.getRol() == Rol.ADMINISTRADOR) {
            return materia.getProfesor() != null ? materia.getProfesor() : actual;
        }
        if (materia.getProfesor() == null
                || !materia.getProfesor().getId().equals(actual.getId())) {
            throw new AccesoDenegadoException(
                    "Solo el profesor asignado puede calificar esta materia");
        }
        return actual;
    }

    private void validarPropietario(Calificacion calificacion) {
        if (seguridad.esAdministrador()) {
            return;
        }
        if (!seguridad.esProfesor()
                || !calificacion.getProfesor().getId().equals(seguridad.usuarioActual().getId())) {
            throw new AccesoDenegadoException(
                    "Solo el profesor que registro la nota puede modificarla");
        }
    }

    private Calificacion entidad(Long id) {
        return calificaciones.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la calificacion con id " + id));
    }

    private Estudiante estudiante(Long id) {
        return estudiantes.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el estudiante con id " + id));
    }

    private Materia materia(Long id) {
        return materias.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la materia con id " + id));
    }

    private PeriodoAcademico periodo(Long id) {
        return periodos.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el periodo con id " + id));
    }
}
