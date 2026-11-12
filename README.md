# SpringAle - Sistema de Gestión Académica Colaborativa

Backend REST desarrollado con **Spring Boot 4.1.2** y **Java 21+** para la gestión académica con enfoque colaborativo. Incluye autenticación JWT, roles, notas, tareas, trabajo en equipo, tutorías entre pares, gamificación, notificaciones y seguimiento del desempeño.

La documentación interactiva está disponible en `/swagger-ui.html`. El proyecto no incluye vistas HTML: el backend se consume mediante la API REST y Swagger.

## Ubicación real del proyecto

La raíz Maven es:

```text
C:\Users\Eider\OneDrive\Documentos\SpringAle\SpringAle
```

No es `C:\Users\Eider\OneDrive\Documentos\SpringAle`: esa carpeta solo contiene la carpeta del proyecto y archivos de IDE. Todos los comandos de este README se ejecutan desde la carpeta que contiene `pom.xml`.

> Nota: el repositorio Git de SpringAle está inicializado dentro de la raíz Maven y apunta a `https://github.com/magoaleja-prog/SchoolNote` (rama `main`). La carpeta `C:\Users\Eider` contiene además un repositorio Git ajeno a este proyecto; no lo use para versionar SpringAle.

---

## Funcionalidades

### 1. Herramientas colaborativas
- **Grupos de estudio** con tema, descripción y miembros.
- **Publicaciones** de apuntes, anuncios, preguntas y recursos dentro de los grupos.
- **Proyectos de equipo** con estado, materia, líder, integrantes y aportes.
- **Consolidado de aportes** por proyecto para apoyar la evaluación del trabajo colaborativo.
- Control de pertenencia: un usuario solo puede publicar en grupos a los que pertenece.

### 2. Apoyo entre estudiantes
- **Postulaciones de tutores** por materia, con disponibilidad y estado.
- **Regla de desempeño**: se requiere promedio mínimo **3.8** para postularse como tutor; con **4.0** o más la postulación se aprueba automáticamente.
- **Tutorías entre pares** con estados `PENDIENTE`, `ACEPTADA`, `RECHAZADA`, `COMPLETADA` y `CANCELADA`.
- **Chat interno** de la tutoría, habilitado cuando esta ha sido aceptada.
- **Notificaciones** automáticas al solicitar, aceptar, rechazar, completar o cancelar una tutoría.
- Registro de la nota del aprendiz antes y después de la tutoría.

### 3. Seguimiento del desempeño académico
- **Calificaciones** por estudiante, materia, periodo y evaluación, con escala de **0.0 a 5.0**.
- **Promedio general**, promedio por materia, historial y estado de rendimiento:
  - `EXCELENTE`, `BUENO`, `ACEPTABLE`, `BAJO` o `CRITICO`.
- **Ranking general y por materia**, puntos, niveles y logros.
- **Gamificación** por eventos: entregas, tutorías, aportes, publicaciones y cumplimiento.
- **Tareas y entregas** con fechas límite, estados y detección de entregas atrasadas.
- **Llegadas tardías** con historial por estudiante y conteo de retardos.
- **Semilleros**, materias y periodos académicos como catálogos base.

---

## Arquitectura

El proyecto mantiene una arquitectura en capas:

```text
src/main/java/org/example/springale/
├── config/        Seguridad, OpenAPI, datos iniciales, recordatorios y apertura del navegador
├── controller/    Endpoints REST y control de acceso por roles
├── dto/           Request/Response records y DTOs de proyección
├── entity/        Entidades JPA y enumeraciones del dominio
├── exception/     Excepciones de negocio y manejo global de errores
├── repository/    Interfaces Spring Data JPA
├── security/      JWT, filtro de autenticación y UserDetailsService
└── service/       Lógica de negocio y patrones de estrategia
```

Estructura resumida de la raíz Maven:

```text
C:\Users\Eider\OneDrive\Documentos\SpringAle\SpringAle\
├── database\
│   └── sistema_academico.sql
├── src\
│   ├── main\
│   │   ├── java\org\example\springale\
│   │   │   ├── config\
│   │   │   ├── controller\
│   │   │   ├── dto\
│   │   │   ├── entity\
│   │   │   ├── exception\
│   │   │   ├── repository\
│   │   │   ├── security\
│   │   │   └── service\
│   │   │       └── estrategia\
│   │   └── resources\
│   │       ├── application.properties
│   │       └── application-h2.properties
│   └── test\
│       ├── java\org\example\springale\
│       └── resources\application-test.properties
├── pom.xml
├── mvnw
└── mvnw.cmd
```

