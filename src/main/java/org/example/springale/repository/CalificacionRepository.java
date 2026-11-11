package org.example.springale.repository;

import org.example.springale.entity.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/** Acceso a datos de calificaciones. */
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

    List<Calificacion> findAllByOrderByFechaDesc();

    List<Calificacion> findByEstudianteIdOrderByFechaDesc(Long estudianteId);

    List<Calificacion> findByEstudianteIdAndMateriaIdOrderByFechaDesc(Long estudianteId, Long materiaId);

    List<Calificacion> findByMateriaIdOrderByFechaDesc(Long materiaId);

    List<Calificacion> findByEstudianteIdAndMateriaNombreContainingIgnoreCaseOrderByFechaDesc(
            Long estudianteId, String materia);

    @Query("select avg(c.nota) from Calificacion c where c.estudiante.id = :estudianteId")
    Double promedioEstudiante(@Param("estudianteId") Long estudianteId);

    @Query("""
            select c.materia.nombre, avg(c.nota)
            from Calificacion c
            where c.estudiante.id = :estudianteId
            group by c.materia.id, c.materia.nombre
            """)
    List<Object[]> promedioPorMateria(@Param("estudianteId") Long estudianteId);

    @Query("""
            select avg(c.nota)
            from Calificacion c
            where c.estudiante.id = :estudianteId and c.materia.id = :materiaId
            """)
    Double promedioEstudianteEnMateria(@Param("estudianteId") Long estudianteId,
                                       @Param("materiaId") Long materiaId);
}
