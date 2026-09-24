# TechLab Web – Semana 7 (Thymeleaf)

Proyecto acumulativo del curso **Marcos de Desarrollo Web**.
Línea: Java 25 LTS · Spring Boot 4.1.1 · Thymeleaf 3.1.5 · Bootstrap 5.3.8.

## Qué incluye
- API REST de la semana 6 (`/api/v1/cursos`) sin cambios.
- Vistas Thymeleaf: catálogo `/cursos`, búsqueda `?q=`, detalle `/cursos/{id}` y resumen `/cursos/resumen` (reto).
- Fragmentos (`fragments/layout.html`), mensajes (`messages.properties`), estilos (`static/css/site.css`) y página de error `error/4xx.html`.
- Pruebas MockMvc (`CourseViewControllerTests`).

## Comandos
Linux / macOS / Git Bash:
```
./mvnw clean test
./mvnw spring-boot:run
./mvnw clean package
java -jar target/techlab-web-0.0.1-SNAPSHOT.jar
```
Windows PowerShell: usar `.\mvnw.cmd` en lugar de `./mvnw`.

## Rutas
| Ruta | Respuesta |
|---|---|
| `/` y `/portal` | 302 → `/cursos` |
| `/cursos` | HTML, catálogo (acepta `q`, máx. 60 caracteres, si no 400) |
| `/cursos/{id}` | HTML detalle; 404 con vista 4xx |
| `/cursos/resumen` | HTML resumen (3 cursos, 48 horas) |
| `/api/v1/cursos` | JSON |
| `/actuator/health` | Estado de salud |

## Reglas de continuidad
No se duplica la lista de cursos en plantillas ni JS; no se eliminó `CourseController`; no se sube `target/`.

## Evidencias
Capturas en `evidencias/` (ver `EVIDENCIAS_Y_RESPUESTAS.md`).
