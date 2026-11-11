package org.example.springale.repository;

import org.example.springale.entity.EstadoProyecto;
import org.example.springale.entity.ProyectoEquipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Acceso a datos de proyectos grupales. */
public interface ProyectoEquipoRepository extends JpaRepository<ProyectoEquipo, Long> {

    List<ProyectoEquipo> findByMateriaId(Long materiaId);

    List<ProyectoEquipo> findByProfesorId(Long profesorId);

    List<ProyectoEquipo> findByMiembrosId(Long estudianteId);

    List<ProyectoEquipo> findByLiderId(Long estudianteId);

    List<ProyectoEquipo> findByEstado(EstadoProyecto estado);
}
