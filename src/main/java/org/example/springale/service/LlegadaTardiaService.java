package org.example.springale.service;

import org.example.springale.dto.LlegadaQrRequest;
import org.example.springale.dto.LlegadaTardiaRequest;
import org.example.springale.dto.LlegadaTardiaResponse;
import org.example.springale.entity.Estudiante;
import org.example.springale.entity.LlegadaTardia;
import org.example.springale.entity.MetodoRegistro;
import org.example.springale.entity.TipoNotificacion;
import org.example.springale.entity.Usuario;
import org.example.springale.exception.RecursoNoEncontradoException;
import org.example.springale.exception.ReglaNegocioException;
import org.example.springale.repository.EstudianteRepository;
import org.example.springale.repository.LlegadaTardiaRepository;
import org.example.springale.security.SeguridadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Registro y consulta de llegadas tardias.
 * El control de rol se refuerza en el controlador con @PreAuthorize;
 * aqui se aplican las reglas de negocio y la notificacion al estudiante.
 */
@Service
@Transactional(readOnly = true)
public class LlegadaTardiaService {

    private final LlegadaTardiaRepository llegadas;
    private final EstudianteRepository estudiantes;
    private final SeguridadService seguridad;
    private final NotificacionService notificaciones;

    public LlegadaTardiaService(LlegadaTardiaRepository llegadas,
                                EstudianteRepository estudiantes,
                                SeguridadService seguridad,
                                NotificacionService notificaciones) {
        this.llegadas = llegadas;
        this.estudiantes = estudiantes;
        this.seguridad = seguridad;
        this.notificaciones = notificaciones;
    }

    public List<LlegadaTardiaResponse> listar(Long estudianteId, LocalDate desde, LocalDate hasta) {
        Long filtroEstudiante = seguridad.esEstudiante()
                ? seguridad.estudianteActual().getId()
                : estudianteId;

        List<LlegadaTardia> resultado;
        if (filtroEstudiante != null && desde != null && hasta != null) {
            resultado = llegadas.findByEstudianteIdAndFechaBetweenOrderByFechaDescHoraDesc(
                    filtroEstudiante, desde, hasta);
        } else if (filtroEstudiante != null) {
            resultado = llegadas.findByEstudianteIdOrderByFechaDescHoraDesc(filtroEstudiante);
        } else {
            resultado = llegadas.findAllByOrderByFechaDescHoraDesc();
        }
        return resultado.stream().map(LlegadaTardiaResponse::from).toList();
    }

    public List<LlegadaTardiaResponse> historial(Long estudianteId) {
        seguridad.validarAccesoEstudiante(estudianteId);
        return llegadas.findByEstudianteIdOrderByFechaDescHoraDesc(estudianteId)
                .stream()
                .map(LlegadaTardiaResponse::from)
                .toList();
    }

    public LlegadaTardiaResponse buscar(Long id) {
        LlegadaTardia llegada = entidad(id);
        seguridad.validarAccesoEstudiante(llegada.getEstudiante().getId());
        return LlegadaTardiaResponse.from(llegada);
    }

    @Transactional
    public LlegadaTardiaResponse crear(LlegadaTardiaRequest request) {
        Estudiante estudiante = estudiante(request.estudianteId());
        Usuario administrador = seguridad.usuarioActual();

        LlegadaTardia llegada = new LlegadaTardia(
                request.fecha(),
                request.hora(),
                request.observacion(),
                MetodoRegistro.MANUAL,
                estudiante,
                administrador
        );
        return guardarYNotificar(llegada);
    }

    /** Registra la llegada a partir del QR que contiene documento, id o EST-{id}. */
    @Transactional
    public LlegadaTardiaResponse registrarPorQr(LlegadaQrRequest request) {
        Estudiante estudiante = resolverPorCodigoQr(request.codigoQr().trim());
        Usuario administrador = seguridad.usuarioActual();

        LlegadaTardia llegada = new LlegadaTardia(
                LocalDate.now(),
                LocalTime.now().withNano(0),
                request.observacion(),
                MetodoRegistro.QR,
                estudiante,
                administrador
        );
        return guardarYNotificar(llegada);
    }

    @Transactional
    public LlegadaTardiaResponse actualizar(Long id, LlegadaTardiaRequest request) {
        LlegadaTardia llegada = entidad(id);
        llegada.setEstudiante(estudiante(request.estudianteId()));
        llegada.setFecha(request.fecha());
        llegada.setHora(request.hora());
        llegada.setObservacion(request.observacion());
        return LlegadaTardiaResponse.from(llegadas.save(llegada));
    }

    @Transactional
    public void eliminar(Long id) {
        llegadas.delete(entidad(id));
    }

    private LlegadaTardiaResponse guardarYNotificar(LlegadaTardia llegada) {
        llegada = llegadas.save(llegada);
        notificaciones.crearParaEstudiante(
                llegada.getEstudiante(),
                "Llegada tardia registrada",
                "Se registro una llegada tardia el " + llegada.getFecha()
                        + " a las " + llegada.getHora() + ".",
                TipoNotificacion.SISTEMA,
                llegada.getId()
        );
        return LlegadaTardiaResponse.from(llegada);
    }

    private Estudiante resolverPorCodigoQr(String codigo) {
        return estudiantes.findByDocumento(codigo)
                .orElseGet(() -> {
                    String soloDigitos = codigo.replaceAll("\\D", "");
                    if (soloDigitos.isBlank()) {
                        throw new ReglaNegocioException("El codigo QR no es valido: " + codigo);
                    }
                    Long id = Long.valueOf(soloDigitos);
                    return estudiantes.findById(id)
                            .orElseThrow(() -> new RecursoNoEncontradoException(
                                    "No existe un estudiante asociado al QR " + codigo));
                });
    }

    private Estudiante estudiante(Long id) {
        return estudiantes.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el estudiante con id " + id));
    }

    private LlegadaTardia entidad(Long id) {
        return llegadas.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la llegada tardia con id " + id));
    }
}
