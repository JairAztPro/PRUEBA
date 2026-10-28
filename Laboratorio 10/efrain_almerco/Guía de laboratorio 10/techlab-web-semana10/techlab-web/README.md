# TechLab Web · Semana 9 · Persistencia con JPA, Hibernate, Spring Data JPA, Flyway y MySQL

Curso **Marcos de Desarrollo Web** (UTP). Línea técnica: Java 25 · Spring Boot 4.1.1 · MySQL 8.4.12 · Flyway.
Sustituye únicamente la fuente de datos (lista en memoria → MySQL). Rutas, plantillas y contratos HTTP se conservan.
Alcance: solo lectura (sin altas, ediciones ni eliminaciones: semana 10; sin relaciones ni Bean Validation: semana 11).

**Línea base:** copia de la semana 8 (`techlab-web-integración.zip`, APF2) · fecha de la copia: 2026-10-21 · rama sugerida: `week-09-persistence`.

## 1. Cómo ejecutar

```bash
# 1) Secretos locales (el .env real NO se confirma en Git)
cp .env.example .env            # editar y poner dos claves locales distintas

# 2) MySQL reproducible
docker compose config --quiet
docker compose up -d mysql
docker compose ps               # esperar mysql = healthy
docker compose logs mysql --tail=30

# 3) Variables para el proceso Java (misma terminal que Maven)
export DB_USER='techlab_app' DB_PASSWORD='valor_de_su_env'      # Linux / macOS / Git Bash
# PowerShell: $env:DB_USER='techlab_app'; $env:DB_PASSWORD='valor_de_su_env'

# 4) Pruebas, empaquetado y JAR
./mvnw clean test               # PowerShell: .\mvnw.cmd clean test
./mvnw clean package
java -jar target/techlab-web-0.0.1-SNAPSHOT.jar
```

Abrir <http://localhost:8080>. Con el perfil `local` el puerto es 8081 (`--spring.profiles.active=local`).
Las pruebas web usan `@MockitoBean` y no necesitan MySQL; `CoursePersistenceIntegrationTests` sí (MySQL *healthy* y `DB_PASSWORD`).

## 2. Estructura

```
techlab-web/
├── .env.example  .gitignore  compose.yaml  pom.xml  mvnw / mvnw.cmd
└── src/
    ├── main/java/pe/edu/utp/techlab/
    │   ├── controller/   CourseController, CourseViewController, PortalController (sin cambios)
    │   ├── dto/CourseDto.java            record (id, titulo, horas, categoria)
    │   ├── entity/Course.java            @Entity → tabla cursos
    │   ├── mapper/CourseMapper.java      Course → CourseDto
    │   ├── repository/CourseRepository.java
    │   └── service/CourseService.java    @Transactional(readOnly = true)
    ├── main/resources/
    │   ├── application.properties
    │   ├── db/migration/  V1__create_cursos_table.sql · V2__seed_cursos.sql · V3__add_categoria_to_cursos.sql
    │   └── templates/ (lista y detalle muestran la categoría)
    └── test/java/pe/edu/utp/techlab/
        ├── controller/   CourseViewControllerTests · CourseControllerTests · WebRoutesTests (@MockitoBean)
        ├── persistence/  CoursePersistenceIntegrationTests (MySQL real)
        └── service/      CourseServiceTests (unitarias con Mockito)
```

## 3. Cambios respecto a la semana 8

1. `pom.xml`: `starter-data-jpa`, `starter-flyway`, `flyway-mysql`, `mysql-connector-j` (runtime) y `starter-data-jpa-test`. Sin versiones sueltas: solo la del parent.
2. `compose.yaml` + `.env.example`; `.env` añadido a `.gitignore`.
3. `application.properties`: DataSource con `DB_USER`/`DB_PASSWORD`, `ddl-auto=validate`, `open-in-view=false`, Flyway activo y errores sin mensaje, validaciones ni traza.
4. Migraciones V1 (estructura), V2 (catálogo inicial) y V3 (reto: `categoria`).
5. `Course` (entidad), `CourseRepository`, `CourseMapper`; `CourseService` reescrito sobre el repositorio.
6. `CourseDto` ahora incluye `categoria` (reto). Los controladores no cambiaron.
7. Pruebas web con `@MockitoBean` (sin `@Import(CourseService.class)`); se eliminó `TechlabWebApplicationTests` (su `contextLoads` queda cubierto por la prueba de integración).
8. Se **conservó el filtro `minHoras`** de la semana 8: el servicio mantiene `buscar(String)` y `buscar(String, Integer)`, y ahora el filtro lo ejecuta la base de datos con consultas derivadas.
9. `.gitignore`: solo se añadió `.env`. `application-local.properties` **no** se ignoró porque contiene únicamente el puerto 8081 (sin secretos) y es parte del proyecto de la semana 8.

### Dos diferencias deliberadas con el texto de la guía

