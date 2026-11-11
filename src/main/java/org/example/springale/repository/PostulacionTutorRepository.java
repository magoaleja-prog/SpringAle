package org.example.springale.repository;

import org.example.springale.entity.EstadoPostulacion;
import org.example.springale.entity.PostulacionTutor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Acceso a datos de postulaciones de tutores. */
public interface PostulacionTutorRepository extends JpaRepository<PostulacionTutor, Long> {

    List<PostulacionTutor> findByEstudianteId(Long estudianteId);

    List<PostulacionTutor> findByEstado(EstadoPostulacion estado);

    List<PostulacionTutor> findByMateriaIdAndEstado(Long materiaId, EstadoPostulacion estado);

    Optional<PostulacionTutor> findByEstudianteIdAndMateriaId(Long estudianteId, Long materiaId);

    boolean existsByEstudianteIdAndMateriaIdAndEstado(
            Long estudianteId, Long materiaId, EstadoPostulacion estado);
}
