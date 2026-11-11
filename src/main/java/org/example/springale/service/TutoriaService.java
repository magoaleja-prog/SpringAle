package org.example.springale.service;

import org.example.springale.dto.CambiarEstadoTutoriaRequest;
import org.example.springale.dto.MensajeTutoriaRequest;
import org.example.springale.dto.MensajeTutoriaResponse;
import org.example.springale.dto.PostulacionTutorRequest;
import org.example.springale.dto.PostulacionTutorResponse;
import org.example.springale.dto.TutoriaRequest;
import org.example.springale.dto.TutoriaResponse;
import org.example.springale.entity.EstadoPostulacion;
import org.example.springale.entity.EstadoTutoria;
import org.example.springale.entity.Estudiante;
import org.example.springale.entity.Materia;
import org.example.springale.entity.MensajeTutoria;
import org.example.springale.entity.MotivoPuntos;
import org.example.springale.entity.PostulacionTutor;
import org.example.springale.entity.TipoNotificacion;
import org.example.springale.entity.Tutoria;
import org.example.springale.exception.AccesoDenegadoException;
import org.example.springale.exception.RecursoNoEncontradoException;
import org.example.springale.exception.ReglaNegocioException;
import org.example.springale.repository.CalificacionRepository;
import org.example.springale.repository.EstudianteRepository;
import org.example.springale.repository.MateriaRepository;
import org.example.springale.repository.MensajeTutoriaRepository;
import org.example.springale.repository.PostulacionTutorRepository;
import org.example.springale.repository.TutoriaRepository;
import org.example.springale.security.SeguridadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Sistema de tutorias entre estudiantes.
 *
 * Flujo:
 * 1. Un estudiante con buen promedio se postula como tutor de una materia.
 * 2. Otro estudiante solicita una tutoria indicando tema y fecha.
 * 3. El tutor acepta o rechaza.
 * 4. Al aceptar se habilita el chat interno.
 * 5. Al completar, ambos ganan puntos y se reconoce la mejora academica.
 */
@Service
@Transactional(readOnly = true)
public class TutoriaService {

    private static final double PROMEDIO_MINIMO_POSTULACION = 3.8;
    private static final double PROMEDIO_APROBACION_AUTOMATICA = 4.0;

    private final TutoriaRepository tutorias;
    private final PostulacionTutorRepository postulaciones;
    private final MensajeTutoriaRepository mensajes;
    private final EstudianteRepository estudiantes;
    private final MateriaRepository materias;
    private final CalificacionRepository calificaciones;
    private final SeguridadService seguridad;
    private final GamificacionService gamificacion;
    private final NotificacionService notificaciones;

    public TutoriaService(TutoriaRepository tutorias,
                          PostulacionTutorRepository postulaciones,
                          MensajeTutoriaRepository mensajes,
                          EstudianteRepository estudiantes,
                          MateriaRepository materias,
                          CalificacionRepository calificaciones,
                          SeguridadService seguridad,
                          GamificacionService gamificacion,
                          NotificacionService notificaciones) {
        this.tutorias = tutorias;
        this.postulaciones = postulaciones;
        this.mensajes = mensajes;
        this.estudiantes = estudiantes;
        this.materias = materias;
        this.calificaciones = calificaciones;
        this.seguridad = seguridad;
        this.gamificacion = gamificacion;
        this.notificaciones = notificaciones;
    }

    @Transactional
    public PostulacionTutorResponse postular(PostulacionTutorRequest request) {
        Estudiante estudiante = seguridad.estudianteActual();
        Materia materia = materia(request.materiaId());

        if (postulaciones.findByEstudianteIdAndMateriaId(estudiante.getId(), materia.getId()).isPresent()) {
            throw new ReglaNegocioException("Ya existe una postulacion para esta materia");
        }

        Double promedio = calificaciones.promedioEstudianteEnMateria(
                estudiante.getId(), materia.getId());
        if (promedio == null) {
            throw new ReglaNegocioException(
                    "Debe tener al menos una nota registrada en la materia para postularse");
        }
        if (promedio < PROMEDIO_MINIMO_POSTULACION) {
            throw new ReglaNegocioException(
                    "El promedio minimo para postularse como tutor es "
                            + PROMEDIO_MINIMO_POSTULACION + ". Su promedio es " + promedio);
        }

        PostulacionTutor postulacion = new PostulacionTutor(
                request.disponibilidad().trim(), estudiante, materia);
        if (promedio >= PROMEDIO_APROBACION_AUTOMATICA) {
            postulacion.setEstado(EstadoPostulacion.APROBADA);
        }
        return PostulacionTutorResponse.from(postulaciones.save(postulacion));
    }

