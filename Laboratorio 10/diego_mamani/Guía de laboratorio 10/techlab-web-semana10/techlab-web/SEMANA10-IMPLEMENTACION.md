# Semana 10 — TechLab Web

Se implementó el CRUD de cursos sobre la base de Semana 9, conservando búsqueda por título, filtro por horas, categorías, resumen, DTO, mapper, MySQL y migraciones existentes.

## Cambios
- DTO de entrada `CourseInputDto` (título, horas y categoría; la categoría vacía usa `General`).
- Servicio transaccional para crear, actualizar y eliminar, normalización de datos, validación mínima y detección de títulos duplicados.
- API REST: `GET /api/v1/cursos`, `GET /api/v1/cursos/{id}`, `POST /api/v1/cursos`, `PUT /api/v1/cursos/{id}`, `DELETE /api/v1/cursos/{id}`.
- Errores REST en formato `ProblemDetail`: 400, 404 y 409. Creación devuelve 201 + `Location`; actualización 200; eliminación 204.
- Formularios Thymeleaf para crear y editar, acciones de edición/eliminación en listado y detalle, confirmación JavaScript y patrón Post/Redirect/Get.
- Pruebas de integración del ciclo CRUD y reglas de negocio.

## Ejecutar
1. Configura `DB_PASSWORD` según tu entorno y confirma que MySQL esté activo.
2. Ejecuta `docker compose up -d mysql` y `docker compose ps`.
3. Ejecuta `./mvnw clean test` (Windows: `mvnw.cmd clean test`).
4. Inicia la aplicación y abre `http://localhost:8080/cursos`.

No se modificaron las migraciones V1, V2 ni V3; el proyecto ya incluía V3 para `categoria`, por lo que el CRUD la conserva y solicita ese dato en el formulario/API (opcional, predeterminado `General`).
