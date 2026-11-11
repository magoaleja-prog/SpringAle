package org.example.springale.service;

import org.example.springale.dto.AporteEquipoRequest;
import org.example.springale.dto.AporteEquipoResponse;
import org.example.springale.dto.CalificarProyectoRequest;
import org.example.springale.dto.EntregaFinalRequest;
import org.example.springale.dto.EstudianteResumen;
import org.example.springale.dto.ProyectoConsolidadoResponse;
import org.example.springale.dto.ProyectoEquipoRequest;
import org.example.springale.dto.ProyectoEquipoResponse;
import org.example.springale.entity.AporteEquipo;
import org.example.springale.entity.EstadoProyecto;
import org.example.springale.entity.Estudiante;
import org.example.springale.entity.Materia;
import org.example.springale.entity.MotivoPuntos;
import org.example.springale.entity.ProyectoEquipo;
import org.example.springale.entity.Rol;
import org.example.springale.entity.TipoNotificacion;
import org.example.springale.entity.Usuario;
import org.example.springale.exception.AccesoDenegadoException;
import org.example.springale.exception.RecursoNoEncontradoException;
import org.example.springale.exception.ReglaNegocioException;
import org.example.springale.repository.AporteEquipoRepository;
import org.example.springale.repository.EstudianteRepository;
import org.example.springale.repository.MateriaRepository;
import org.example.springale.repository.ProyectoEquipoRepository;
import org.example.springale.security.SeguridadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Logica del trabajo en equipo:
 * - El profesor crea el proyecto y designa al lider.
 * - Cada integrante sube su aporte individual.
 * - El lider ve la vista consolidada y realiza la entrega final.
 * - El profesor califica el trabajo completo.
 */
@Service
@Transactional(readOnly = true)
public class ProyectoEquipoService {

    private final ProyectoEquipoRepository proyectos;
    private final AporteEquipoRepository aportes;
    private final EstudianteRepository estudiantes;
    private final MateriaRepository materias;
    private final SeguridadService seguridad;
    private final GamificacionService gamificacion;
    private final NotificacionService notificaciones;

    public ProyectoEquipoService(ProyectoEquipoRepository proyectos,
                                 AporteEquipoRepository aportes,
                                 EstudianteRepository estudiantes,
                                 MateriaRepository materias,
                                 SeguridadService seguridad,
                                 GamificacionService gamificacion,
                                 NotificacionService notificaciones) {
        this.proyectos = proyectos;
        this.aportes = aportes;
        this.estudiantes = estudiantes;
        this.materias = materias;
        this.seguridad = seguridad;
        this.gamificacion = gamificacion;
        this.notificaciones = notificaciones;
    }

    public List<ProyectoEquipoResponse> listar(Long materiaId) {
        List<ProyectoEquipo> resultado;
        if (seguridad.esEstudiante()) {
            resultado = proyectos.findByMiembrosId(seguridad.estudianteActual().getId());
        } else if (materiaId != null) {
            resultado = proyectos.findByMateriaId(materiaId);
        } else if (seguridad.esProfesor()) {
            resultado = proyectos.findByProfesorId(seguridad.usuarioActual().getId());
        } else {
            resultado = proyectos.findAll();
        }
        return resultado.stream().map(ProyectoEquipoResponse::from).toList();
    }

    public ProyectoEquipoResponse buscar(Long id) {
        ProyectoEquipo proyecto = entidad(id);
        validarAcceso(proyecto);
        return ProyectoEquipoResponse.from(proyecto);
    }

