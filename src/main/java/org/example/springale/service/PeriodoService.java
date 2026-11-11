package org.example.springale.service;

import org.example.springale.dto.PeriodoRequest;
import org.example.springale.dto.PeriodoResponse;
import org.example.springale.entity.PeriodoAcademico;
import org.example.springale.exception.RecursoNoEncontradoException;
import org.example.springale.exception.ReglaNegocioException;
import org.example.springale.repository.PeriodoAcademicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** CRUD de periodos academicos. */
@Service
@Transactional(readOnly = true)
public class PeriodoService {

    private final PeriodoAcademicoRepository periodos;

    public PeriodoService(PeriodoAcademicoRepository periodos) {
        this.periodos = periodos;
    }

    public List<PeriodoResponse> listar() {
        return periodos.findAll().stream().map(PeriodoResponse::from).toList();
    }

    public List<PeriodoResponse> activos() {
        return periodos.findByActivoTrue().stream().map(PeriodoResponse::from).toList();
    }

    public PeriodoResponse buscar(Long id) {
        return PeriodoResponse.from(entidad(id));
    }

    @Transactional
    public PeriodoResponse crear(PeriodoRequest request) {
        validarFechas(request);
        if (periodos.existsByNombreIgnoreCase(request.nombre().trim())) {
            throw new ReglaNegocioException("Ya existe el periodo " + request.nombre());
        }
        PeriodoAcademico periodo = new PeriodoAcademico(
                request.nombre().trim(),
                request.fechaInicio(),
                request.fechaFin(),
                request.activo() == null || request.activo()
        );
        return PeriodoResponse.from(periodos.save(periodo));
    }

    @Transactional
    public PeriodoResponse actualizar(Long id, PeriodoRequest request) {
        validarFechas(request);
        PeriodoAcademico periodo = entidad(id);
        periodos.findByNombreIgnoreCase(request.nombre().trim())
                .filter(otro -> !otro.getId().equals(id))
                .ifPresent(otro -> {
                    throw new ReglaNegocioException("Ya existe el periodo " + request.nombre());
                });
        periodo.setNombre(request.nombre().trim());
        periodo.setFechaInicio(request.fechaInicio());
        periodo.setFechaFin(request.fechaFin());
        periodo.setActivo(request.activo() == null || request.activo());
        return PeriodoResponse.from(periodos.save(periodo));
    }

    @Transactional
    public void eliminar(Long id) {
        periodos.delete(entidad(id));
    }

    private void validarFechas(PeriodoRequest request) {
        if (request.fechaFin().isBefore(request.fechaInicio())) {
            throw new ReglaNegocioException(
                    "La fecha de fin no puede ser anterior a la fecha de inicio");
        }
    }

    private PeriodoAcademico entidad(Long id) {
        return periodos.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el periodo con id " + id));
    }
}
