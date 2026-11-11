package org.example.springale.repository;

import org.example.springale.entity.EstadoTutoria;
import org.example.springale.entity.Tutoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Acceso a datos de tutorias. */
public interface TutoriaRepository extends JpaRepository<Tutoria, Long> {

    List<Tutoria> findAllByOrderByFechaSolicitudDesc();

    List<Tutoria> findByEstadoOrderByFechaSolicitudDesc(EstadoTutoria estado);

    List<Tutoria> findByEstudianteTutorIdOrderByFechaSolicitudDesc(Long tutorId);

    List<Tutoria> findByEstudianteTutorIdAndEstadoOrderByFechaSolicitudDesc(Long tutorId, EstadoTutoria estado);

    List<Tutoria> findByEstudianteAprendizIdOrderByFechaSolicitudDesc(Long aprendizId);

    List<Tutoria> findByEstudianteAprendizIdAndEstadoOrderByFechaSolicitudDesc(Long aprendizId, EstadoTutoria estado);

    List<Tutoria> findByMateriaIdOrderByFechaSolicitudDesc(Long materiaId);
}
