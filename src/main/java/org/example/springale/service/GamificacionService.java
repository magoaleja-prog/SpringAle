package org.example.springale.service;

import org.example.springale.dto.EventoPuntosResponse;
import org.example.springale.dto.LogroResponse;
import org.example.springale.dto.PerfilGamificacionResponse;
import org.example.springale.dto.RankingResponse;
import org.example.springale.entity.Estudiante;
import org.example.springale.entity.EventoPuntos;
import org.example.springale.entity.Logro;
import org.example.springale.entity.Materia;
import org.example.springale.entity.MotivoPuntos;
import org.example.springale.exception.RecursoNoEncontradoException;
import org.example.springale.repository.EstudianteRepository;
import org.example.springale.repository.EventoPuntosRepository;
import org.example.springale.repository.LogroRepository;
import org.example.springale.repository.MateriaRepository;
import org.example.springale.security.SeguridadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de gamificacion: otorga puntos, calcula niveles, logros y rankings.
 * Toda la logica de puntos se centraliza aqui para que los demas modulos
 * solo publiquen el evento correspondiente.
 */
@Service
@Transactional(readOnly = true)
public class GamificacionService {

    private final EventoPuntosRepository eventos;
    private final LogroRepository logros;
    private final EstudianteRepository estudiantes;
    private final MateriaRepository materias;
    private final CalculadoraPuntos calculadoraPuntos;
    private final CalculadoraNivel calculadoraNivel;
    private final SeguridadService seguridad;

    public GamificacionService(EventoPuntosRepository eventos,
                               LogroRepository logros,
                               EstudianteRepository estudiantes,
                               MateriaRepository materias,
                               CalculadoraPuntos calculadoraPuntos,
                               CalculadoraNivel calculadoraNivel,
                               SeguridadService seguridad) {
        this.eventos = eventos;
        this.logros = logros;
        this.estudiantes = estudiantes;
        this.materias = materias;
        this.calculadoraPuntos = calculadoraPuntos;
        this.calculadoraNivel = calculadoraNivel;
        this.seguridad = seguridad;
    }

    @Transactional
    public EventoPuntos otorgar(Estudiante estudiante, MotivoPuntos motivo,
                                String descripcion, Materia materia) {
        int puntos = calculadoraPuntos.calcular(motivo);
        EventoPuntos evento = new EventoPuntos(motivo, puntos, descripcion, estudiante, materia);
        return eventos.save(evento);
    }

    @Transactional
    public EventoPuntosResponse otorgarManual(Long estudianteId, MotivoPuntos motivo,
                                              String descripcion, Long materiaId) {
        Estudiante estudiante = estudiante(estudianteId);
        Materia materia = materiaId != null ? materia(materiaId) : null;
        return EventoPuntosResponse.from(otorgar(estudiante, motivo, descripcion, materia));
    }

    public PerfilGamificacionResponse perfil(Long estudianteId) {
        seguridad.validarAccesoEstudiante(estudianteId);
        Estudiante estudiante = estudiante(estudianteId);
        int total = eventos.totalPuntosEstudiante(estudianteId);
        CalculadoraNivel.NivelInfo nivel = calculadoraNivel.calcular(total);

        List<Logro> catalogo = logros.findAllByOrderByPuntosRequeridosAsc();
        List<LogroResponse> desbloqueados = new ArrayList<>();
        List<LogroResponse> pendientes = new ArrayList<>();
        for (Logro logro : catalogo) {
            boolean desbloqueado = total >= logro.getPuntosRequeridos();
            LogroResponse response = LogroResponse.from(logro, desbloqueado);
            if (desbloqueado) {
                desbloqueados.add(response);
            } else {
                pendientes.add(response);
            }
        }

        List<EventoPuntosResponse> historial = eventos
                .findByEstudianteIdOrderByFechaDesc(estudianteId)
                .stream()
                .map(EventoPuntosResponse::from)
                .toList();

        return new PerfilGamificacionResponse(
                estudiante.getId(),
                estudiante.getNombreCompleto(),
                total,
                nivel.nombre(),
                nivel.numero(),
                nivel.puntosSiguienteNivel(),
                nivel.progresoPorcentaje(),
                desbloqueados,
                pendientes,
                historial
        );
    }

    public List<RankingResponse> rankingGeneral() {
        return mapearRanking(eventos.rankingGeneral());
    }

    public List<RankingResponse> rankingPorMateria(Long materiaId) {
        materia(materiaId);
        return mapearRanking(eventos.rankingPorMateria(materiaId));
    }

    public List<EventoPuntosResponse> historial(Long estudianteId) {
        seguridad.validarAccesoEstudiante(estudianteId);
        return eventos.findByEstudianteIdOrderByFechaDesc(estudianteId)
                .stream()
                .map(EventoPuntosResponse::from)
                .toList();
    }

    private List<RankingResponse> mapearRanking(List<Object[]> filas) {
        List<RankingResponse> ranking = new ArrayList<>();
        int posicion = 1;
        for (Object[] fila : filas) {
            int puntos = ((Number) fila[3]).intValue();
            ranking.add(new RankingResponse(
                    posicion++,
                    ((Number) fila[0]).longValue(),
                    fila[1] + " " + fila[2],
                    puntos,
                    calculadoraNivel.calcular(puntos).nombre()
            ));
        }
        return ranking;
    }

    public int puntosDe(Long estudianteId) {
        return eventos.totalPuntosEstudiante(estudianteId);
    }

    private Estudiante estudiante(Long id) {
        return estudiantes.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el estudiante con id " + id));
    }

    private Materia materia(Long id) {
        return materias.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la materia con id " + id));
    }
}