    public List<PostulacionTutorResponse> listarPostulaciones(EstadoPostulacion estado) {
        List<PostulacionTutor> resultado;
        if (seguridad.esEstudiante()) {
            resultado = postulaciones.findByEstudianteId(seguridad.estudianteActual().getId());
        } else if (estado != null) {
            resultado = postulaciones.findByEstado(estado);
        } else {
            resultado = postulaciones.findAll();
        }
        return resultado.stream().map(PostulacionTutorResponse::from).toList();
    }

    @Transactional
    public PostulacionTutorResponse cambiarEstadoPostulacion(Long id, EstadoPostulacion estado) {
        PostulacionTutor postulacion = postulaciones.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la postulacion con id " + id));
        if (postulacion.getEstado() != EstadoPostulacion.PENDIENTE) {
            throw new ReglaNegocioException("La postulacion ya fue evaluada");
        }
        if (estado != EstadoPostulacion.APROBADA && estado != EstadoPostulacion.RECHAZADA) {
            throw new ReglaNegocioException("Solo se puede aprobar o rechazar una postulacion");
        }
        postulacion.setEstado(estado);
        return PostulacionTutorResponse.from(postulaciones.save(postulacion));
    }

    public List<PostulacionTutorResponse> tutoresPorMateria(Long materiaId) {
        materia(materiaId);
        return postulaciones.findByMateriaIdAndEstado(materiaId, EstadoPostulacion.APROBADA)
                .stream()
                .map(PostulacionTutorResponse::from)
                .toList();
    }

    @Transactional
    public TutoriaResponse solicitar(TutoriaRequest request) {
        Estudiante aprendiz = seguridad.estudianteActual();
        Estudiante tutor = estudiante(request.tutorId());
        Materia materia = materia(request.materiaId());

        if (tutor.getId().equals(aprendiz.getId())) {
            throw new ReglaNegocioException("No puede solicitar una tutoria a si mismo");
        }
        boolean tutorAprobado = postulaciones.existsByEstudianteIdAndMateriaIdAndEstado(
                tutor.getId(), materia.getId(), EstadoPostulacion.APROBADA);
        if (!tutorAprobado) {
            throw new ReglaNegocioException("El estudiante no es tutor aprobado de esta materia");
        }

        Tutoria tutoria = new Tutoria(
                request.tema().trim(),
                request.descripcion(),
                request.fechaTutoria(),
                tutor,
                aprendiz,
                materia
        );
        tutoria.setNotaAntes(calificaciones.promedioEstudianteEnMateria(
                aprendiz.getId(), materia.getId()));
        tutoria = tutorias.save(tutoria);

        notificaciones.crearParaEstudiante(
                tutor,
                "Nueva solicitud de tutoria",
                aprendiz.getNombreCompleto() + " solicito una tutoria de "
                        + materia.getNombre() + ": " + tutoria.getTema() + ".",
                TipoNotificacion.TUTORIA,
                tutoria.getId()
        );
        return TutoriaResponse.from(tutoria);
    }

    public List<TutoriaResponse> listar(EstadoTutoria estado) {
        if (seguridad.esEstudiante()) {
            Estudiante estudiante = seguridad.estudianteActual();
            List<Tutoria> comoTutor = estado != null
                    ? tutorias.findByEstudianteTutorIdAndEstadoOrderByFechaSolicitudDesc(
                            estudiante.getId(), estado)
                    : tutorias.findByEstudianteTutorIdOrderByFechaSolicitudDesc(estudiante.getId());
            List<Tutoria> comoAprendiz = estado != null
                    ? tutorias.findByEstudianteAprendizIdAndEstadoOrderByFechaSolicitudDesc(
                            estudiante.getId(), estado)
                    : tutorias.findByEstudianteAprendizIdOrderByFechaSolicitudDesc(estudiante.getId());
            return java.util.stream.Stream.concat(comoTutor.stream(), comoAprendiz.stream())
                    .distinct()
                    .map(TutoriaResponse::from)
                    .toList();
        }
        List<Tutoria> resultado = estado != null
                ? tutorias.findByEstadoOrderByFechaSolicitudDesc(estado)
                : tutorias.findAllByOrderByFechaSolicitudDesc();
        return resultado.stream().map(TutoriaResponse::from).toList();
    }