Patrones aplicados:
- **Repository** para acceso a datos.
- **Service** para reglas de negocio y transacciones.
- **DTO** para desacoplar la API de las entidades.
- **Strategy** para el otorgamiento de puntos de gamificación.
- **Global Exception Handler** para respuestas de error consistentes.
- **JWT stateless** con autorización por roles y `@PreAuthorize`.

---

## Tecnologías

- Java 21+
- Spring Boot 4.1.2
- Spring Web MVC
- Spring Data JPA / Hibernate 7
- Spring Security + JWT
- Bean Validation
- MySQL 8 (perfil por defecto)
- H2 (perfil de demostración y pruebas)
- Springdoc OpenAPI / Swagger UI
- Maven

---

## Puesta en marcha

Todos los comandos se ejecutan desde la raíz Maven:

```bat
cd /d C:\Users\Eider\OneDrive\Documentos\SpringAle\SpringAle
```

En PowerShell:

```powershell
Set-Location 'C:\Users\Eider\OneDrive\Documentos\SpringAle\SpringAle'
```

### Requisitos previos

- **JDK 21 o superior** (el proyecto está configurado con `java.version=21`).
- Verificar la versión que usa Maven:

```bat
mvnw.cmd -v
```

Si `java -version` muestra Java 8 o inferior, configure `JAVA_HOME` antes de ejecutar Maven:

```bat
set JAVA_HOME=C:\ruta\al\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%
```

En PowerShell:

```powershell
$env:JAVA_HOME="C:\ruta\al\jdk-21"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
```

### Opción A: MySQL (perfil por defecto)

1. Tener MySQL 8 en ejecución.
2. Importar `C:\Users\Eider\OneDrive\Documentos\SpringAle\SpringAle\database\sistema_academico.sql` desde phpMyAdmin, MySQL Workbench o la consola.
3. Ajustar las credenciales si es necesario:

```bash
# Windows CMD
set DB_USERNAME=root
set DB_PASSWORD=tu_clave

# PowerShell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="tu_clave"
```

4. Ejecutar:

```bat
mvnw.cmd spring-boot:run
```

En Git Bash o Linux/macOS:

```bash
./mvnw spring-boot:run
```

También se puede dejar que Hibernate cree el esquema y que `DataInitializer` cargue los datos de demostración si la base está vacía. La URL por defecto crea la base `sistema_academico` si no existe.

Variables de entorno principales:

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `DB_URL` | URL JDBC de MySQL | `jdbc:mysql://localhost:3306/sistema_academico?...` |
| `DB_USERNAME` | Usuario de MySQL | `root` |
| `DB_PASSWORD` | Contraseña de MySQL | vacío |
| `DB_DRIVER` | Driver JDBC | `com.mysql.cj.jdbc.Driver` |
| `JWT_SECRET` | Secreto para firmar JWT | valor de desarrollo incluido |
| `JWT_EXPIRATION_MS` | Vigencia del token | `86400000` (24 h) |
| `PORT` | Puerto HTTP | `8080` |

### Opción B: H2 en memoria (demostración rápida)

No requiere MySQL:

```bat
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=h2
```

En Git Bash o Linux/macOS:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

O con el JAR empaquetado:

```bat
mvnw.cmd -DskipTests package
java -jar target\SpringAle-0.0.1-SNAPSHOT.jar --spring.profiles.active=h2
```

El perfil `h2` usa una base en memoria, crea el esquema automáticamente y carga los datos de demostración. Al detener la aplicación, los datos se pierden.

### Documentación

El proyecto no sirve una página HTML; toda la interacción se realiza con la API REST.

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>

> La ruta usa el puerto configurable con la variable `PORT` (por defecto `8080`). Si lo cambias, la URL sería `http://localhost:TU_PUERTO/swagger-ui.html`.
>
> Al abrir Swagger UI, el navegador redirige internamente a `/swagger-ui/index.html`; es el comportamiento normal de Springdoc (por eso una petición directa puede devolver `302`).

En Swagger, usar el botón **Authorize** y pegar el token JWT devuelto por `/auth/login`.

---

## Usuarios de prueba

| Rol | Nombre | Email | Contraseña |
|---|---|---|---|
| Administrador | Laura Administradora | `admin@springale.edu` | `Admin123*` |
| Profesor | Carlos Mendoza | `profesor@springale.edu` | `Profesor123*` |
| Estudiante | Ana Torres | `ana.torres@springale.edu` | `Estudiante123*` |
| Estudiante | Luis Gómez | `luis.gomez@springale.edu` | `Estudiante123*` |
| Estudiante | Sofía Ramírez | `sofia.ramirez@springale.edu` | `Estudiante123*` |

