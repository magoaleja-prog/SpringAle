package org.example.springale.repository;

import org.example.springale.entity.EventoPuntos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/** Acceso a datos de movimientos de puntos. */
public interface EventoPuntosRepository extends JpaRepository<EventoPuntos, Long> {

    List<EventoPuntos> findByEstudianteIdOrderByFechaDesc(Long estudianteId);

    List<EventoPuntos> findByEstudianteIdAndMateriaIdOrderByFechaDesc(Long estudianteId, Long materiaId);

    @Query("select coalesce(sum(e.puntos), 0) from EventoPuntos e where e.estudiante.id = :estudianteId")
    int totalPuntosEstudiante(@Param("estudianteId") Long estudianteId);

    @Query("""
            select e.estudiante.id, e.estudiante.nombre, e.estudiante.apellido, coalesce(sum(e.puntos), 0)
            from EventoPuntos e
            group by e.estudiante.id, e.estudiante.nombre, e.estudiante.apellido
            order by coalesce(sum(e.puntos), 0) desc
            """)
    List<Object[]> rankingGeneral();

    @Query("""
            select e.estudiante.id, e.estudiante.nombre, e.estudiante.apellido, coalesce(sum(e.puntos), 0)
            from EventoPuntos e
            where e.materia.id = :materiaId
            group by e.estudiante.id, e.estudiante.nombre, e.estudiante.apellido
            order by coalesce(sum(e.puntos), 0) desc
            """)
    List<Object[]> rankingPorMateria(@Param("materiaId") Long materiaId);
}
