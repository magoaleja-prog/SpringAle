package org.example.springale.repository;

import org.example.springale.entity.Semillero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SemilleroRepository extends JpaRepository<Semillero, Long> {

    List<Semillero> findByNombreContainingIgnoreCase(String nombre);

    Optional<Semillero> findByNombreIgnoreCase(String nombre);

    @Query("select distinct s from Semillero s left join fetch s.estudiantes where s.id = :id")
    Optional<Semillero> buscarConEstudiantes(@Param("id") Long id);
}
