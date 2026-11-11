package org.example.springale.repository;

import org.example.springale.entity.Materia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Acceso a datos de materias. */
public interface MateriaRepository extends JpaRepository<Materia, Long> {

    Optional<Materia> findByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCase(String codigo);

    List<Materia> findByProfesorId(Long profesorId);

    List<Materia> findByNombreContainingIgnoreCase(String nombre);
}
