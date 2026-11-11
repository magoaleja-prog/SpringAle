package org.example.springale.service;

import org.example.springale.dto.AlertaAtrasoResponse;
import org.example.springale.dto.EntregaTareaRequest;
import org.example.springale.dto.EntregaTareaResponse;
import org.example.springale.dto.TareaEstudianteResponse;
import org.example.springale.dto.TareaRequest;
import org.example.springale.dto.TareaResponse;
import org.example.springale.entity.EntregaTarea;
import org.example.springale.entity.EstadoTarea;
import org.example.springale.entity.Estudiante;
import org.example.springale.entity.Materia;
import org.example.springale.entity.MotivoPuntos;
import org.example.springale.entity.Rol;
import org.example.springale.entity.Tarea;
import org.example.springale.entity.TipoNotificacion;
import org.example.springale.entity.Usuario;
import org.example.springale.exception.AccesoDenegadoException;
import org.example.springale.exception.RecursoNoEncontradoException;
import org.example.springale.exception.ReglaNegocioException;
import org.example.springale.repository.EntregaTareaRepository;
import org.example.springale.repository.EstudianteRepository;
import org.example.springale.repository.MateriaRepository;
import org.example.springale.repository.TareaRepository;
import org.example.springale.security.SeguridadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Logica de tareas y entregas.
 * Incluye bonificacion por entrega anticipada, recordatorios y alertas
 * de tareas atrasadas para el profesor.
 */
@Service
@Transactional(readOnly = true)
public class TareaService {

    private final TareaRepository tareas;
    private final EntregaTareaRepository entregas;
    private final EstudianteRepository estudiantes;
    private final MateriaRepository materias;
    private final SeguridadService seguridad;
    private final GamificacionService gamificacion;
    private final NotificacionService notificaciones;

    public TareaService(TareaRepository tareas,
                        EntregaTareaRepository entregas,
                        EstudianteRepository estudiantes,
                        MateriaRepository materias,
                        SeguridadService seguridad,
                        GamificacionService gamificacion,
                        NotificacionService notificaciones) {
        this.tareas = tareas;
        this.entregas = entregas;
        this.estudiantes = estudiantes;
        this.materias = materias;
        this.seguridad = seguridad;
        this.gamificacion = gamificacion;
        this.notificaciones = notificaciones;
    }

    public List<TareaResponse> listar(Long materiaId, Long profesorId) {
        List<Tarea> resultado;
        if (materiaId != null) {
            resultado = tareas.findByMateriaIdOrderByFechaLimiteAsc(materiaId);
        } else if (profesorId != null) {
            resultado = tareas.findByMateriaProfesorIdOrderByFechaLimiteAsc(profesorId);
        } else if (seguridad.esProfesor()) {
            resultado = tareas.findByMateriaProfesorIdOrderByFechaLimiteAsc(
                    seguridad.usuarioActual().getId());
        } else {
            resultado = tareas.findByActivaTrueOrderByFechaLimiteAsc();
        }
        return resultado.stream().map(TareaResponse::from).toList();
    }

    public TareaResponse buscar(Long id) {
        return TareaResponse.from(entidad(id));
    }

    @Transactional
    public TareaResponse crear(TareaRequest request) {
        Materia materia = materia(request.materiaId());
        Usuario profesor = resolverProfesor(materia);

        Tarea tarea = new Tarea(
                request.titulo().trim(),
                request.descripcion().trim(),
                request.fechaLimite(),
                materia,
                profesor
        );
        tarea = tareas.save(tarea);

        for (Estudiante estudiante : materia.getEstudiantes()) {
            notificaciones.crearParaEstudiante(
                    estudiante,
                    "Nueva tarea en " + materia.getNombre(),
                    "Se publico la tarea '" + tarea.getTitulo()
                            + "'. Fecha limite: " + tarea.getFechaLimite() + ".",
                    TipoNotificacion.TAREA,
                    tarea.getId()
            );
        }
        return TareaResponse.from(tarea);
    }

