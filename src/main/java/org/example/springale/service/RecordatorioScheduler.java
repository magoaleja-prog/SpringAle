package org.example.springale.service;

import org.example.springale.entity.EntregaTarea;
import org.example.springale.entity.Estudiante;
import org.example.springale.entity.Tarea;
import org.example.springale.entity.TipoNotificacion;
import org.example.springale.repository.EntregaTareaRepository;
import org.example.springale.repository.TareaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Job programado que crea recordatorios internos para las tareas que estan
 * por vencer. Evita duplicados consultando el titulo y la referencia.
 */
@Component
public class RecordatorioScheduler {

    private static final String TITULO_RECORDATORIO = "Recordatorio: tarea por vencer";

    private final TareaRepository tareas;
    private final EntregaTareaRepository entregas;
    private final NotificacionService notificaciones;
    private final long horasAntes;

    public RecordatorioScheduler(TareaRepository tareas,
                                 EntregaTareaRepository entregas,
                                 NotificacionService notificaciones,
                                 @Value("${app.recordatorios.horas-antes:24}") long horasAntes) {
        this.tareas = tareas;
        this.entregas = entregas;
        this.notificaciones = notificaciones;
        this.horasAntes = horasAntes;
    }

    @Scheduled(fixedDelayString = "${app.recordatorios.intervalo-ms:3600000}")
    @Transactional
    public void recordarTareasPorVencer() {
        LocalDateTime ahora = LocalDateTime.now();
        List<Tarea> porVencer = tareas.findByActivaTrueAndFechaLimiteBetween(
                ahora, ahora.plusHours(horasAntes));

        for (Tarea tarea : porVencer) {
            for (Estudiante estudiante : tarea.getMateria().getEstudiantes()) {
                if (estudiante.getUsuario() == null) {
                    continue;
                }
                boolean yaEntrego = entregas
                        .findByTareaIdAndEstudianteId(tarea.getId(), estudiante.getId())
                        .filter(entrega -> entrega.getFechaEntrega() != null)
                        .isPresent();
                if (yaEntrego) {
                    continue;
                }
                boolean yaNotificado = notificaciones.existeRecordatorio(
                        estudiante.getUsuario().getId(), tarea.getId());
                if (!yaNotificado) {
                    notificaciones.crear(
                            estudiante.getUsuario(),
                            TITULO_RECORDATORIO,
                            "La tarea '" + tarea.getTitulo() + "' de "
                                    + tarea.getMateria().getNombre()
                                    + " vence el " + tarea.getFechaLimite() + ".",
                            TipoNotificacion.TAREA,
                            tarea.getId()
                    );
                }
            }
        }
    }
}