    public TutoriaResponse buscar(Long id) {
        Tutoria tutoria = entidad(id);
        validarParticipante(tutoria);
        return TutoriaResponse.from(tutoria);
    }

    public List<TutoriaResponse> porTutor(Long tutorId, EstadoTutoria estado) {
        seguridad.validarAccesoEstudiante(tutorId);
        List<Tutoria> resultado = estado != null
                ? tutorias.findByEstudianteTutorIdAndEstadoOrderByFechaSolicitudDesc(tutorId, estado)
                : tutorias.findByEstudianteTutorIdOrderByFechaSolicitudDesc(tutorId);
        return resultado.stream().map(TutoriaResponse::from).toList();
    }

    public List<TutoriaResponse> porAprendiz(Long aprendizId, EstadoTutoria estado) {
        seguridad.validarAccesoEstudiante(aprendizId);
        List<Tutoria> resultado = estado != null
                ? tutorias.findByEstudianteAprendizIdAndEstadoOrderByFechaSolicitudDesc(aprendizId, estado)
                : tutorias.findByEstudianteAprendizIdOrderByFechaSolicitudDesc(aprendizId);
        return resultado.stream().map(TutoriaResponse::from).toList();
    }

    public List<TutoriaResponse> historial(Long estudianteId) {
        seguridad.validarAccesoEstudiante(estudianteId);
        return tutorias.findByEstudianteAprendizIdAndEstadoOrderByFechaSolicitudDesc(
                        estudianteId, EstadoTutoria.COMPLETADA)
                .stream()
                .map(TutoriaResponse::from)
                .toList();
    }

    @Transactional
    public TutoriaResponse cambiarEstado(Long id, CambiarEstadoTutoriaRequest request) {
        Tutoria tutoria = entidad(id);
        Estudiante actual = seguridad.estudianteActual();
        EstadoTutoria nuevo = request.estado();
        EstadoTutoria anterior = tutoria.getEstado();

        if (anterior == nuevo) {
            return TutoriaResponse.from(tutoria);
        }

        boolean esTutor = tutoria.getEstudianteTutor().getId().equals(actual.getId());
        boolean esAprendiz = tutoria.getEstudianteAprendiz().getId().equals(actual.getId());
        if (!esTutor && !esAprendiz) {
            throw new AccesoDenegadoException("No participa en esta tutoria");
        }

        switch (anterior) {
            case PENDIENTE -> {
                if (!esTutor) {
                    throw new AccesoDenegadoException("Solo el tutor puede aceptar o rechazar");
                }
                if (nuevo != EstadoTutoria.ACEPTADA && nuevo != EstadoTutoria.RECHAZADA) {
                    throw new ReglaNegocioException(
                            "Una tutoria pendiente solo puede aceptarse o rechazarse");
                }
            }
            case ACEPTADA -> {
                if (nuevo != EstadoTutoria.COMPLETADA && nuevo != EstadoTutoria.CANCELADA) {
                    throw new ReglaNegocioException(
                            "Una tutoria aceptada solo puede completarse o cancelarse");
                }
            }
            case RECHAZADA, COMPLETADA, CANCELADA -> throw new ReglaNegocioException(
                    "La tutoria ya esta cerrada y no puede cambiar de estado");
        }

        tutoria.setEstado(nuevo);

        if (nuevo == EstadoTutoria.ACEPTADA) {
            notificaciones.crearParaEstudiante(
                    tutoria.getEstudianteAprendiz(),
                    "Tutoria aceptada",
                    "El tutor " + tutoria.getEstudianteTutor().getNombreCompleto()
                            + " acepto la tutoria. Ya pueden coordinar por el chat.",
                    TipoNotificacion.TUTORIA,
                    tutoria.getId()
            );
        }
        if (nuevo == EstadoTutoria.RECHAZADA) {
            notificaciones.crearParaEstudiante(
                    tutoria.getEstudianteAprendiz(),
                    "Tutoria rechazada",
                    "El tutor rechazo la solicitud de tutoria de " + tutoria.getMateria().getNombre() + ".",
                    TipoNotificacion.TUTORIA,
                    tutoria.getId()
            );
        }
        if (nuevo == EstadoTutoria.COMPLETADA) {
            otorgarPuntosPorTutoria(tutoria);
        }

        return TutoriaResponse.from(tutorias.save(tutoria));
    }

