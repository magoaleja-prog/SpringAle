package org.example.springale.repository;

import org.example.springale.entity.PeriodoAcademico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Acceso a datos de periodos academicos. */
public interface PeriodoAcademicoRepository extends JpaRepository<PeriodoAcademico, Long> {

    Optional<PeriodoAcademico> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    List<PeriodoAcademico> findByActivoTrue();
}
