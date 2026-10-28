# TechLab Web — Semana 11

## Cambios implementados
- Relación JPA unidireccional `Course @ManyToOne(fetch = LAZY) -> Category`, obligatoria y sin cascada de borrado.
- DTO de entrada con Jakarta Validation: título obligatorio y máximo 120 caracteres; horas entre 1 y 500; categoría obligatoria.
- DTO de salida plano con `categoriaId` y `categoriaNombre`.
- Repositorio con `EntityGraph` para listados/búsquedas y `join fetch` para el detalle, incluyendo el filtro opcional por categoría.
- Validación MVC con `BindingResult`, errores al lado del campo, conservación de valores y select accesible de categorías.
- API REST valida JSON con `@Valid` y devuelve `ProblemDetail` con mapa `errors` por campo; categoría inexistente es 400, curso inexistente 404 y título duplicado 409.
- Integridad MySQL: tabla `categorias`, `NOT NULL`, `UNIQUE`, `CHECK` existente para horas, índice y FK con `ON DELETE RESTRICT`.
- Pruebas unitarias de DTO, web REST/MVC y de integración de relación/FK.

## Nota importante sobre Flyway
El ZIP de entrada ya tenía una migración `V3__add_categoria_to_cursos.sql` que agrega la columna textual `categoria`. Como una migración registrada no debe editarse ni reutilizarse, esta adaptación añade `V4__relacion_cursos_categorias.sql`: crea el catálogo, transforma los valores de texto existentes a IDs y elimina la columna heredada tras copiar su información. Se conservan intactos V1, V2 y V3.

## Ejecución local
1. Copie `.env.example` a `.env` y configure contraseñas locales no compartidas.
2. Inicie Docker Desktop.
3. `docker compose up -d mysql`
4. `docker compose ps` y espere a que MySQL esté `healthy`.
5. `./mvnw clean test` (Windows: `mvnw.cmd clean test`).
6. `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`).
7. Abra `http://localhost:8080/cursos`.

## Verificación de integridad
```sql
SELECT c.id, c.titulo, c.horas, k.nombre AS categoria
FROM cursos c JOIN categorias k ON k.id = c.categoria_id ORDER BY c.id;
SHOW CREATE TABLE cursos;
SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank;
```

La prueba de integración requiere MySQL disponible con la misma configuración que `application.properties`. No se declara éxito de compilación hasta ejecutar Maven en un entorno con las dependencias descargadas.
