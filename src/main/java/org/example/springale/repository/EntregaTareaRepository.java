package org.example.springale.repository;

import org.example.springale.entity.EntregaTarea;
import org.example.springale.entity.EstadoTarea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Acceso a datos de entregas individuales de tareas. */
public interface EntregaTareaRepository extends JpaRepository<EntregaTarea, Long> {

    List<EntregaTarea> findByTareaId(Long tareaId);

    List<EntregaTarea> findByEstudianteId(Long estudianteId);

    Optional<EntregaTarea> findByTareaIdAndEstudianteId(Long tareaId, Long estudianteId);

    List<EntregaTarea> findByTareaIdAndEstado(Long tareaId, EstadoTarea estado);

    long countByEstudianteIdAndEstado(Long estudianteId, EstadoTarea estado);
}
