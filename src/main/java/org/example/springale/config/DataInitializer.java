package org.example.springale.config;

import org.example.springale.entity.AporteEquipo;
import org.example.springale.entity.Calificacion;
import org.example.springale.entity.EntregaTarea;
import org.example.springale.entity.EstadoPostulacion;
import org.example.springale.entity.EstadoProyecto;
import org.example.springale.entity.EstadoTarea;
import org.example.springale.entity.EstadoTutoria;
import org.example.springale.entity.Estudiante;
import org.example.springale.entity.GrupoEstudio;
import org.example.springale.entity.LlegadaTardia;
import org.example.springale.entity.Logro;
import org.example.springale.entity.Materia;
import org.example.springale.entity.MensajeTutoria;
import org.example.springale.entity.MetodoRegistro;
import org.example.springale.entity.MotivoPuntos;
import org.example.springale.entity.PeriodoAcademico;
import org.example.springale.entity.PostulacionTutor;
import org.example.springale.entity.ProyectoEquipo;
import org.example.springale.entity.Publicacion;
import org.example.springale.entity.Rol;
import org.example.springale.entity.Semillero;
import org.example.springale.entity.Tarea;
import org.example.springale.entity.TipoAporte;
import org.example.springale.entity.TipoPublicacion;
import org.example.springale.entity.Tutoria;
import org.example.springale.entity.Usuario;
import org.example.springale.repository.AporteEquipoRepository;
import org.example.springale.repository.CalificacionRepository;
import org.example.springale.repository.EntregaTareaRepository;
import org.example.springale.repository.EstudianteRepository;
import org.example.springale.repository.GrupoEstudioRepository;
import org.example.springale.repository.LlegadaTardiaRepository;
import org.example.springale.repository.LogroRepository;
import org.example.springale.repository.MateriaRepository;
import org.example.springale.repository.MensajeTutoriaRepository;
import org.example.springale.repository.PeriodoAcademicoRepository;
import org.example.springale.repository.PostulacionTutorRepository;
import org.example.springale.repository.ProyectoEquipoRepository;
import org.example.springale.repository.PublicacionRepository;
import org.example.springale.repository.SemilleroRepository;
import org.example.springale.repository.TareaRepository;
import org.example.springale.repository.TutoriaRepository;
import org.example.springale.repository.UsuarioRepository;
import org.example.springale.service.GamificacionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

