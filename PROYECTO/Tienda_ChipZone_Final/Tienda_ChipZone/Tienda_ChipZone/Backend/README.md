# TechLab Web (ChipZone) - Backend Spring Boot

Backend de la tienda online de articulos tecnologicos (curso Marcos de Desarrollo Web).
**Frontend y backend estan separados.**

```
Tienda_ChipZone/
├── Frontend/    tienda para el cliente (HTML, CSS, JS); consume la API con fetch
└── Backend/     este proyecto (Spring Boot)
    ├── ProductoController        API REST (JSON): lectura semana 6, CRUD semana 10
    ├── ProductoApiExceptionHandler  errores de la API como application/problem+json  semana 10
    ├── ProductoVistaController   panel Thymeleaf: lectura semana 7, CRUD con Post-Redirect-Get semana 10
    ├── ProductoService           logica compartida (lecturas readOnly; crear/actualizar/eliminar transaccionales)
    ├── ProductoMapper            entidad JPA -> DTO                semana 9
    ├── ProductoRepository        acceso a MySQL con Spring Data JPA
    └── db/migration (Flyway)     V1 esquema, V2 catalogo inicial   semana 9
```

## Requisitos
- JDK 21 o superior (las guias usan Java 25; para cambiarlo, editar `java.version` en `pom.xml`).
- **MySQL/MariaDB**: por defecto la app usa **XAMPP** (`localhost:3306`, base `bd_chipzone`, usuario `root` sin clave).
  Hay que crear la base vacia (`CREATE DATABASE bd_chipzone CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`);
  Flyway crea las tablas al arrancar. **Docker** es opcional (ver abajo).
- Puertos libres: `8080` (aplicacion) y `3306` (XAMPP) o `3307` (MySQL de Docker).
- Internet la primera vez (dependencias de Maven y CDN de Bootstrap).
- No hace falta Maven: se usa el wrapper (`mvnw`).

## Base de datos (MySQL 8.4 en Docker + Flyway) - Semana 9
El catalogo ya no se crea desde Java: **Flyway** manda sobre el esquema y Hibernate solo lo valida
(`ddl-auto=validate`). No existen `schema.sql`, `data.sql` ni `DataInitializer`.

| Migracion | Contenido |
|---|---|
| `V1__create_productos_tables.sql` | Tablas `productos`, `producto_imagenes`, `producto_etiquetas`, `producto_especificaciones` |
| `V2__seed_productos.sql` | Catalogo inicial: 47 productos, 4 categorias |

1. Crear el archivo de secretos local (no se sube a git):
   ```
   cp .env.example .env        (Windows: copy .env.example .env)
   ```
   y editar `.env` con contrasenas reales locales.
2. Levantar MySQL y esperar el estado `healthy`:
   ```
   docker compose up -d mysql
   docker compose ps
   ```
3. Exportar las credenciales en la terminal donde se ejecute Maven o el JAR (mismos valores que `.env`):
   ```
   export DB_USER='techlab_app'              (PowerShell: $env:DB_USER='techlab_app')
   export DB_PASSWORD='valor_de_su_env'      (PowerShell: $env:DB_PASSWORD='valor_de_su_env')
   ```

| Variable | Significado | Valor por defecto |
|---|---|---|
| `DB_URL` | URL JDBC completa | `jdbc:mysql://localhost:3306/bd_chipzone?serverTimezone=UTC` (XAMPP) |
| `DB_USER` | Usuario de MySQL | `root` |
| `DB_PASSWORD` | Contrasena de MySQL | (vacia, como XAMPP) |

Con el MySQL de Docker hay que exportar las tres variables (puerto `3307`, base `techlab`, usuario y clave de `.env`).

Verificar MySQL y Flyway:
```
docker compose exec mysql mysql -utechlab_app -p techlab
SELECT id, nombre, categoria FROM productos ORDER BY id;
SELECT installed_rank, version, description, success FROM flyway_schema_history ORDER BY installed_rank;
```
Si el backend no arranca, revisar que el contenedor este `healthy` y que `DB_USER`/`DB_PASSWORD` coincidan con `.env`.

## Ejecucion (desde la carpeta que contiene `pom.xml`)
```
.\mvnw.cmd spring-boot:run        (Windows)
./mvnw spring-boot:run            (macOS / Linux)
```