    @Transactional
    public TareaResponse actualizar(Long id, TareaRequest request) {
        Tarea tarea = entidad(id);
        validarProfesor(tarea);
        Materia materia = materia(request.materiaId());

        tarea.setTitulo(request.titulo().trim());
        tarea.setDescripcion(request.descripcion().trim());
        tarea.setFechaLimite(request.fechaLimite());
        tarea.setMateria(materia);
        tarea.setProfesor(resolverProfesor(materia));
        return TareaResponse.from(tareas.save(tarea));
    }

    @Transactional
    public void eliminar(Long id) {
        Tarea tarea = entidad(id);
        validarProfesor(tarea);
        entregas.deleteAll(entregas.findByTareaId(id));
        tareas.delete(tarea);
    }

    public List<TareaEstudianteResponse> listarParaEstudiante(Long estudianteId) {
        seguridad.validarAccesoEstudiante(estudianteId);
        Estudiante estudiante = estudiante(estudianteId);

        List<TareaEstudianteResponse> resultado = new ArrayList<>();
        for (Materia materia : estudiante.getMaterias()) {
            for (Tarea tarea : tareas.findByMateriaIdOrderByFechaLimiteAsc(materia.getId())) {
                if (!tarea.isActiva()) {
                    continue;
                }
                EntregaTarea entrega = entregas
                        .findByTareaIdAndEstudianteId(tarea.getId(), estudianteId)
                        .orElse(null);
                resultado.add(TareaEstudianteResponse.desde(tarea, entrega));
            }
        }
        resultado.sort(Comparator.comparing(TareaEstudianteResponse::fechaLimite));
        return resultado;
    }

    @Transactional
    public EntregaTareaResponse marcarEntregada(Long tareaId, EntregaTareaRequest request) {
        Estudiante estudiante = seguridad.estudianteActual();
        Tarea tarea = entidad(tareaId);

        if (!tarea.getMateria().getEstudiantes().contains(estudiante)) {
            throw new AccesoDenegadoException(
                    "El estudiante no esta inscrito en la materia de la tarea");
        }

        EntregaTarea entrega = entregas.findByTareaIdAndEstudianteId(tareaId, estudiante.getId())
                .orElseGet(() -> new EntregaTarea(tarea, estudiante));

        if (entrega.getFechaEntrega() != null) {
            throw new ReglaNegocioException("La tarea ya fue entregada");
        }

        LocalDateTime ahora = LocalDateTime.now();
        entrega.setEstado(EstadoTarea.COMPLETADA);
        entrega.setFechaEntrega(ahora);
        entrega.setComentario(request != null ? request.comentario() : null);
        entrega = entregas.save(entrega);

        if (!ahora.isAfter(tarea.getFechaLimite())) {
            gamificacion.otorgar(estudiante, MotivoPuntos.ENTREGA_TAREA_A_TIEMPO,
                    "Entrega a tiempo de la tarea: " + tarea.getTitulo(), tarea.getMateria());
            if (!ahora.isAfter(tarea.getFechaLimite().minusDays(1))) {
                gamificacion.otorgar(estudiante, MotivoPuntos.BONIFICACION_ANTICIPADA,
                        "Entrega anticipada de la tarea: " + tarea.getTitulo(), tarea.getMateria());
            }
        }

        notificaciones.crear(
                tarea.getProfesor(),
                "Tarea entregada",
                estudiante.getNombreCompleto() + " entrego la tarea '" + tarea.getTitulo() + "'.",
                TipoNotificacion.TAREA,
                tarea.getId()
        );
        return EntregaTareaResponse.from(entrega);
    }

    public List<EntregaTareaResponse> entregasDe(Long tareaId) {
        Tarea tarea = entidad(tareaId);
        validarProfesor(tarea);
        return entregas.findByTareaId(tareaId).stream()
                .map(EntregaTareaResponse::from)
                .toList();
    }

