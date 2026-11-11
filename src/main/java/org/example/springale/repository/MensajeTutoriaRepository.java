package org.example.springale.repository;

import org.example.springale.entity.MensajeTutoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Acceso a datos del chat de una tutoria. */
public interface MensajeTutoriaRepository extends JpaRepository<MensajeTutoria, Long> {

    List<MensajeTutoria> findByTutoriaIdOrderByFechaAsc(Long tutoriaId);
}