## Rutas
| Ruta | Tipo | Descripcion |
|---|---|---|
| `/api/v1/productos` | API JSON | Lista; admite `q` (nombre o marca) y `categoria` |
| `/api/v1/productos/{id}` | API JSON | Detalle (404 si no existe, 400 si el id no es numerico) |
| `POST /api/v1/productos` | API JSON | Crea (201 + `Location`; 400 datos invalidos; 409 nombre duplicado) |
| `PUT /api/v1/productos/{id}` | API JSON | Actualiza (200; 400; 404; 409) |
| `DELETE /api/v1/productos/{id}` | API JSON | Elimina (204; 404) |
| `/admin/productos` | Vista Thymeleaf | Listado con busqueda por GET |
| `/admin/productos/{id}` | Vista Thymeleaf | Detalle con `th:object` |
| `/admin/productos/nuevo` | Vista Thymeleaf | Formulario de alta |
| `POST /admin/productos` | Formulario | Crea y redirige al detalle (Post-Redirect-Get) |
| `/admin/productos/{id}/editar` | Vista Thymeleaf | Formulario de edicion |
| `POST /admin/productos/{id}` | Formulario | Actualiza y redirige al detalle |
| `POST /admin/productos/{id}/eliminar` | Formulario | Elimina (con confirmacion en JS) y redirige al listado |
| `/`, `/portal` | Redireccion | 302 a `/admin/productos` |
| `/actuator/health` | Salud | `{"status":"UP"}` |

## Conectar el frontend
1. Levantar el backend (puerto 8080).
2. Abrir `Frontend/html/productos-api.html` con Live Server (puerto 5500).
3. En el JS del frontend, llamar a la API con la URL completa:
```js
const API_URL = "http://localhost:8080";
const respuesta = await fetch(`${API_URL}/api/v1/productos?q=ryzen&categoria=gaming`);
if (!respuesta.ok) throw new Error(`HTTP ${respuesta.status}`);
const productos = await respuesta.json();
```
Campos de cada producto: `id`, `nombre`, `nombreCompleto`, `marca`, `categoria`, `precio`,
`descuento`, `stock`, `resumen`, `descripcion`, `imagenes`, `etiquetas`, `especificaciones`.

Las escrituras (POST/PUT/DELETE) estan pensadas para el panel y para clientes como Postman o curl; el frontend
de la tienda solo lee. Ejemplo de cuerpo JSON (`nombre`, `marca`, `categoria`, `precio` y `stock` son obligatorios;
`marca` y `categoria` se guardan en minusculas; `nombreCompleto` vacio usa el nombre):
```json
{"nombre":"Mouse Nuevo","marca":"msi","categoria":"accesorios","precio":89,"descuento":10,"stock":true,
 "imagenes":["https://ejemplo.com/mouse.jpg"],"etiquetas":["RGB"],
 "especificaciones":[{"clave":"Conexion","valor":"USB"}]}
```
Los errores de la API (400, 404, 409) responden con `application/problem+json`.

CORS solo permite GET desde `http://localhost:5500` y `http://127.0.0.1:5500`.
Para otro origen, cambiar `techlab.cors.origins` en `application.properties`.

## Estructura
- `controller/`: `ProductoController` (API), `ProductoVistaController` (vistas), `PortalController`.
- `entity/` (`Producto`, `Especificacion`; sin setters), `repository/ProductoRepository`, `mapper/ProductoMapper` y `service/ProductoService`: datos en MySQL.
- `dto/`: `ProductoDto` (salida), `ProductoInputDto` (entrada JSON, con restricciones Bean Validation) y `ProductoFormDto` (texto del formulario HTML, con restricciones de forma).
- `exception/`: `InvalidProductoDataException` (400), `ProductoNotFoundException` (404), `DuplicateProductoNombreException` (409).
- `config/CorsConfig`, `bootstrap/StartupReporter`. `resources/db/migration/`: scripts de Flyway.
- `templates/`: `fragments/layout.html` (head, cabecera, pie), `productos/lista`, `productos/detalle`, `productos/formulario`, `error/4xx`.
- `ValidationMessages.properties`: mensajes de Bean Validation (`{producto.*}`). `messages.properties`: textos reutilizables. `static/css/panel.css`: estilos del panel. `static/js/site.js`: confirmacion antes de eliminar.

## Temas de las guias aplicados
- Semana 5: Spring Boot, `application.properties`, perfil `local`, `StartupReporter`, `/actuator/health`, JAR.
- Semana 6: `@RestController`, `@RequestParam`, `@PathVariable`, DTO, servicio, estados 400/404/405, MockMvc, consumo con `fetch`.
- Semana 9: MySQL en Docker (`compose.yaml`, `.env`), JPA/Hibernate, Spring Data JPA, Flyway (V1/V2), `ProductoMapper`,
  servicio `@Transactional(readOnly = true)`, pruebas web aisladas con `@MockitoBean` y pruebas de persistencia contra MySQL real.
