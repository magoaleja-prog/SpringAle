package org.example.springale.repository;

import org.example.springale.entity.LlegadaTardia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/** Acceso a datos de llegadas tardias. */
public interface LlegadaTardiaRepository extends JpaRepository<LlegadaTardia, Long> {

    List<LlegadaTardia> findAllByOrderByFechaDescHoraDesc();

    List<LlegadaTardia> findByEstudianteIdOrderByFechaDescHoraDesc(Long estudianteId);

    List<LlegadaTardia> findByEstudianteIdAndFechaBetweenOrderByFechaDescHoraDesc(
            Long estudianteId, LocalDate desde, LocalDate hasta);

    long countByEstudianteId(Long estudianteId);
}
