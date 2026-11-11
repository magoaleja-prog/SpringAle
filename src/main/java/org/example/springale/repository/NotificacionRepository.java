package org.example.springale.repository;

import org.example.springale.entity.Notificacion;
import org.example.springale.entity.TipoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Acceso a datos de notificaciones internas. */
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    List<Notificacion> findByUsuarioIdAndLeidaFalseOrderByFechaDesc(Long usuarioId);

    long countByUsuarioIdAndLeidaFalse(Long usuarioId);

    boolean existsByUsuarioIdAndReferenciaIdAndTipo(
            Long usuarioId, Long referenciaId, TipoNotificacion tipo);

    boolean existsByUsuarioIdAndTituloAndReferenciaIdAndTipo(
            Long usuarioId, String titulo, Long referenciaId, TipoNotificacion tipo);
}