- **`"boot"` devuelve dos cursos, no uno.** La guía espera solo "Spring Boot" en `repositorioBuscaSinDistinguirMayusculas`, pero "Bootstrap" también contiene "boot". La prueba espera `Bootstrap` y `Spring Boot`, y `q=boot&minHoras=17` aísla a Spring Boot (misma observación del README de la semana 8).
- **Las pruebas MVC simulan `buscar(texto, minHoras)`** (no `buscar(texto)`), porque `CourseViewController` conserva el filtro por horas y siempre invoca esa sobrecarga.

## 4. Flyway: historial esperado

```sql
SELECT installed_rank, version, description, success FROM flyway_schema_history ORDER BY installed_rank;
```

| installed_rank | version | description | success |
|---|---|---|---|
| 1 | 1 | create cursos table | 1 |
| 2 | 2 | seed cursos | 1 |
| 3 | 3 | add categoria to cursos | 1 |

`V3` se aplica sobre la base existente (sin borrar el volumen): agrega `categoria VARCHAR(40) NOT NULL DEFAULT 'General'`,
asigna Frontend (HTML y CSS, Bootstrap) y Backend (Spring Boot) y retira el valor por defecto transitorio.

## 5. Contrato de rutas (sin cambios de rutas)

| Ruta | Resultado |
|---|---|
| GET `/` o `/portal` | 302 → `/cursos` |
| GET `/cursos` | 200, tres tarjetas (con categoría) |
| GET `/cursos?q=boot` | 200, Bootstrap y Spring Boot |
| GET `/cursos?q=boot&minHoras=17` | 200, solo Spring Boot |
| GET `/cursos/2` | 200, detalle de Bootstrap con categoría |
| GET `/cursos/999` | 404 sin traza |
| GET `/cursos/resumen` | 200, 3 cursos y 48 horas |
| GET `/api/v1/cursos` | JSON con tres objetos (incluye `categoria`) |
| GET `/api/v1/cursos/999` | 404 sin cuerpo |
| GET `/actuator/health` | `status: UP` |

## 6. Matriz funcional de la semana 9 (ejecutar con el JAR)

Registre fecha, comando y evidencia. Los estados quedan sin marcar hasta ejecutarlos en su equipo.

| ID | Acción | Resultado esperado | Estado |
|---|---|---|---|
| P01 | `docker compose ps` | mysql healthy | □ |
| P02 | Consultar `cursos` | Tres filas y 48 horas | □ |
| P03 | Consultar `flyway_schema_history` | V1, V2 y V3 con success=1 | □ |
| F01 | Abrir `/cursos` | 200 y tres tarjetas | □ |
| F02 | Buscar `q=boot` | Bootstrap y Spring Boot | □ |
| F03 | Abrir `/cursos/2` | Detalle de Bootstrap | □ |
| F04 | Abrir `/cursos/999` | 404 sin traza | □ |
| F05 | Abrir `/cursos/resumen` | 3 cursos y 48 horas | □ |
| A01 | GET `/api/v1/cursos` | JSON con tres objetos | □ |
| A02 | GET `/actuator/health` | status UP | □ |
| R01 | Reiniciar aplicación | V1, V2 y V3 no se repiten | □ |
| R02 | Reiniciar contenedor | Datos aún disponibles | □ |
| RT1 | `/cursos?minHoras=16` | 2 tarjetas, campo conservado (regresión semana 8) | □ |
| RT2 | `/cursos?minHoras=abc` | 400 | □ |
| RT3 | Categoría visible en `/cursos`, `/cursos/2` y JSON | Frontend / Backend | □ |

Prueba de persistencia: `Ctrl+C` → `docker compose restart mysql` (esperar *healthy*) → iniciar el JAR con las mismas variables →
consultar `/cursos` y `flyway_schema_history` → los datos permanecen y Flyway no reaplica V1, V2 ni V3.
La permanencia viene del volumen `techlab_mysql_data`; el historial permite a Flyway saber qué migraciones ya corrió y validar sus checksums.

Consultas de verificación (el cliente pide la contraseña; no la escriba en el comando):

```bash
docker compose exec mysql mysql -utechlab_app -p techlab
```
```sql
SELECT id, titulo, horas, categoria FROM cursos ORDER BY id;
SELECT installed_rank, version, description, success FROM flyway_schema_history ORDER BY installed_rank;
EXIT;
```

## 7. Comprobación conceptual

| Pregunta | Respuesta |
|---|---|
| ¿Qué elemento es una especificación JPA? | **JPA** (Jakarta Persistence): define anotaciones e interfaces, no genera SQL por sí sola. |
| ¿Qué elemento implementa JPA? | **Hibernate**, proveedor administrado por Spring Boot. |
| ¿Quién genera el repositorio? | **Spring Data JPA**, a partir de la interfaz `CourseRepository`. |
| ¿Quién crea y versiona la tabla? | **Flyway**, con las migraciones V1, V2 y V3. |
| ¿Por qué se conserva `CourseDto`? | Para no filtrar el modelo de persistencia a MVC ni a REST. |

## 8. Reto de aplicación (categoría)

