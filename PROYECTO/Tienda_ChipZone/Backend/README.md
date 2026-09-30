# TechLab Web (ChipZone) - Backend Spring Boot

Backend de la tienda online de articulos tecnologicos (curso Marcos de Desarrollo Web).
**Frontend y backend estan separados.**

```
Tienda_ChipZone/
├── Frontend/    tienda para el cliente (HTML, CSS, JS); consume la API con fetch
└── Backend/     este proyecto (Spring Boot)
    ├── ProductoController        API REST (JSON)                  semana 6
    ├── ProductoVistaController   panel Thymeleaf de solo lectura  semana 7
    ├── ProductoService           logica compartida por ambos
    └── ProductoRepository        acceso a MySQL con Spring Data JPA
```

## Requisitos
- JDK 21 o superior (las guias usan Java 25; para cambiarlo, editar `java.version` en `pom.xml`).
- **MySQL** encendido en `localhost:3306` (por ejemplo con XAMPP).
- Internet la primera vez (dependencias de Maven y CDN de Bootstrap).
- No hace falta Maven: se usa el wrapper (`mvnw`).

## Base de datos (MySQL)
La aplicacion usa la base `bd_tiendachipzone`. Spring la crea sola la primera vez
(`createDatabaseIfNotExist=true`), crea las tablas (`ddl-auto=update`) y `DataInitializer`
carga los 9 productos del catalogo si la tabla esta vacia.

Por defecto se conecta con usuario `root` y **sin contrasena**. Para otros datos, definir
variables de entorno antes de ejecutar:

| Variable | Significado | Valor por defecto |
|---|---|---|
| `DB_URL` | URL JDBC completa | `jdbc:mysql://localhost:3306/bd_tiendachipzone?...` |
| `DB_USER` | Usuario de MySQL | `root` |
| `DB_PASSWORD` | Contrasena de MySQL | (vacia) |

Si el backend no arranca, revisar primero que MySQL este encendido y que usuario y contrasena sean correctos.

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
| `/admin/productos` | Vista Thymeleaf | Listado con busqueda por GET |
| `/admin/productos/{id}` | Vista Thymeleaf | Detalle con `th:object` |
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

CORS solo permite GET desde `http://localhost:5500` y `http://127.0.0.1:5500`.
Para otro origen, cambiar `techlab.cors.origins` en `application.properties`.

## Estructura
- `controller/`: `ProductoController` (API), `ProductoVistaController` (vistas), `PortalController`.
- `entity/` (`Producto`, `Especificacion`), `repository/ProductoRepository` y `service/ProductoService`: datos en MySQL.
- `dto/ProductoDto`: forma en que los datos salen por la API.
- `config/CorsConfig`, `bootstrap/StartupReporter`, `bootstrap/DataInitializer` (carga inicial del catalogo).
- `templates/`: `fragments/layout.html` (head, cabecera, pie), `productos/lista`, `productos/detalle`, `error/4xx`.
- `messages.properties`: textos reutilizables. `static/css/panel.css`: estilos del panel.

## Temas de las guias aplicados
- Semana 5: Spring Boot, `application.properties`, perfil `local`, `StartupReporter`, `/actuator/health`, JAR.
- Semana 6: `@RestController`, `@RequestParam`, `@PathVariable`, DTO, servicio, estados 400/404/405, MockMvc, consumo con `fetch`.
- Semana 7: `@Controller` + `Model`, `th:each`, `th:if/unless`, `th:text`, `th:value`, `th:href`, `th:object`/`*{}`,
  fragmentos, mensajes `#{}`, pagina de error 4xx, busqueda por GET.

## Pruebas
```
.\mvnw.cmd test        (Windows)
./mvnw test            (macOS / Linux)
```
Las pruebas **no usan MySQL**: `src/test/resources/application.properties` configura una base H2 en memoria,
que `DataInitializer` llena con los 9 productos. Si se agregan productos al catalogo inicial, hay que
actualizar los conteos esperados en los tests.

