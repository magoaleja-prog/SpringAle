package org.example.springale.dto;

import org.example.springale.entity.Notificacion;
import org.example.springale.entity.TipoNotificacion;

import java.time.LocalDateTime;

/** Notificacion interna de un usuario. */
public record NotificacionResponse(
        Long id,
        String titulo,
        String mensaje,
        TipoNotificacion tipo,
        boolean leida,
        LocalDateTime fecha,
        Long referenciaId
) {

    public static NotificacionResponse from(Notificacion notificacion) {
        return new NotificacionResponse(
                notificacion.getId(),
                notificacion.getTitulo(),
                notificacion.getMensaje(),
                notificacion.getTipo(),
                notificacion.isLeida(),
                notificacion.getFecha(),
                notificacion.getReferenciaId()
        );
    }
}