/**
 * Carga datos de demostracion al iniciar la aplicacion.
 * Solo se ejecuta si la tabla de usuarios esta vacia, por lo que es seguro
 * reiniciar el servidor sin duplicar informacion.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarios;
    private final EstudianteRepository estudiantes;
    private final SemilleroRepository semilleros;
    private final MateriaRepository materias;
    private final PeriodoAcademicoRepository periodos;
    private final CalificacionRepository calificaciones;
    private final TareaRepository tareas;
    private final EntregaTareaRepository entregas;
    private final LlegadaTardiaRepository llegadas;
    private final ProyectoEquipoRepository proyectos;
    private final AporteEquipoRepository aportes;
    private final TutoriaRepository tutorias;
    private final MensajeTutoriaRepository mensajes;
    private final PostulacionTutorRepository postulaciones;
    private final LogroRepository logros;
    private final GrupoEstudioRepository grupos;
    private final PublicacionRepository publicaciones;
    private final GamificacionService gamificacion;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarios,
                           EstudianteRepository estudiantes,
                           SemilleroRepository semilleros,
                           MateriaRepository materias,
                           PeriodoAcademicoRepository periodos,
                           CalificacionRepository calificaciones,
                           TareaRepository tareas,
                           EntregaTareaRepository entregas,
                           LlegadaTardiaRepository llegadas,
                           ProyectoEquipoRepository proyectos,
                           AporteEquipoRepository aportes,
                           TutoriaRepository tutorias,
                           MensajeTutoriaRepository mensajes,
                           PostulacionTutorRepository postulaciones,
                           LogroRepository logros,
                           GrupoEstudioRepository grupos,
                           PublicacionRepository publicaciones,
                           GamificacionService gamificacion,
                           PasswordEncoder passwordEncoder) {
        this.usuarios = usuarios;
        this.estudiantes = estudiantes;
        this.semilleros = semilleros;
        this.materias = materias;
        this.periodos = periodos;
        this.calificaciones = calificaciones;
        this.tareas = tareas;
        this.entregas = entregas;
        this.llegadas = llegadas;
        this.proyectos = proyectos;
        this.aportes = aportes;
        this.tutorias = tutorias;
        this.mensajes = mensajes;
        this.postulaciones = postulaciones;
        this.logros = logros;
        this.grupos = grupos;
        this.publicaciones = publicaciones;
        this.gamificacion = gamificacion;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarios.count() > 0) {
            return;
        }

        Semillero quipux = semilleros.save(new Semillero(
                "Quipux", "Semillero de desarrollo de software y gestion documental"));

        Usuario admin = usuarios.save(new Usuario(
                "Laura", "Administradora", "admin@springale.edu",
                passwordEncoder.encode("Admin123*"), Rol.ADMINISTRADOR));

        Usuario carlos = usuarios.save(new Usuario(
                "Carlos", "Mendoza", "profesor@springale.edu",
                passwordEncoder.encode("Profesor123*"), Rol.PROFESOR));

        Usuario usuarioAna = usuarios.save(new Usuario(
                "Ana", "Torres", "ana.torres@springale.edu",
                passwordEncoder.encode("Estudiante123*"), Rol.ESTUDIANTE));
        Usuario usuarioLuis = usuarios.save(new Usuario(
                "Luis", "Gomez", "luis.gomez@springale.edu",
                passwordEncoder.encode("Estudiante123*"), Rol.ESTUDIANTE));
        Usuario usuarioSofia = usuarios.save(new Usuario(
                "Sofia", "Ramirez", "sofia.ramirez@springale.edu",
                passwordEncoder.encode("Estudiante123*"), Rol.ESTUDIANTE));

        Estudiante ana = estudiantes.save(new Estudiante(
                "1001", "Ana", "Torres", usuarioAna.getEmail(), 5, usuarioAna, quipux));
        Estudiante luis = estudiantes.save(new Estudiante(
                "1002", "Luis", "Gomez", usuarioLuis.getEmail(), 7, usuarioLuis, quipux));
        Estudiante sofia = estudiantes.save(new Estudiante(
                "1003", "Sofia", "Ramirez", usuarioSofia.getEmail(), 3, usuarioSofia, quipux));

        PeriodoAcademico periodo = periodos.save(new PeriodoAcademico(
                "2026-1", LocalDate.of(2026, 1, 15), LocalDate.of(2026, 6, 30), true));
        periodos.save(new PeriodoAcademico(
                "2026-2", LocalDate.of(2026, 7, 15), LocalDate.of(2026, 12, 15), false));

        Materia basesDatos = materias.save(new Materia(
                "BD-101", "Bases de Datos",
                "Modelado relacional, SQL y transacciones", carlos));
        Materia programacion = materias.save(new Materia(
                "PRG-101", "Programacion",
                "Fundamentos de programacion orientada a objetos", carlos));
        Materia algoritmos = materias.save(new Materia(
                "ALG-101", "Algoritmos",
                "Estructuras de datos y analisis de complejidad", carlos));

        basesDatos.getEstudiantes().addAll(Set.of(ana, luis, sofia));
        programacion.getEstudiantes().addAll(Set.of(ana, luis, sofia));
        algoritmos.getEstudiantes().add(luis);
        materias.saveAll(Set.of(basesDatos, programacion, algoritmos));

        calificaciones.save(new Calificacion(ana, basesDatos, periodo, carlos,
                "Parcial 1", 3.5, LocalDate.now().minusDays(30), "Requiere refuerzo en consultas"));
        calificaciones.save(new Calificacion(ana, basesDatos, periodo, carlos,
                "Parcial 2", 4.5, LocalDate.now().minusDays(5), "Mejoro despues de la tutoria"));
        calificaciones.save(new Calificacion(ana, programacion, periodo, carlos,
                "Taller JPA", 4.0, LocalDate.now().minusDays(10), null));
        calificaciones.save(new Calificacion(luis, basesDatos, periodo, carlos,
                "Parcial 1", 4.8, LocalDate.now().minusDays(30), "Excelente desempeno"));
        calificaciones.save(new Calificacion(luis, programacion, periodo, carlos,
                "Taller JPA", 4.6, LocalDate.now().minusDays(10), null));
        calificaciones.save(new Calificacion(sofia, basesDatos, periodo, carlos,
                "Parcial 1", 3.2, LocalDate.now().minusDays(30), "Debe reforzar joins"));
        calificaciones.save(new Calificacion(sofia, programacion, periodo, carlos,
                "Taller JPA", 3.8, LocalDate.now().minusDays(10), null));

        Tarea tareaVencida = tareas.save(new Tarea(
                "Taller de consultas SQL",
                "Resolver diez consultas con joins y subconsultas.",
                LocalDateTime.now().minusDays(4), basesDatos, carlos));
        tareas.save(new Tarea(
                "Modelo entidad-relacion",
                "Disenar el modelo de una biblioteca con al menos ocho entidades.",
                LocalDateTime.now().plusDays(2), basesDatos, carlos));
        tareas.save(new Tarea(
                "Refactorizar ejercicio POO",
                "Aplicar encapsulamiento, herencia y polimorfismo.",
                LocalDateTime.now().plusDays(5), programacion, carlos));

        EntregaTarea entregaLuis = new EntregaTarea(tareaVencida, luis);
        entregaLuis.setEstado(EstadoTarea.COMPLETADA);
        entregaLuis.setFechaEntrega(LocalDateTime.now().minusDays(5));
        entregaLuis.setComentario("Consultas resueltas y documentadas.");
        entregas.save(entregaLuis);

        llegadas.save(new LlegadaTardia(
                LocalDate.now().minusDays(2), LocalTime.of(7, 25),
                "Llego 25 minutos tarde sin excusa.", MetodoRegistro.MANUAL, sofia, admin));

        ProyectoEquipo proyecto = new ProyectoEquipo(
                "Sistema de biblioteca",
                "Disenar e implementar el modulo de prestamos de una biblioteca.",
                LocalDateTime.now().plusDays(10), basesDatos, carlos, luis);
        proyecto.setMiembros(Set.of(ana, luis, sofia));
        proyecto.setEstado(EstadoProyecto.EN_PROGRESO);
        proyecto = proyectos.save(proyecto);

        aportes.save(new AporteEquipo(
                TipoAporte.TEXTO,
                "Modelo entidad-relacion y diccionario de datos.",
                "Incluyo las entidades principales y sus relaciones.",
                proyecto, luis));
        aportes.save(new AporteEquipo(
                TipoAporte.ENLACE,
                "https://ejemplo.com/aporte-ana",
                "Documento con las consultas SQL del modulo.",
                proyecto, ana));

        PostulacionTutor tutorLuis = new PostulacionTutor(
                "Martes y jueves de 14:00 a 17:00", luis, basesDatos);
        tutorLuis.setEstado(EstadoPostulacion.APROBADA);
        postulaciones.save(tutorLuis);

        PostulacionTutor tutoraAna = new PostulacionTutor(
                "Lunes y miercoles de 10:00 a 12:00", ana, programacion);
        tutoraAna.setEstado(EstadoPostulacion.APROBADA);
        postulaciones.save(tutoraAna);

        Tutoria tutoria = new Tutoria(
                "Repasar consultas SQL",
                "Ejercicios de joins y subconsultas para preparar el parcial.",
                LocalDate.now().plusDays(3), luis, ana, basesDatos);
        tutoria.setEstado(EstadoTutoria.ACEPTADA);
        tutoria.setNotaAntes(3.5);
        tutoria = tutorias.save(tutoria);
        mensajes.save(new MensajeTutoria(
                "Hola Ana, revisemos los joins el martes.", tutoria, luis));
        mensajes.save(new MensajeTutoria(
                "Perfecto, llevo los ejercicios del taller.", tutoria, ana));

        Tutoria finalizada = new Tutoria(
                "Introduccion a Git",
                "Acompanamiento para crear ramas y resolver conflictos.",
                LocalDate.now().minusDays(10), luis, sofia, programacion);
        finalizada.setEstado(EstadoTutoria.COMPLETADA);
        finalizada.setNotaAntes(3.0);
        tutorias.save(finalizada);

        logros.save(new Logro("Primer paso", "Gana tus primeros 10 puntos", 10, "spark"));
        logros.save(new Logro("Aprendiz constante", "Alcanza 100 puntos", 100, "book"));
        logros.save(new Logro("Colaborador", "Alcanza 300 puntos", 300, "users"));
        logros.save(new Logro("Mentor", "Alcanza 600 puntos", 600, "star"));
        logros.save(new Logro("Maestro", "Alcanza 1000 puntos", 1000, "trophy"));

        gamificacion.otorgar(ana, MotivoPuntos.BUEN_DESEMPENO,
                "Buen desempeno en el segundo parcial de Bases de Datos", basesDatos);
        gamificacion.otorgar(ana, MotivoPuntos.ENTREGA_TAREA_A_TIEMPO,
                "Entrega a tiempo del taller de SQL", basesDatos);
        gamificacion.otorgar(luis, MotivoPuntos.BUEN_DESEMPENO,
                "Excelente desempeno academico", basesDatos);
        gamificacion.otorgar(luis, MotivoPuntos.TUTORIA_COMPLETADA,
                "Tutoria completada de Bases de Datos", basesDatos);
        gamificacion.otorgar(luis, MotivoPuntos.APORTE_EQUIPO,
                "Aporte al proyecto Sistema de biblioteca", basesDatos);
        gamificacion.otorgar(sofia, MotivoPuntos.APORTE_EQUIPO,
                "Aporte al proyecto Sistema de biblioteca", programacion);

        GrupoEstudio grupo = new GrupoEstudio(
                "Club de Algoritmos",
                "Espacio para resolver ejercicios y compartir apuntes",
                "Algoritmos y estructuras de datos");
        grupo.getMiembros().addAll(Set.of(ana, luis));
        grupo = grupos.save(grupo);

        publicaciones.save(new Publicacion(
                "Como empezar con recursividad",
                "Comparto ejercicios sencillos para practicar antes del parcial.",
                TipoPublicacion.PREGUNTA, ana, grupo));
        publicaciones.save(new Publicacion(
                "Apunte: complejidad Big-O",
                "Resumen de las complejidades mas comunes con ejemplos.",
                TipoPublicacion.APUNTE, luis, grupo));
    }
}