    /**
     * Alerta de una tarea: estudiantes que no han entregado y ya superaron
     * la fecha limite. Se usa en el panel del profesor.
     */
    public List<AlertaAtrasoResponse> alertasDeTarea(Long tareaId) {
        Tarea tarea = entidad(tareaId);
        validarProfesor(tarea);
        List<AlertaAtrasoResponse> alertas = new ArrayList<>();

        for (Estudiante estudiante : tarea.getMateria().getEstudiantes()) {
            boolean sinEntrega = entregas
                    .findByTareaIdAndEstudianteId(tareaId, estudiante.getId())
                    .filter(entrega -> entrega.getFechaEntrega() != null)
                    .isEmpty();
            if (sinEntrega && LocalDateTime.now().isAfter(tarea.getFechaLimite())) {
                alertas.add(new AlertaAtrasoResponse(
                        estudiante.getId(),
                        estudiante.getNombreCompleto(),
                        1,
                        0,
                        List.of(tarea.getTitulo())
                ));
            }
        }
        return alertas;
    }

    /** Alerta acumulada por estudiante para todas las tareas de un profesor. */
    public List<AlertaAtrasoResponse> alertasAcumuladas(Long materiaId) {
        Long profesorId = seguridad.esProfesor() ? seguridad.usuarioActual().getId() : null;
        List<Tarea> lista = materiaId != null
                ? tareas.findByMateriaIdOrderByFechaLimiteAsc(materiaId)
                : (profesorId != null
                    ? tareas.findByMateriaProfesorIdOrderByFechaLimiteAsc(profesorId)
                    : tareas.findAll());

        Map<Long, Estudiante> estudiantesPorId = new LinkedHashMap<>();
        Map<Long, List<String>> atrasadas = new LinkedHashMap<>();
        Map<Long, List<String>> pendientes = new LinkedHashMap<>();

        for (Tarea tarea : lista) {
            for (Estudiante estudiante : tarea.getMateria().getEstudiantes()) {
                estudiantesPorId.putIfAbsent(estudiante.getId(), estudiante);
                boolean sinEntrega = entregas
                        .findByTareaIdAndEstudianteId(tarea.getId(), estudiante.getId())
                        .filter(entrega -> entrega.getFechaEntrega() != null)
                        .isEmpty();
                if (!sinEntrega) {
                    continue;
                }
                if (LocalDateTime.now().isAfter(tarea.getFechaLimite())) {
                    atrasadas.computeIfAbsent(estudiante.getId(), clave -> new ArrayList<>())
                            .add(tarea.getTitulo());
                } else {
                    pendientes.computeIfAbsent(estudiante.getId(), clave -> new ArrayList<>())
                            .add(tarea.getTitulo());
                }
            }
        }

        return estudiantesPorId.values().stream()
                .map(estudiante -> new AlertaAtrasoResponse(
                        estudiante.getId(),
                        estudiante.getNombreCompleto(),
                        atrasadas.getOrDefault(estudiante.getId(), List.of()).size(),
                        pendientes.getOrDefault(estudiante.getId(), List.of()).size(),
                        atrasadas.getOrDefault(estudiante.getId(), List.of())
                ))
                .filter(alerta -> alerta.tareasAtrasadas() > 0 || alerta.tareasPendientes() > 0)
                .sorted(Comparator.comparingLong(AlertaAtrasoResponse::tareasAtrasadas).reversed())
                .toList();
    }

    private Usuario resolverProfesor(Materia materia) {
        Usuario actual = seguridad.usuarioActual();
        if (actual.getRol() == Rol.ADMINISTRADOR) {
            return materia.getProfesor() != null ? materia.getProfesor() : actual;
        }
        if (materia.getProfesor() == null
                || !materia.getProfesor().getId().equals(actual.getId())) {
            throw new AccesoDenegadoException(
                    "Solo el profesor asignado puede gestionar las tareas de esta materia");
        }
        return actual;
    }

    private void validarProfesor(Tarea tarea) {
        if (seguridad.esAdministrador()) {
            return;
        }
        if (!seguridad.esProfesor()
                || !tarea.getProfesor().getId().equals(seguridad.usuarioActual().getId())) {
            throw new AccesoDenegadoException(
                    "Solo el profesor que creo la tarea puede modificarla");
        }
    }

    private Tarea entidad(Long id) {
        return tareas.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la tarea con id " + id));
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
}
