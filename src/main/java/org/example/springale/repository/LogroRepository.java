package org.example.springale.repository;

import org.example.springale.entity.Logro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Acceso a datos del catalogo de logros. */
public interface LogroRepository extends JpaRepository<Logro, Long> {

    List<Logro> findAllByOrderByPuntosRequeridosAsc();
}
