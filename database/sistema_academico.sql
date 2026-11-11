-- =====================================================================
-- Sistema de Gestion Academica Colaborativa - SpringAle
-- Script compatible con MySQL 8.x, phpMyAdmin, MySQL Workbench y
-- Oracle MySQL. Incluye estructura y datos de demostracion.
--
-- Uso:
--   1. Abrir phpMyAdmin o MySQL Workbench.
--   2. Importar este archivo (o ejecutarlo completo).
--   3. Credenciales de prueba al final del script.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS sistema_academico
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sistema_academico;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS publicaciones;
DROP TABLE IF EXISTS grupo_estudio_miembros;
DROP TABLE IF EXISTS grupos_estudio;
DROP TABLE IF EXISTS notificaciones;
DROP TABLE IF EXISTS eventos_puntos;
DROP TABLE IF EXISTS logros;
DROP TABLE IF EXISTS mensajes_tutoria;
DROP TABLE IF EXISTS tutorias;
DROP TABLE IF EXISTS postulaciones_tutor;
DROP TABLE IF EXISTS aportes_equipo;
DROP TABLE IF EXISTS proyecto_miembros;
DROP TABLE IF EXISTS proyectos_equipo;
DROP TABLE IF EXISTS llegadas_tardias;
DROP TABLE IF EXISTS entregas_tarea;
DROP TABLE IF EXISTS tareas;
DROP TABLE IF EXISTS calificaciones;
DROP TABLE IF EXISTS materia_estudiantes;
DROP TABLE IF EXISTS materias;
DROP TABLE IF EXISTS periodos_academicos;
DROP TABLE IF EXISTS estudiantes;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS semilleros;

SET FOREIGN_KEY_CHECKS = 1;

