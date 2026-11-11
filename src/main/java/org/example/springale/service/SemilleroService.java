package org.example.springale.service;

import org.example.springale.dto.EstudianteResponse;
import org.example.springale.dto.SemilleroRequest;
import org.example.springale.dto.SemilleroResponse;
import org.example.springale.entity.Semillero;
import org.example.springale.repository.EstudianteRepository;
import org.example.springale.repository.SemilleroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class SemilleroService {

    private final SemilleroRepository semilleros;
    private final EstudianteRepository estudiantes;

    public SemilleroService(SemilleroRepository semilleros, EstudianteRepository estudiantes) {
        this.semilleros = semilleros;
        this.estudiantes = estudiantes;
    }

    public List<SemilleroResponse> listar() {
        return semilleros.findAll().stream().map(this::toResponse).toList();
    }

    public SemilleroResponse buscar(Long id) {
        return toResponse(entidad(id));
    }

    public List<SemilleroResponse> buscarPorNombre(String nombre) {
        return semilleros.findByNombreContainingIgnoreCase(nombre).stream().map(this::toResponse).toList();
    }

    public List<EstudianteResponse> estudiantesDe(Long id) {
        entidad(id);
        return estudiantes.findBySemilleroId(id).stream().map(EstudianteResponse::from).toList();
    }

    @Transactional
    public SemilleroResponse crear(SemilleroRequest request) {
        Semillero semillero = new Semillero();
        aplicar(semillero, request);
        return toResponse(semilleros.save(semillero));
    }

    @Transactional
    public SemilleroResponse actualizar(Long id, SemilleroRequest request) {
        Semillero semillero = entidad(id);
        aplicar(semillero, request);
        return toResponse(semilleros.save(semillero));
    }

    @Transactional
    public void eliminar(Long id) {
        semilleros.delete(entidad(id));
    }

    private void aplicar(Semillero semillero, SemilleroRequest request) {
        if (request.nombre() == null || request.nombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del semillero es obligatorio");
        }
        semillero.setNombre(request.nombre().trim());
        semillero.setDescripcion(request.descripcion());
    }

    private Semillero entidad(Long id) {
        return semilleros.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe el semillero con id " + id));
    }

    private SemilleroResponse toResponse(Semillero semillero) {
        return SemilleroResponse.from(semillero, estudiantes.countBySemilleroId(semillero.getId()));
    }
}
