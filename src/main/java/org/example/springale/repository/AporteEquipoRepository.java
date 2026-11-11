package org.example.springale.repository;

import org.example.springale.entity.AporteEquipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Acceso a datos de aportes individuales de un proyecto. */
public interface AporteEquipoRepository extends JpaRepository<AporteEquipo, Long> {

    List<AporteEquipo> findByProyectoIdOrderByFechaAsc(Long proyectoId);

    List<AporteEquipo> findByProyectoIdAndEstudianteId(Long proyectoId, Long estudianteId);

    Optional<AporteEquipo> findFirstByProyectoIdAndEstudianteIdOrderByFechaDesc(
            Long proyectoId, Long estudianteId);
}
