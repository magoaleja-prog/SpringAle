package org.example.springale.service;

import org.example.springale.dto.NotificacionResponse;
import org.example.springale.entity.Estudiante;
import org.example.springale.entity.Notificacion;
import org.example.springale.entity.TipoNotificacion;
import org.example.springale.entity.Usuario;
import org.example.springale.exception.AccesoDenegadoException;
import org.example.springale.exception.RecursoNoEncontradoException;
import org.example.springale.repository.NotificacionRepository;
import org.example.springale.security.SeguridadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Gestiona las notificaciones internas. Es un servicio de apoyo usado por
 * tareas, tutorias, equipos y recordatorios.
 */
@Service
@Transactional(readOnly = true)
public class NotificacionService {

    private final NotificacionRepository notificaciones;
    private final SeguridadService seguridad;

    public NotificacionService(NotificacionRepository notificaciones,
                               SeguridadService seguridad) {
        this.notificaciones = notificaciones;
        this.seguridad = seguridad;
    }

    @Transactional
    public Notificacion crear(Usuario usuario, String titulo, String mensaje,
                              TipoNotificacion tipo, Long referenciaId) {
        return notificaciones.save(new Notificacion(titulo, mensaje, tipo, usuario, referenciaId));
    }

    @Transactional
    public void crearParaEstudiante(Estudiante estudiante, String titulo, String mensaje,
                                    TipoNotificacion tipo, Long referenciaId) {
        if (estudiante != null && estudiante.getUsuario() != null) {
            crear(estudiante.getUsuario(), titulo, mensaje, tipo, referenciaId);
        }
    }

    public List<NotificacionResponse> listarPropias() {
        Long usuarioId = seguridad.usuarioActual().getId();
        return notificaciones.findByUsuarioIdOrderByFechaDesc(usuarioId)
                .stream()
                .map(NotificacionResponse::from)
                .toList();
    }

    public List<NotificacionResponse> listarNoLeidas() {
        Long usuarioId = seguridad.usuarioActual().getId();
        return notificaciones.findByUsuarioIdAndLeidaFalseOrderByFechaDesc(usuarioId)
                .stream()
                .map(NotificacionResponse::from)
                .toList();
    }

    public long contarNoLeidas() {
        return notificaciones.countByUsuarioIdAndLeidaFalse(seguridad.usuarioActual().getId());
    }

    @Transactional
    public NotificacionResponse marcarLeida(Long id) {
        Notificacion notificacion = notificaciones.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la notificacion con id " + id));

        if (!notificacion.getUsuario().getId().equals(seguridad.usuarioActual().getId())) {
            throw new AccesoDenegadoException("La notificacion no pertenece al usuario autenticado");
        }

        notificacion.setLeida(true);
        return NotificacionResponse.from(notificaciones.save(notificacion));
    }

    @Transactional
    public void marcarTodasLeidas() {
        Long usuarioId = seguridad.usuarioActual().getId();
        List<Notificacion> pendientes = notificaciones
                .findByUsuarioIdAndLeidaFalseOrderByFechaDesc(usuarioId);
        pendientes.forEach(notificacion -> notificacion.setLeida(true));
        notificaciones.saveAll(pendientes);
    }

    public boolean existe(Long usuarioId, Long referenciaId, TipoNotificacion tipo) {
        return notificaciones.existsByUsuarioIdAndReferenciaIdAndTipo(
                usuarioId, referenciaId, tipo);
    }

    public boolean existeRecordatorio(Long usuarioId, Long tareaId) {
        return notificaciones.existsByUsuarioIdAndTituloAndReferenciaIdAndTipo(
                usuarioId, "Recordatorio: tarea por vencer", tareaId, TipoNotificacion.TAREA);
    }
}
