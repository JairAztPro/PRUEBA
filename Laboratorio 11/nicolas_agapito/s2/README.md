# TechLab Web · Semana 11

Proyecto acumulativo de Marcos de Desarrollo Web (UTP): CRUD web y REST de cursos, relación JPA muchos-a-uno con categorías, validación de entrada y restricciones de integridad en MySQL.

**Tecnologías de la guía:** Java 25 · Spring Boot 4.1.1 · Jakarta Validation · Spring Data JPA · Flyway · MySQL 8.4 · Thymeleaf · Bootstrap.

## Ejecutar

1. Copie `.env.example` a `.env` y configure contraseñas locales.
2. Inicie Docker Desktop.
3. Ejecute `docker compose up -d mysql` y espere que `docker compose ps` muestre `healthy`.
4. Configure `DB_USER` y `DB_PASSWORD` en la terminal donde ejecutará Java.
5. Ejecute `./mvnw clean test` (Windows: `mvnw.cmd clean test`).
6. Ejecute `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`).
7. Abra <http://localhost:8080/cursos>.

## Migraciones

- `V1`: tabla de cursos y restricciones de título/horas.
- `V2`: catálogo inicial de cursos.
- `V3`: columna de categoría textual de la entrega anterior.
- `V4`: crea `categorias`, convierte las categorías heredadas a claves foráneas y elimina la columna textual antigua.

**Importante:** la guía de Semana 11 propone una migración `V3`, pero este ZIP ya trae una `V3` aplicada de la Semana 9. Por eso se conserva sin cambios y se incorpora el mismo cambio relacional como `V4`, para no invalidar el historial de Flyway ni requerir borrar el volumen MySQL.

## Qué incluye

- `Course` con `@ManyToOne(fetch = LAZY, optional = false)` hacia `Category`, sin cascada de borrado.
- `CourseInputDto` con Jakarta Validation y mensajes externalizados.
- `CourseDto` plano con `categoriaId` y `categoriaNombre`.
- `EntityGraph` para listados y búsquedas; `join fetch` para detalle.
- Formulario Thymeleaf con selector de categoría, `BindingResult`, errores por campo y accesibilidad.
- API REST validada y errores `ProblemDetail` con mapa `errors`.
- Filtro opcional por categoría en el catálogo web y la API.
- Pruebas de validación, controladores e integración de FK.

## Verificación de MySQL

```sql
SELECT c.id, c.titulo, c.horas, k.nombre AS categoria
FROM cursos c JOIN categorias k ON k.id = c.categoria_id ORDER BY c.id;
SHOW CREATE TABLE cursos;
SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank;
```

La suite no se pudo ejecutar durante la preparación porque Maven no pudo descargar su distribución desde Maven Central en este entorno. Ejecute `clean test` localmente y compruebe `BUILD SUCCESS`; las pruebas de integración requieren MySQL disponible.