    @Transactional
    public ProyectoEquipoResponse crear(ProyectoEquipoRequest request) {
        Materia materia = materia(request.materiaId());
        Usuario profesor = resolverProfesor(materia);

        if (request.miembrosIds() == null || request.miembrosIds().isEmpty()) {
            throw new ReglaNegocioException("El proyecto debe tener al menos un integrante");
        }

        Set<Estudiante> miembros = new HashSet<>();
        for (Long id : request.miembrosIds()) {
            miembros.add(estudiante(id));
        }

        Estudiante lider = estudiante(request.liderId());
        if (!materia.getEstudiantes().contains(lider)) {
            throw new ReglaNegocioException("El lider debe estar inscrito en la materia");
        }
        miembros.add(lider);

        ProyectoEquipo proyecto = new ProyectoEquipo(
                request.nombre().trim(),
                request.descripcion().trim(),
                request.fechaLimite(),
                materia,
                profesor,
                lider
        );
        proyecto.setMiembros(miembros);
        proyecto = proyectos.save(proyecto);

        for (Estudiante miembro : miembros) {
            notificaciones.crearParaEstudiante(
                    miembro,
                    "Nuevo proyecto de equipo",
                    "Fue asignado al proyecto '" + proyecto.getNombre()
                            + "'. Lider: " + lider.getNombreCompleto() + ".",
                    TipoNotificacion.EQUIPO,
                    proyecto.getId()
            );
        }
        return ProyectoEquipoResponse.from(proyecto);
    }

    public ProyectoConsolidadoResponse consolidado(Long proyectoId) {
        ProyectoEquipo proyecto = entidad(proyectoId);
        validarAcceso(proyecto);

        List<AporteEquipo> listaAportes = aportes.findByProyectoIdOrderByFechaAsc(proyectoId);
        List<EstudianteResumen> sinAporte = proyecto.getMiembros().stream()
                .filter(miembro -> listaAportes.stream()
                        .noneMatch(aporte -> aporte.getEstudiante().getId().equals(miembro.getId())))
                .map(EstudianteResumen::from)
                .toList();

        return new ProyectoConsolidadoResponse(
                ProyectoEquipoResponse.from(proyecto),
                listaAportes.stream().map(AporteEquipoResponse::from).toList(),
                sinAporte,
                sinAporte.isEmpty()
        );
    }

    @Transactional
    public AporteEquipoResponse agregarAporte(Long proyectoId, AporteEquipoRequest request) {
        ProyectoEquipo proyecto = entidad(proyectoId);
        Estudiante estudiante = seguridad.estudianteActual();

        if (!proyecto.getMiembros().contains(estudiante)) {
            throw new AccesoDenegadoException("Solo los integrantes pueden subir aportes");
        }
        if (proyecto.getEstado() == EstadoProyecto.ENTREGADO
                || proyecto.getEstado() == EstadoProyecto.CALIFICADO) {
            throw new ReglaNegocioException("El proyecto ya fue entregado o calificado");
        }

        boolean primerAporte = aportes
                .findFirstByProyectoIdAndEstudianteIdOrderByFechaDesc(proyectoId, estudiante.getId())
                .isEmpty();

        AporteEquipo aporte = new AporteEquipo(
                request.tipo(),
                request.contenido().trim(),
                request.comentario(),
                proyecto,
                estudiante
        );
        aporte = aportes.save(aporte);

        if (primerAporte) {
            gamificacion.otorgar(estudiante, MotivoPuntos.APORTE_EQUIPO,
                    "Aporte al proyecto: " + proyecto.getNombre(), proyecto.getMateria());
        }

        proyecto.setEstado(EstadoProyecto.EN_PROGRESO);
        proyectos.save(proyecto);

        notificaciones.crear(
                proyecto.getProfesor(),
                "Nuevo aporte de equipo",
                estudiante.getNombreCompleto() + " subio un aporte al proyecto '"
                        + proyecto.getNombre() + "'.",
                TipoNotificacion.EQUIPO,
                proyecto.getId()
        );
        return AporteEquipoResponse.from(aporte);
    }