    @Transactional
    public MensajeTutoriaResponse enviarMensaje(Long tutoriaId, MensajeTutoriaRequest request) {
        Tutoria tutoria = entidad(tutoriaId);
        Estudiante autor = seguridad.estudianteActual();

        if (!tutoria.getEstudianteTutor().getId().equals(autor.getId())
                && !tutoria.getEstudianteAprendiz().getId().equals(autor.getId())) {
            throw new AccesoDenegadoException("No participa en esta tutoria");
        }
        if (tutoria.getEstado() != EstadoTutoria.ACEPTADA) {
            throw new ReglaNegocioException(
                    "El chat solo esta disponible cuando la tutoria fue aceptada");
        }

        MensajeTutoria mensaje = mensajes.save(
                new MensajeTutoria(request.contenido().trim(), tutoria, autor));

        Estudiante destinatario = tutoria.getEstudianteTutor().getId().equals(autor.getId())
                ? tutoria.getEstudianteAprendiz()
                : tutoria.getEstudianteTutor();
        notificaciones.crearParaEstudiante(
                destinatario,
                "Nuevo mensaje de tutoria",
                autor.getNombreCompleto() + ": " + request.contenido(),
                TipoNotificacion.TUTORIA,
                tutoria.getId()
        );
        return MensajeTutoriaResponse.from(mensaje);
    }

    public List<MensajeTutoriaResponse> listarMensajes(Long tutoriaId) {
        Tutoria tutoria = entidad(tutoriaId);
        validarParticipante(tutoria);
        return mensajes.findByTutoriaIdOrderByFechaAsc(tutoriaId)
                .stream()
                .map(MensajeTutoriaResponse::from)
                .toList();
    }

    /**
     * Reconoce la tutoria y, si el aprendiz mejoro su promedio, otorga
     * puntos extra tanto al aprendiz como al tutor.
     */
    private void otorgarPuntosPorTutoria(Tutoria tutoria) {
        gamificacion.otorgar(tutoria.getEstudianteTutor(), MotivoPuntos.TUTORIA_COMPLETADA,
                "Tutoria completada: " + tutoria.getTema(), tutoria.getMateria());
        gamificacion.otorgar(tutoria.getEstudianteAprendiz(), MotivoPuntos.TUTORIA_COMPLETADA,
                "Tutoria recibida: " + tutoria.getTema(), tutoria.getMateria());

        Double promedioActual = calificaciones.promedioEstudianteEnMateria(
                tutoria.getEstudianteAprendiz().getId(), tutoria.getMateria().getId());
        if (tutoria.getNotaAntes() != null && promedioActual != null
                && promedioActual > tutoria.getNotaAntes()) {
            gamificacion.otorgar(tutoria.getEstudianteAprendiz(), MotivoPuntos.BUEN_DESEMPENO,
                    "Mejoro su promedio despues de la tutoria de " + tutoria.getMateria().getNombre(),
                    tutoria.getMateria());
            gamificacion.otorgar(tutoria.getEstudianteTutor(), MotivoPuntos.TUTORIA_COMPLETADA,
                    "Reconocimiento por la mejora de su aprendiz en "
                            + tutoria.getMateria().getNombre(),
                    tutoria.getMateria());
        }

        notificaciones.crearParaEstudiante(
                tutoria.getEstudianteAprendiz(),
                "Tutoria completada",
                "La tutoria '" + tutoria.getTema() + "' fue completada. Ganaste puntos de gamificacion.",
                TipoNotificacion.TUTORIA,
                tutoria.getId()
        );
    }

    private void validarParticipante(Tutoria tutoria) {
        if (seguridad.esAdministrador() || seguridad.esProfesor()) {
            return;
        }
        Long actual = seguridad.estudianteActual().getId();
        if (!tutoria.getEstudianteTutor().getId().equals(actual)
                && !tutoria.getEstudianteAprendiz().getId().equals(actual)) {
            throw new AccesoDenegadoException("No participa en esta tutoria");
        }
    }

    private Tutoria entidad(Long id) {
        return tutorias.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la tutoria con id " + id));
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