| Requisito | Dónde |
|---|---|
| 1-3. `V3__add_categoria_to_cursos.sql` con categorías específicas | `db/migration/` |
| 4. Mapeo en `Course`, `CourseDto` y `CourseMapper` | `entity/`, `dto/`, `mapper/` |
| 5. Categoría en listado, detalle y JSON (rutas intactas) | `lista.html`, `detalle.html`, `CourseDto` |
| 6. Consulta por categoría + prueba | `findByCategoriaIgnoreCaseOrderByIdAsc`, `CourseService.buscarPorCategoria`, `repositorioConsultaPorCategoria`, `buscarPorCategoriaNormalizaElCriterio` |
| 7. Matriz de regresión e historial V1-V2-V3 | secciones 4 y 6 |

Restricciones respetadas: V1 y V2 no se modificaron, no se borra el volumen, no se usa `ddl-auto=update` y no hay formularios de escritura.

| Criterio | Evidencia |
|---|---|
| Migración incremental | V3 aplicada sobre la base existente |
| Compatibilidad | La aplicación inicia con `ddl-auto=validate` |
| Contrato ampliado | Categoría visible en HTML y JSON |
| Consulta | Filtro por categoría ejecutado por el repositorio |
| Regresión | Pruebas anteriores y nuevas en verde (`./mvnw clean test`) |

## 9. Diagnóstico de errores frecuentes

| Síntoma | Causa probable | Corrección |
|---|---|---|
| Access denied | Usuario o clave distintos | Comparar `.env` y variables de la terminal |
| Communications link failure | Contenedor detenido o puerto incorrecto | `docker compose ps` / `logs`; puerto 3307 |
| Unknown database techlab | Volumen creado con otra configuración | Revisar `MYSQL_DATABASE` y el volumen |
| Flyway checksum mismatch | Se editó una migración aplicada | Restaurar el archivo y crear una nueva versión |
| Schema validation failed | Entidad y migración no coinciden | Comparar nombres, tipos, null y longitud |
| Application tests fail | `DB_PASSWORD` ausente | Definirla en la misma terminal |
| Prueba web busca DataSource | Se importó el servicio real | Usar `@MockitoBean` y quitar `@Import` |

Reinicio controlado (**destructivo**, solo entorno local sin datos que conservar): `docker compose down` → `docker volume rm techlab_mysql_data` → `docker compose up -d mysql`.

## 10. Preguntas de cierre

1. **¿Qué problema resuelve Flyway que `ddl-auto=update` no resuelve de forma auditable?**
   `update` altera el esquema de forma implícita según las entidades: no deja historia revisable, no es reproducible entre equipos, no maneja bien renombres o borrados y puede aplicar cambios no previstos. Flyway ejecuta migraciones numeradas e inmutables, registra versión, checksum y éxito en `flyway_schema_history` y detecta si alguien edita una migración ya aplicada.
2. **¿Por qué una entidad JPA no debería convertirse automáticamente en la respuesta REST?**
   Acopla el contrato público al esquema (un cambio en la tabla rompe a los clientes), puede exponer campos internos, arrastra asociaciones perezosas (`LazyInitializationException` o recursión al serializar) y mezcla anotaciones de persistencia con la representación. El DTO deja evolucionar ambas partes por separado.
3. **¿Dónde termina la transacción cuando `open-in-view` está desactivado?**
   Al salir del método de `CourseService` (límite `@Transactional(readOnly = true)`). Después las entidades quedan desasociadas, por eso el mapper convierte a DTO dentro del servicio y la vista no puede disparar consultas.
4. **¿Qué prueba fallaría primero si se renombra la columna `titulo` solo en la entidad?**
   Fallaría el arranque del contexto por `ddl-auto=validate` (`Schema validation: missing column`), así que lo primero en romperse es `CoursePersistenceIntegrationTests` (todas sus pruebas, pues usan `@SpringBootTest`). Las pruebas web con `@MockitoBean` y las unitarias del servicio seguirían en verde porque no cargan JPA: una razón más para mantener la prueba contra MySQL real.
5. **¿Qué partes permanecerán estables cuando se agregue CRUD en la semana 10?**
   La estructura por capas, `CourseDto` y `CourseMapper` (se ampliará con el sentido inverso), la entidad y su tabla, las rutas de lectura, las plantillas de consulta, el Compose, el `.env` y el historial de Flyway (cualquier cambio de esquema será una V4). Se añadirán métodos `@Transactional` de escritura en el servicio y rutas POST/PUT/DELETE, sin tocar la lectura.

## 11. Reflexión individual (borrador: ajústelo con su experiencia)

- **Decisión que mejoró la mantenibilidad:** haber concentrado el acceso a datos en `CourseService`; cambiar la fuente solo tocó servicio, repositorio y mapper, y los controladores y plantillas siguieron igual.
- **Fallo encontrado:** el ejemplo de la guía esperaba un solo resultado para `"boot"`, pero "Bootstrap" también coincide; se corrigió la expectativa de la prueba en lugar de alterar los datos.
- **Cuidado para la semana 10:** nunca editar V1-V3; cualquier cambio de esquema se hace con una nueva versión.