- Semana 10: CRUD completo en vistas (Post-Redirect-Get, mensajes flash) y en la API (201/200/204/400/404/409 con `ProblemDetail`),
  `ProductoInputDto`, excepciones de negocio, escrituras `@Transactional` con dirty checking y pruebas CRUD contra la base real.
- Semana 11: Jakarta Bean Validation (`spring-boot-starter-validation`, imports `jakarta.validation.*`) en lugar de la validacion manual:
  `@Valid` en la API (400 `application/problem+json` con el atributo `errors` por campo) y en el formulario (`@Valid` + `BindingResult`,
  `th:errors`, `is-invalid` y `aria-invalid`, sin perder lo escrito); `@Validated` en el servicio, que conserva solo la normalizacion y las
  reglas que dependen de datos (nombre unico, especificacion completa). **No se aplico** la parte de relacion `ManyToOne`/V3 de la guia:
  `categoria` ya es una columna de `productos` desde V1, por lo que no hay `Category`, `CategoryDto` ni consultas N+1 que resolver.
- Semana 7: `@Controller` + `Model`, `th:each`, `th:if/unless`, `th:text`, `th:value`, `th:href`, `th:object`/`*{}`,
  fragmentos, mensajes `#{}`, pagina de error 4xx, busqueda por GET.

## Pruebas
Con MySQL levantado y `DB_USER`/`DB_PASSWORD` exportadas:
```
.\mvnw.cmd clean test        (Windows)
./mvnw clean test            (macOS / Linux)
```
- `ProductoFormDtoTests`, `ProductoInputDtoValidationTests` y `ProductoFormDtoValidationTests`: pruebas unitarias puras (sin Spring ni base de datos).
- `ProductoVistaTests` y `WebRoutesTests`: `@WebMvcTest` con `ProductoService` simulado (`@MockitoBean`); no usan base de datos.
- `ProductoPersistenceIntegrationTests`, `ProductoCrudIntegrationTests`, `ProductoServiceTests` y `TechLabWebApplicationTests`:
  `@SpringBootTest` contra **MySQL real** (Flyway aplica V1 y V2). Si MySQL no esta encendido, estas fallan.
  `ProductoCrudIntegrationTests` usa `@Transactional` y hace rollback: el catalogo de 47 productos no cambia.
  Estas pruebas asumen el catalogo inicial intacto (47 productos): no ejecutarlas sobre una base con altas o bajas manuales.
- Ya no se usa H2: los scripts de Flyway son de MySQL.

Ultimo resultado registrado: pendiente de ejecutar con la configuracion de la Semana 11.

## Empaquetado y perfiles
```
.\mvnw.cmd clean package
java -jar target/techlab-web-0.0.1-SNAPSHOT.jar
java -jar target/techlab-web-0.0.1-SNAPSHOT.jar --spring.profiles.active=local   (puerto 8081)
```
El perfil `local` solo cambia el puerto y no contiene secretos.

## Alcance actual
Catalogo guardado en MySQL y versionado con Flyway, con CRUD completo en el panel y en la API. Todavia no hay login ni
autorizacion: **cualquiera con acceso a la URL puede crear, editar o eliminar productos** (semanas posteriores).
Las categorias Laptops y Celulares existen en el menu de la tienda, pero aun no tienen productos.
El carrito, el login y el registro de la tienda siguen simulados con JavaScript en el frontend.