-- ------------------------- Usuarios y perfiles ------------------------
CREATE TABLE semilleros (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    email VARCHAR(120) NOT NULL,
    password VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    activo TINYINT(1) NOT NULL DEFAULT 1,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuarios_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE estudiantes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    documento VARCHAR(30),
    nombre VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    email VARCHAR(120) NOT NULL,
    semestre INT NOT NULL,
    usuario_id BIGINT,
    semillero_id BIGINT,
    PRIMARY KEY (id),
    UNIQUE KEY uk_estudiantes_documento (documento),
    UNIQUE KEY uk_estudiantes_email (email),
    UNIQUE KEY uk_estudiantes_usuario (usuario_id),
    CONSTRAINT fk_estudiantes_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios (id) ON DELETE SET NULL,
    CONSTRAINT fk_estudiantes_semillero FOREIGN KEY (semillero_id)
        REFERENCES semilleros (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------- Oferta academica ---------------------------
CREATE TABLE periodos_academicos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(30) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    activo TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uk_periodos_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE materias (
    id BIGINT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(20) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500),
    profesor_id BIGINT,
    PRIMARY KEY (id),
    UNIQUE KEY uk_materias_codigo (codigo),
    CONSTRAINT fk_materias_profesor FOREIGN KEY (profesor_id)
        REFERENCES usuarios (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE materia_estudiantes (
    materia_id BIGINT NOT NULL,
    estudiante_id BIGINT NOT NULL,
    PRIMARY KEY (materia_id, estudiante_id),
    CONSTRAINT fk_materia_estudiantes_materia FOREIGN KEY (materia_id)
        REFERENCES materias (id) ON DELETE CASCADE,
    CONSTRAINT fk_materia_estudiantes_estudiante FOREIGN KEY (estudiante_id)
        REFERENCES estudiantes (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------- Notas --------------------------------------
CREATE TABLE calificaciones (
    id BIGINT NOT NULL AUTO_INCREMENT,
    estudiante_id BIGINT NOT NULL,
    materia_id BIGINT NOT NULL,
    periodo_id BIGINT NOT NULL,
    profesor_id BIGINT NOT NULL,
    evaluacion VARCHAR(120) NOT NULL,
    nota DOUBLE NOT NULL,
    fecha DATE NOT NULL,
    observacion VARCHAR(500),
    PRIMARY KEY (id),
    CONSTRAINT fk_calificaciones_estudiante FOREIGN KEY (estudiante_id)
        REFERENCES estudiantes (id) ON DELETE CASCADE,
    CONSTRAINT fk_calificaciones_materia FOREIGN KEY (materia_id)
        REFERENCES materias (id) ON DELETE CASCADE,
    CONSTRAINT fk_calificaciones_periodo FOREIGN KEY (periodo_id)
        REFERENCES periodos_academicos (id) ON DELETE RESTRICT,
    CONSTRAINT fk_calificaciones_profesor FOREIGN KEY (profesor_id)
        REFERENCES usuarios (id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------- Tareas y entregas --------------------------
CREATE TABLE tareas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(150) NOT NULL,
    descripcion VARCHAR(1500) NOT NULL,
    fecha_limite DATETIME NOT NULL,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activa TINYINT(1) NOT NULL DEFAULT 1,
    materia_id BIGINT NOT NULL,
    profesor_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_tareas_materia FOREIGN KEY (materia_id)
        REFERENCES materias (id) ON DELETE CASCADE,
    CONSTRAINT fk_tareas_profesor FOREIGN KEY (profesor_id)
        REFERENCES usuarios (id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE entregas_tarea (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tarea_id BIGINT NOT NULL,
    estudiante_id BIGINT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_entrega DATETIME,
    comentario VARCHAR(500),
    nota DOUBLE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_entregas_tarea_estudiante (tarea_id, estudiante_id),
    CONSTRAINT fk_entregas_tarea FOREIGN KEY (tarea_id)
        REFERENCES tareas (id) ON DELETE CASCADE,
    CONSTRAINT fk_entregas_estudiante FOREIGN KEY (estudiante_id)
        REFERENCES estudiantes (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------- Llegadas tardias ---------------------------
CREATE TABLE llegadas_tardias (
    id BIGINT NOT NULL AUTO_INCREMENT,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    observacion VARCHAR(500),
    metodo VARCHAR(20) NOT NULL,
    estudiante_id BIGINT NOT NULL,
    registrado_por_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_llegadas_estudiante FOREIGN KEY (estudiante_id)
        REFERENCES estudiantes (id) ON DELETE CASCADE,
    CONSTRAINT fk_llegadas_registrado_por FOREIGN KEY (registrado_por_id)
        REFERENCES usuarios (id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------- Trabajo en equipo --------------------------
CREATE TABLE proyectos_equipo (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(1500) NOT NULL,
    fecha_limite DATETIME NOT NULL,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    calificacion DOUBLE,
    comentario_profesor VARCHAR(1000),
    fecha_entrega_final DATETIME,
    entrega_final_contenido VARCHAR(2000),
    materia_id BIGINT NOT NULL,
    profesor_id BIGINT NOT NULL,
    lider_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_proyectos_materia FOREIGN KEY (materia_id)
        REFERENCES materias (id) ON DELETE CASCADE,
    CONSTRAINT fk_proyectos_profesor FOREIGN KEY (profesor_id)
        REFERENCES usuarios (id) ON DELETE RESTRICT,
    CONSTRAINT fk_proyectos_lider FOREIGN KEY (lider_id)
        REFERENCES estudiantes (id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE proyecto_miembros (
    proyecto_id BIGINT NOT NULL,
    estudiante_id BIGINT NOT NULL,
    PRIMARY KEY (proyecto_id, estudiante_id),
    CONSTRAINT fk_proyecto_miembros_proyecto FOREIGN KEY (proyecto_id)
        REFERENCES proyectos_equipo (id) ON DELETE CASCADE,
    CONSTRAINT fk_proyecto_miembros_estudiante FOREIGN KEY (estudiante_id)
        REFERENCES estudiantes (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE aportes_equipo (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tipo VARCHAR(20) NOT NULL,
    contenido VARCHAR(2000) NOT NULL,
    comentario VARCHAR(500),
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    proyecto_id BIGINT NOT NULL,
    estudiante_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_aportes_proyecto FOREIGN KEY (proyecto_id)
        REFERENCES proyectos_equipo (id) ON DELETE CASCADE,
    CONSTRAINT fk_aportes_estudiante FOREIGN KEY (estudiante_id)
        REFERENCES estudiantes (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------- Tutorias entre pares -----------------------
CREATE TABLE postulaciones_tutor (
    id BIGINT NOT NULL AUTO_INCREMENT,
    disponibilidad VARCHAR(300) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_postulacion DATE NOT NULL,
    estudiante_id BIGINT NOT NULL,
    materia_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_postulaciones_estudiante_materia (estudiante_id, materia_id),
    CONSTRAINT fk_postulaciones_estudiante FOREIGN KEY (estudiante_id)
        REFERENCES estudiantes (id) ON DELETE CASCADE,
    CONSTRAINT fk_postulaciones_materia FOREIGN KEY (materia_id)
        REFERENCES materias (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tutorias (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tema VARCHAR(100) NOT NULL,
    descripcion VARCHAR(600),
    estado VARCHAR(20) NOT NULL,
    fecha_solicitud DATE NOT NULL,
    fecha_tutoria DATE,
    nota_antes DOUBLE,
    tutor_id BIGINT NOT NULL,
    aprendiz_id BIGINT NOT NULL,
    materia_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_tutorias_tutor FOREIGN KEY (tutor_id)
        REFERENCES estudiantes (id) ON DELETE CASCADE,
    CONSTRAINT fk_tutorias_aprendiz FOREIGN KEY (aprendiz_id)
        REFERENCES estudiantes (id) ON DELETE CASCADE,
    CONSTRAINT fk_tutorias_materia FOREIGN KEY (materia_id)
        REFERENCES materias (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE mensajes_tutoria (
    id BIGINT NOT NULL AUTO_INCREMENT,
    contenido VARCHAR(1000) NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tutoria_id BIGINT NOT NULL,
    autor_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_mensajes_tutoria FOREIGN KEY (tutoria_id)
        REFERENCES tutorias (id) ON DELETE CASCADE,
    CONSTRAINT fk_mensajes_autor FOREIGN KEY (autor_id)
        REFERENCES estudiantes (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------- Gamificacion -------------------------------
CREATE TABLE logros (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(80) NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    puntos_requeridos INT NOT NULL,
    icono VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_logros_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE eventos_puntos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    motivo VARCHAR(40) NOT NULL,
    puntos INT NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estudiante_id BIGINT NOT NULL,
    materia_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_eventos_estudiante FOREIGN KEY (estudiante_id)
        REFERENCES estudiantes (id) ON DELETE CASCADE,
    CONSTRAINT fk_eventos_materia FOREIGN KEY (materia_id)
        REFERENCES materias (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE notificaciones (
    id BIGINT NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(150) NOT NULL,
    mensaje VARCHAR(500) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    leida TINYINT(1) NOT NULL DEFAULT 0,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    referencia_id BIGINT,
    usuario_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_notificaciones_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------- Modulos colaborativos ----------------------
CREATE TABLE grupos_estudio (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    tema VARCHAR(100) NOT NULL,
    fecha_creacion DATE NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE grupo_estudio_miembros (
    grupo_id BIGINT NOT NULL,
    estudiante_id BIGINT NOT NULL,
    PRIMARY KEY (grupo_id, estudiante_id),
    CONSTRAINT fk_grupo_miembros_grupo FOREIGN KEY (grupo_id)
        REFERENCES grupos_estudio (id) ON DELETE CASCADE,
    CONSTRAINT fk_grupo_miembros_estudiante FOREIGN KEY (estudiante_id)
        REFERENCES estudiantes (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE publicaciones (
    id BIGINT NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(150) NOT NULL,
    contenido VARCHAR(2000) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    autor_id BIGINT NOT NULL,
    grupo_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_publicaciones_autor FOREIGN KEY (autor_id)
        REFERENCES estudiantes (id) ON DELETE CASCADE,
    CONSTRAINT fk_publicaciones_grupo FOREIGN KEY (grupo_id)
        REFERENCES grupos_estudio (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- DATOS DE DEMOSTRACION
-- Contrasenas (texto plano):
--   admin@springale.edu       -> Admin123*
--   profesor@springale.edu    -> Profesor123*
--   ana.torres@springale.edu  -> Estudiante123*
--   luis.gomez@springale.edu  -> Estudiante123*
--   sofia.ramirez@springale.edu -> Estudiante123*
-- =====================================================================

INSERT INTO semilleros (id, nombre, descripcion) VALUES
(1, 'Quipux', 'Semillero de desarrollo de software y gestion documental'),
(2, 'Semillero General', 'Semillero asignado automaticamente a nuevos estudiantes');

INSERT INTO usuarios (id, nombre, apellido, email, password, rol, activo, fecha_registro) VALUES
(1, 'Laura', 'Administradora', 'admin@springale.edu',
 '$2a$10$U3ptE1UjxgJZsUqiT6vOUOCU1CaA4k/b3jTBxMtlZ0uWc3yukH2nG', 'ADMINISTRADOR', 1, NOW()),
(2, 'Carlos', 'Mendoza', 'profesor@springale.edu',
 '$2a$10$vpz4KeoudVxiXFQWc..rvO9HaQU1FDDNQYd9I54gdI8TYFk1g03dO', 'PROFESOR', 1, NOW()),
(3, 'Ana', 'Torres', 'ana.torres@springale.edu',
 '$2a$10$3SVLtGWzT2sf1lm7dLqmlOUP62dj6g.okdt/4FJWhW6stF6nndnsa', 'ESTUDIANTE', 1, NOW()),
(4, 'Luis', 'Gomez', 'luis.gomez@springale.edu',
 '$2a$10$3SVLtGWzT2sf1lm7dLqmlOUP62dj6g.okdt/4FJWhW6stF6nndnsa', 'ESTUDIANTE', 1, NOW()),
(5, 'Sofia', 'Ramirez', 'sofia.ramirez@springale.edu',
 '$2a$10$3SVLtGWzT2sf1lm7dLqmlOUP62dj6g.okdt/4FJWhW6stF6nndnsa', 'ESTUDIANTE', 1, NOW());

INSERT INTO estudiantes (id, documento, nombre, apellido, email, semestre, usuario_id, semillero_id) VALUES
(1, '1001', 'Ana', 'Torres', 'ana.torres@springale.edu', 5, 3, 1),
(2, '1002', 'Luis', 'Gomez', 'luis.gomez@springale.edu', 7, 4, 1),
(3, '1003', 'Sofia', 'Ramirez', 'sofia.ramirez@springale.edu', 3, 5, 1);

INSERT INTO periodos_academicos (id, nombre, fecha_inicio, fecha_fin, activo) VALUES
(1, '2026-1', '2026-01-15', '2026-06-30', 1),
(2, '2026-2', '2026-07-15', '2026-12-15', 0);

INSERT INTO materias (id, codigo, nombre, descripcion, profesor_id) VALUES
(1, 'BD-101', 'Bases de Datos', 'Modelado relacional, SQL y transacciones', 2),
(2, 'PRG-101', 'Programacion', 'Fundamentos de programacion orientada a objetos', 2),
(3, 'ALG-101', 'Algoritmos', 'Estructuras de datos y analisis de complejidad', 2);

INSERT INTO materia_estudiantes (materia_id, estudiante_id) VALUES
(1, 1), (1, 2), (1, 3),
(2, 1), (2, 2), (2, 3),
(3, 2);

INSERT INTO calificaciones
(id, estudiante_id, materia_id, periodo_id, profesor_id, evaluacion, nota, fecha, observacion) VALUES
(1, 1, 1, 1, 2, 'Parcial 1', 3.5, DATE_SUB(CURDATE(), INTERVAL 30 DAY), 'Requiere refuerzo en consultas'),
(2, 1, 1, 1, 2, 'Parcial 2', 4.5, DATE_SUB(CURDATE(), INTERVAL 5 DAY), 'Mejoro despues de la tutoria'),
(3, 1, 2, 1, 2, 'Taller JPA', 4.0, DATE_SUB(CURDATE(), INTERVAL 10 DAY), NULL),
(4, 2, 1, 1, 2, 'Parcial 1', 4.8, DATE_SUB(CURDATE(), INTERVAL 30 DAY), 'Excelente desempeno'),
(5, 2, 2, 1, 2, 'Taller JPA', 4.6, DATE_SUB(CURDATE(), INTERVAL 10 DAY), NULL),
(6, 3, 1, 1, 2, 'Parcial 1', 3.2, DATE_SUB(CURDATE(), INTERVAL 30 DAY), 'Debe reforzar joins'),
(7, 3, 2, 1, 2, 'Taller JPA', 3.8, DATE_SUB(CURDATE(), INTERVAL 10 DAY), NULL);

INSERT INTO tareas (id, titulo, descripcion, fecha_limite, fecha_creacion, activa, materia_id, profesor_id) VALUES
(1, 'Taller de consultas SQL', 'Resolver diez consultas con joins y subconsultas.',
 DATE_SUB(NOW(), INTERVAL 4 DAY), NOW(), 1, 1, 2),
(2, 'Modelo entidad-relacion', 'Disenar el modelo de una biblioteca con al menos ocho entidades.',
 DATE_ADD(NOW(), INTERVAL 2 DAY), NOW(), 1, 1, 2),
(3, 'Refactorizar ejercicio POO', 'Aplicar encapsulamiento, herencia y polimorfismo.',
 DATE_ADD(NOW(), INTERVAL 5 DAY), NOW(), 1, 2, 2);

INSERT INTO entregas_tarea (id, tarea_id, estudiante_id, estado, fecha_entrega, comentario, nota) VALUES
(1, 1, 2, 'COMPLETADA', DATE_SUB(NOW(), INTERVAL 5 DAY), 'Consultas resueltas y documentadas.', NULL);

INSERT INTO llegadas_tardias (id, fecha, hora, observacion, metodo, estudiante_id, registrado_por_id) VALUES
(1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), '07:25:00', 'Llego 25 minutos tarde sin excusa.', 'MANUAL', 3, 1);

INSERT INTO proyectos_equipo
(id, nombre, descripcion, fecha_limite, fecha_creacion, estado, calificacion,
 comentario_profesor, fecha_entrega_final, entrega_final_contenido, materia_id, profesor_id, lider_id) VALUES
(1, 'Sistema de biblioteca', 'Disenar e implementar el modulo de prestamos de una biblioteca.',
 DATE_ADD(NOW(), INTERVAL 10 DAY), NOW(), 'EN_PROGRESO', NULL, NULL, NULL, NULL, 1, 2, 2);

INSERT INTO proyecto_miembros (proyecto_id, estudiante_id) VALUES
(1, 1), (1, 2), (1, 3);

INSERT INTO aportes_equipo (id, tipo, contenido, comentario, fecha, proyecto_id, estudiante_id) VALUES
(1, 'TEXTO', 'Modelo entidad-relacion y diccionario de datos.',
 'Incluyo las entidades principales y sus relaciones.', NOW(), 1, 2),
(2, 'ENLACE', 'https://ejemplo.com/aporte-ana',
 'Documento con las consultas SQL del modulo.', NOW(), 1, 1);

INSERT INTO postulaciones_tutor (id, disponibilidad, estado, fecha_postulacion, estudiante_id, materia_id) VALUES
(1, 'Martes y jueves de 14:00 a 17:00', 'APROBADA', CURDATE(), 2, 1),
(2, 'Lunes y miercoles de 10:00 a 12:00', 'APROBADA', CURDATE(), 1, 2);

INSERT INTO tutorias
(id, tema, descripcion, estado, fecha_solicitud, fecha_tutoria, nota_antes, tutor_id, aprendiz_id, materia_id) VALUES
(1, 'Repasar consultas SQL', 'Ejercicios de joins y subconsultas para preparar el parcial.',
 'ACEPTADA', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 3.5, 2, 1, 1),
(2, 'Introduccion a Git', 'Acompanamiento para crear ramas y resolver conflictos.',
 'COMPLETADA', DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_SUB(CURDATE(), INTERVAL 9 DAY), 3.0, 2, 3, 2);

INSERT INTO mensajes_tutoria (id, contenido, fecha, tutoria_id, autor_id) VALUES
(1, 'Hola Ana, revisemos los joins el martes.', DATE_SUB(NOW(), INTERVAL 1 DAY), 1, 2),
(2, 'Perfecto, llevo los ejercicios del taller.', NOW(), 1, 1);

INSERT INTO logros (id, nombre, descripcion, puntos_requeridos, icono) VALUES
(1, 'Primer paso', 'Gana tus primeros 10 puntos', 10, 'spark'),
(2, 'Aprendiz constante', 'Alcanza 100 puntos', 100, 'book'),
(3, 'Colaborador', 'Alcanza 300 puntos', 300, 'users'),
(4, 'Mentor', 'Alcanza 600 puntos', 600, 'star'),
(5, 'Maestro', 'Alcanza 1000 puntos', 1000, 'trophy');

INSERT INTO eventos_puntos (id, motivo, puntos, descripcion, fecha, estudiante_id, materia_id) VALUES
(1, 'BUEN_DESEMPENO', 20, 'Buen desempeno en el segundo parcial de Bases de Datos', DATE_SUB(NOW(), INTERVAL 5 DAY), 1, 1),
(2, 'ENTREGA_TAREA_A_TIEMPO', 10, 'Entrega a tiempo del taller de SQL', DATE_SUB(NOW(), INTERVAL 6 DAY), 1, 1),
(3, 'BUEN_DESEMPENO', 20, 'Excelente desempeno academico', DATE_SUB(NOW(), INTERVAL 8 DAY), 2, 1),
(4, 'TUTORIA_COMPLETADA', 15, 'Tutoria completada de Bases de Datos', DATE_SUB(NOW(), INTERVAL 4 DAY), 2, 1),
(5, 'APORTE_EQUIPO', 8, 'Aporte al proyecto Sistema de biblioteca', DATE_SUB(NOW(), INTERVAL 3 DAY), 2, 1),
(6, 'APORTE_EQUIPO', 8, 'Aporte al proyecto Sistema de biblioteca', DATE_SUB(NOW(), INTERVAL 2 DAY), 3, 2);

INSERT INTO notificaciones (id, titulo, mensaje, tipo, leida, fecha, referencia_id, usuario_id) VALUES
(1, 'Nueva calificacion registrada', 'Se registro la nota 4.5 en Bases de Datos (Parcial 2).',
 'CALIFICACION', 0, DATE_SUB(NOW(), INTERVAL 5 DAY), 2, 3),
(2, 'Nuevo proyecto de equipo', 'Fue asignado al proyecto Sistema de biblioteca.',
 'EQUIPO', 1, DATE_SUB(NOW(), INTERVAL 12 DAY), 1, 3);

INSERT INTO grupos_estudio (id, nombre, descripcion, tema, fecha_creacion) VALUES
(1, 'Club de Algoritmos', 'Espacio para resolver ejercicios y compartir apuntes',
 'Algoritmos y estructuras de datos', CURDATE());

INSERT INTO grupo_estudio_miembros (grupo_id, estudiante_id) VALUES
(1, 1), (1, 2);

INSERT INTO publicaciones (id, titulo, contenido, tipo, fecha, autor_id, grupo_id) VALUES
(1, 'Como empezar con recursividad',
 'Comparto ejercicios sencillos para practicar antes del parcial.',
 'PREGUNTA', DATE_SUB(NOW(), INTERVAL 2 DAY), 1, 1),
(2, 'Apunte: complejidad Big-O',
 'Resumen de las complejidades mas comunes con ejemplos.',
 'APUNTE', DATE_SUB(NOW(), INTERVAL 1 DAY), 2, 1);

-- =====================================================================
-- Consultas utiles de verificacion
-- =====================================================================
-- SELECT u.email, u.rol, e.nombre, e.apellido FROM usuarios u
-- LEFT JOIN estudiantes e ON e.usuario_id = u.id;
-- SELECT e.nombre, SUM(ep.puntos) AS puntos
-- FROM eventos_puntos ep JOIN estudiantes e ON e.id = ep.estudiante_id
-- GROUP BY e.id, e.nombre ORDER BY puntos DESC;