Ultimo resultado: `BUILD SUCCESS`, 17 pruebas, 0 fallos, 0 errores (29/09/2026).

## Empaquetado y perfiles
```
.\mvnw.cmd clean package
java -jar target/techlab-web-0.0.1-SNAPSHOT.jar
java -jar target/techlab-web-0.0.1-SNAPSHOT.jar --spring.profiles.active=local   (puerto 8081)
```
El perfil `local` solo cambia el puerto y no contiene secretos.

## Alcance actual
Catalogo de solo lectura guardado en MySQL: sin login ni formularios de escritura (semanas posteriores).
Las categorias Laptops y Celulares existen en el menu de la tienda, pero aun no tienen productos.
El carrito, el login y el registro de la tienda siguen simulados con JavaScript en el frontend.

## Matriz de pruebas (registrar resultado real)
| ID | Procedimiento | Esperado | Resultado real | Estado | Correccion |
|---|---|---|---|---|---|
| CP-01 | `java --version` | JDK activo (21) | | | |
| CP-02 | `mvnw spring-boot:run` | Tomcat escucha en 8080 | | | |
| CP-03 | `/actuator/health` | 200 y `{"status":"UP"}` | | | |
| CP-04 | Log de inicio | "techlab-web disponible en http://localhost:8080" | | | |
| CP-05 | `mvnw test` | BUILD SUCCESS, 0 fallos | BUILD SUCCESS, 17 pruebas, 0 fallos | OK | Se adaptaron los tests a H2 |
| CP-06 | `clean package` y `java -jar` | JAR inicia y responde | | | |
| CP-07 | Perfil `local`, luego sin perfil | 8081 y luego 8080 | | | |
| CP-08 | `git status` | Sin `target`, secretos ni archivos del IDE | | | |
| S6-01 | GET /api/v1/productos | 200 y 9 productos | | | |
| S6-02 | GET /api/v1/productos?q=ryzen | 1 producto, id 4 | | | |
| S6-03 | GET /api/v1/productos?q=ZZZ | 200 y lista vacia | | | |
| S6-04 | GET /api/v1/productos/2 | 200 RTX 4060 Gaming | | | |
| S6-05 | GET /api/v1/productos/999 | 404 | | | |
| S6-06 | GET /api/v1/productos/abc | 400 | | | |
| S6-07 | q de 61 caracteres | 400 | | | |
| S6-08 | POST /api/v1/productos | 405 | | | |
| S6-09 | `productos-api.html` (Live Server :5500) con backend encendido | Lista los 9 productos (CORS correcto) | | | |
| S6-10 | Misma pagina con backend apagado | Mensaje de error y boton habilitado | | | |
| S7-01 | GET /admin/productos | 9 filas; en "Ver codigo fuente" no hay `th:*` | | | |
| S7-02 | GET /admin/productos?q=ryzen | 1 fila y campo con "ryzen" | | | |
| S7-03 | GET /admin/productos?q=zzz | Estado vacio y "0 producto(s)" | | | |
| S7-04 | GET /admin/productos?categoria=accesorios | 4 filas | | | |
| S7-05 | GET /admin/productos/2 | Detalle de RTX 4060 con S/ 749 | | | |
| S7-06 | GET /admin/productos/999 | Vista 4xx y estado 404 | | | |
| S7-07 | q de 61 caracteres en /admin/productos | 400 | | | |
| S7-08 | GET / | 302 a /admin/productos | | | |
| S7-09 | 320, 768 y 1280 px | Sin desplazamiento horizontal | | | |
| S7-10 | Tab / Shift+Tab y "Saltar al contenido" | Foco visible | | | |
| S7-11 | Zoom 200 % | Contenido legible y operable | | | |

## Creditos
Bootstrap 5.3.8 via CDN. Imagenes de sitios de los fabricantes y tiendas (enlaces remotos).