Los datos de demostración se cargan automáticamente si la tabla de usuarios está vacía. También están incluidos en `database/sistema_academico.sql`.

### Login de ejemplo

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@springale.edu","password":"Admin123*"}'
```

Respuesta:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "usuarioId": 1,
  "nombreCompleto": "Laura Administradora",
  "email": "admin@springale.edu",
  "rol": "ADMINISTRADOR",
  "estudianteId": null,
  "expiraEnMs": 86400000
}
```

---

## Endpoints principales

| Módulo | Ruta base | Descripción |
|---|---|---|
| Autenticación | `/auth` | Registro de estudiantes e inicio de sesión con JWT |
| Usuarios | `/api/usuarios` | Consulta y administración de usuarios |
| Estudiantes | `/api/estudiantes` | Perfiles, materias, semillero y datos propios (`/me`) |
| Semilleros | `/api/semilleros` | Catálogo y estudiantes por semillero |
| Materias | `/api/materias` | Catálogo de materias |
| Periodos | `/api/periodos` | Periodos académicos y periodo activo |
| Calificaciones | `/api/calificaciones` | Notas, historial, promedio y rendimiento |
| Tareas | `/api/tareas` | Tareas, entregas, alertas y atrasos |
| Llegadas tardías | `/api/llegadas-tardias` | Registro e historial de retardos |
| Proyectos | `/api/proyectos` | Proyectos de equipo, integrantes, aportes y consolidado |
| Tutorías | `/api/tutorias` | Postulaciones, tutorías, estados y mensajes |
| Grupos de estudio | `/api/grupos-estudio` | Grupos, miembros y consulta por estudiante |
| Publicaciones | `/api/publicaciones` | Apuntes, anuncios y recursos por grupo/autor |
| Gamificación | `/api/gamificacion` | Puntos, niveles, logros y ranking |
| Notificaciones | `/api/notificaciones` | Bandeja, no leídas y marcado como leídas |

La referencia completa, con parámetros y esquemas de request/response, está disponible en Swagger.

---

## Reglas de negocio destacadas

- Todas las rutas, excepto `/auth/**` y la documentación de Swagger, requieren JWT.
- Roles disponibles: `ADMINISTRADOR`, `PROFESOR` y `ESTUDIANTE`.
- Un estudiante solo puede consultar sus propias calificaciones, tareas, tutorías y notificaciones.
- Profesores y administradores pueden consultar y gestionar calificaciones de cualquier estudiante.
- La nota debe estar entre `0.0` y `5.0`.
- Para postularse como tutor se requiere promedio mínimo `3.8`; con `4.0` o más se aprueba automáticamente.
- El chat de una tutoría solo está disponible cuando el estado es `ACEPTADA` o `COMPLETADA`.
- Las transiciones de estado de una tutoría se validan; no se permiten cambios arbitrarios.
- Un usuario solo puede publicar en un grupo del que sea miembro.
- Las operaciones de eliminación en grupos y publicaciones aplican borrado en cascada cuando corresponde.
- Los errores de validación, parámetros faltantes, recursos inexistentes y conflictos devuelven respuestas JSON consistentes con su código HTTP.

---

## Pruebas

```bat
mvnw.cmd test
```

En Git Bash o Linux/macOS:

```bash
./mvnw test
```

Las pruebas usan H2 en memoria mediante `src/test/resources/application-test.properties`, por lo que no requieren MySQL.

---

## Estructura de datos

El script `database/sistema_academico.sql` contiene:
- Creación de la base `sistema_academico`.
- Tablas para usuarios, estudiantes, semilleros, materias, periodos, calificaciones, tareas, entregas, llegadas tardías, proyectos, equipos, tutorías, mensajes, grupos, publicaciones, gamificación y notificaciones.
- Datos de demostración y credenciales de prueba.

Si se prefiere empezar sin datos, basta con crear la base vacía y dejar que Hibernate y `DataInitializer` generen el esquema y los datos iniciales.

---

## Notas de seguridad

- El secreto JWT incluido es solo para desarrollo. En producción debe reemplazarse con `JWT_SECRET`.
- Cambiar las contraseñas de los usuarios de demostración antes de desplegar.
- Configurar CORS según el dominio real del frontend; actualmente se permiten todos los orígenes para facilitar el desarrollo.
- Deshabilitar Swagger o restringirlo en producción si no se desea exponer la documentación.