    @Transactional
    public ProyectoEquipoResponse entregaFinal(Long proyectoId, EntregaFinalRequest request) {
        ProyectoEquipo proyecto = entidad(proyectoId);
        Estudiante actual = seguridad.estudianteActual();

        if (!proyecto.getLider().getId().equals(actual.getId())) {
            throw new AccesoDenegadoException("Solo el lider puede realizar la entrega final");
        }
        if (proyecto.getEstado() == EstadoProyecto.CALIFICADO) {
            throw new ReglaNegocioException("El proyecto ya fue calificado");
        }

        List<AporteEquipo> listaAportes = aportes.findByProyectoIdOrderByFechaAsc(proyectoId);
        boolean todosAportaron = proyecto.getMiembros().stream()
                .allMatch(miembro -> listaAportes.stream()
                        .anyMatch(aporte -> aporte.getEstudiante().getId().equals(miembro.getId())));
        if (!todosAportaron) {
            throw new ReglaNegocioException(
                    "Todos los integrantes deben subir al menos un aporte antes de la entrega final");
        }

        proyecto.setEntregaFinalContenido(request.contenido().trim());
        proyecto.setFechaEntregaFinal(LocalDateTime.now());
        proyecto.setEstado(EstadoProyecto.ENTREGADO);
        proyecto = proyectos.save(proyecto);

        notificaciones.crear(
                proyecto.getProfesor(),
                "Entrega final de proyecto",
                "El lider " + actual.getNombreCompleto() + " entrego el proyecto '"
                        + proyecto.getNombre() + "'.",
                TipoNotificacion.EQUIPO,
                proyecto.getId()
        );
        return ProyectoEquipoResponse.from(proyecto);
    }

    @Transactional
    public ProyectoEquipoResponse calificar(Long proyectoId, CalificarProyectoRequest request) {
        ProyectoEquipo proyecto = entidad(proyectoId);
        if (seguridad.esProfesor()
                && !proyecto.getProfesor().getId().equals(seguridad.usuarioActual().getId())) {
            throw new AccesoDenegadoException("Solo el profesor del proyecto puede calificarlo");
        }

        proyecto.setCalificacion(request.calificacion());
        proyecto.setComentarioProfesor(request.comentario());
        proyecto.setEstado(EstadoProyecto.CALIFICADO);
        proyecto = proyectos.save(proyecto);

        for (Estudiante miembro : proyecto.getMiembros()) {
            notificaciones.crearParaEstudiante(
                    miembro,
                    "Proyecto calificado",
                    "El proyecto '" + proyecto.getNombre() + "' obtuvo " + request.calificacion()
                            + ". " + (request.comentario() != null ? request.comentario() : ""),
                    TipoNotificacion.EQUIPO,
                    proyecto.getId()
            );
        }
        return ProyectoEquipoResponse.from(proyecto);
    }

    @Transactional
    public void eliminar(Long id) {
        ProyectoEquipo proyecto = entidad(id);
        if (seguridad.esProfesor()
                && !proyecto.getProfesor().getId().equals(seguridad.usuarioActual().getId())) {
            throw new AccesoDenegadoException("Solo el profesor del proyecto puede eliminarlo");
        }
        aportes.deleteAll(aportes.findByProyectoIdOrderByFechaAsc(id));
        proyectos.delete(proyecto);
    }

    private void validarAcceso(ProyectoEquipo proyecto) {
        if (seguridad.esAdministrador()) {
            return;
        }
        if (seguridad.esProfesor()) {
            if (!proyecto.getProfesor().getId().equals(seguridad.usuarioActual().getId())) {
                throw new AccesoDenegadoException("No tiene acceso a este proyecto");
            }
            return;
        }
        Long estudianteId = seguridad.estudianteActual().getId();
        boolean participa = proyecto.getLider().getId().equals(estudianteId)
                || proyecto.getMiembros().stream()
                    .anyMatch(miembro -> miembro.getId().equals(estudianteId));
        if (!participa) {
            throw new AccesoDenegadoException("No pertenece a este proyecto");
        }
    }

    private Usuario resolverProfesor(Materia materia) {
        Usuario actual = seguridad.usuarioActual();
        if (actual.getRol() == Rol.ADMINISTRADOR) {
            return materia.getProfesor() != null ? materia.getProfesor() : actual;
        }
        if (materia.getProfesor() == null
                || !materia.getProfesor().getId().equals(actual.getId())) {
            throw new AccesoDenegadoException(
                    "Solo el profesor asignado puede crear proyectos en esta materia");
        }
        return actual;
    }

    private ProyectoEquipo entidad(Long id) {
        return proyectos.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el proyecto con id " + id));
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