## Matriz de pruebas (registrar resultado real)
| ID | Procedimiento | Esperado | Resultado real | Estado | Correccion |
|---|---|---|---|---|---|
| CP-01 | `java --version` | JDK activo (21) | | | |
| CP-02 | `mvnw spring-boot:run` | Tomcat escucha en 8080 | | | |
| CP-03 | `/actuator/health` | 200 y `{"status":"UP"}` | | | |
| CP-04 | Log de inicio | "techlab-web disponible en http://localhost:8080" | | | |
| CP-05 | `mvnw clean test` (con MySQL en Docker) | BUILD SUCCESS, 0 fallos | | | |
| CP-06 | `clean package` y `java -jar` | JAR inicia y responde | | | |
| CP-07 | Perfil `local`, luego sin perfil | 8081 y luego 8080 | | | |
| CP-08 | `git status` | Sin `target`, secretos ni archivos del IDE | | | |
| S6-01 | GET /api/v1/productos | 200 y 47 productos | | | |
| S6-02 | GET /api/v1/productos?q=ryzen | 1 producto, id 4 | | | |
| S6-03 | GET /api/v1/productos?q=ZZZ | 200 y lista vacia | | | |
| S6-04 | GET /api/v1/productos/2 | 200 RTX 4060 Gaming | | | |
| S6-05 | GET /api/v1/productos/999 | 404 | | | |
| S6-06 | GET /api/v1/productos/abc | 400 | | | |
| S6-07 | q de 61 caracteres | 400 | | | |
| S6-08 | DELETE /api/v1/productos (sobre la coleccion) | 405 | | | |
| S6-09 | `productos-api.html` (Live Server :5500) con backend encendido | Lista los 47 productos (CORS correcto) | | | |
| S6-10 | Misma pagina con backend apagado | Mensaje de error y boton habilitado | | | |
| S7-01 | GET /admin/productos | 47 filas; en "Ver codigo fuente" no hay `th:*` | | | |
| S7-02 | GET /admin/productos?q=ryzen | 1 fila y campo con "ryzen" | | | |
| S7-03 | GET /admin/productos?q=zzz | Estado vacio y "0 producto(s)" | | | |
| S7-04 | GET /admin/productos?categoria=accesorios | 13 filas | | | |
| S7-05 | GET /admin/productos/2 | Detalle de RTX 4060 con S/ 749 | | | |
| S7-06 | GET /admin/productos/999 | Vista 4xx y estado 404 | | | |
| S7-07 | q de 61 caracteres en /admin/productos | 400 | | | |
| S7-08 | GET / | 302 a /admin/productos | | | |
| S7-09 | 320, 768 y 1280 px | Sin desplazamiento horizontal | | | |
| S7-10 | Tab / Shift+Tab y "Saltar al contenido" | Foco visible | | | |
| S7-11 | Zoom 200 % | Contenido legible y operable | | | |
| S10-01 | POST /api/v1/productos con JSON valido | 201, cabecera `Location` y producto con id nuevo | | | |
| S10-02 | POST con `nombre` vacio o `precio` 0 | 400 `application/problem+json` | | | |
| S10-03 | POST con un nombre ya existente (otra mayuscula) | 409 | | | |
| S10-04 | PUT /api/v1/productos/{id} y luego GET del mismo id | 200 y datos actualizados | | | |
| S10-05 | DELETE /api/v1/productos/{id} y luego GET | 204 y luego 404 | | | |
| S10-06 | PUT/DELETE de un id inexistente | 404 `application/problem+json` | | | |
| S10-07 | /admin/productos/nuevo: registrar un producto valido | Redirige al detalle con mensaje verde | | | |
| S10-08 | Mismo formulario con nombre repetido | Vuelve al formulario con el error y los datos escritos | | | |
| S10-09 | Editar un producto desde el listado | Formulario precargado; al guardar, mensaje de exito | | | |
| S10-10 | Eliminar desde el listado | Aparece confirmacion; al aceptar, vuelve al listado con mensaje | | | |
| S11-01 | POST /api/v1/productos con cuerpo `{}` | 400 `application/problem+json` con `errors` para nombre, marca, categoria, precio y stock | | | |
| S11-02 | POST con `precio` 0, `descuento` 101 e imagen `javascript:alert(1)` | 400 con un mensaje por campo (`precio`, `descuento`, `imagenes[0]`) | | | |
| S11-03 | PUT con `nombre` vacio | 400 con `errors.nombre`; el producto no cambia | | | |
| S11-04 | /admin/productos/nuevo: enviar el formulario vacio | Vuelve al formulario con un mensaje junto a cada campo obligatorio y alerta de resumen | | | |
| S11-05 | Formulario con precio 0 y nombre escrito | Error en precio; el nombre y el resto de lo escrito se conservan | | | |
| S11-06 | Formulario con una especificacion sin `:` | Error junto al campo Especificaciones tecnicas | | | |
| S11-07 | Formulario con nombre repetido | Error junto al campo Nombre y datos conservados | | | |
| S11-08 | Lector de pantalla / teclado en un campo invalido | `aria-invalid="true"` y el mensaje enlazado con `aria-describedby`; no depende solo del color | | | |
| S11-09 | Crear un producto valido, reiniciar la aplicacion y abrirlo | Sigue en el listado; `flyway_schema_history` solo muestra V1 y V2 | | | |

## Creditos
Bootstrap 5.3.8 via CDN. Imagenes de sitios de los fabricantes y tiendas (enlaces remotos).