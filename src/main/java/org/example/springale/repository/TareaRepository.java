package org.example.springale.repository;

import org.example.springale.entity.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/** Acceso a datos de tareas. */
public interface TareaRepository extends JpaRepository<Tarea, Long> {

    List<Tarea> findByMateriaIdOrderByFechaLimiteAsc(Long materiaId);

    List<Tarea> findByMateriaProfesorIdOrderByFechaLimiteAsc(Long profesorId);

    List<Tarea> findByActivaTrueOrderByFechaLimiteAsc();

    List<Tarea> findByActivaTrueAndFechaLimiteBetween(LocalDateTime desde, LocalDateTime hasta);
}
