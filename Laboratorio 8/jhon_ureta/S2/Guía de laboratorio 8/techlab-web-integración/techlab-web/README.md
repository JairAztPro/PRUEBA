# TechLab Web · APF2 (Semana 8)

Integración Spring Web + Thymeleaf del curso **Marcos de Desarrollo Web**.
Línea técnica: Java 25 · Spring Boot 4.1.1 · Thymeleaf 3.1.5.RELEASE · Bootstrap 5.3.8.
Los cursos siguen en memoria (`CourseService`); no hay MySQL, JPA ni seguridad.

## 1. Cómo ejecutar

```bash
# Linux / macOS / Git Bash
./mvnw clean test
./mvnw clean package
java -jar target/techlab-web-0.0.1-SNAPSHOT.jar

# Windows PowerShell
.\mvnw.cmd clean test
.\mvnw.cmd clean package
java -jar target/techlab-web-0.0.1-SNAPSHOT.jar
```

Abrir <http://localhost:8080>. Con el perfil `local` el puerto es 8081:
`java -jar target/techlab-web-0.0.1-SNAPSHOT.jar --spring.profiles.active=local`

## 2. Estructura

```
src/main/java/pe/edu/utp/techlab/
├── TechlabWebApplication.java
├── bootstrap/StartupReporter.java
├── controller/
│   ├── CourseController.java       (@RestController  /api/v1/cursos)
│   ├── CourseViewController.java   (@Controller      /cursos, /cursos/resumen, /cursos/{id})
│   └── PortalController.java       (/ y /portal -> redirect:/cursos)
├── dto/CourseDto.java
└── service/CourseService.java      (buscar, buscarPorId, cantidad, totalHoras)
src/main/resources/
├── application.properties          (errores sin mensaje ni traza)
├── messages.properties
├── static/css/site.css
└── templates/
    ├── cursos/{lista,detalle,resumen}.html
    ├── error/4xx.html
    └── fragments/layout.html       (head, cabecera, pie)
src/test/java/pe/edu/utp/techlab/
├── controller/{CourseViewControllerTests,CourseControllerTests,WebRoutesTests}.java
└── service/CourseServiceTests.java
```

## 3. Contrato de rutas

| Método y ruta | Representación | Resultado |
|---|---|---|
| GET `/` o `/portal` | Redirección | 302, `Location: /cursos` |
| GET `/cursos` | HTML | 200, tres tarjetas |
| GET `/cursos?q=Spring` | HTML | 200, una tarjeta, `q` conservado |
| GET `/cursos?minHoras=16` | HTML | 200, Bootstrap y Spring Boot |
| GET `/cursos/resumen` | HTML | 200, 3 cursos y 48 horas |
| GET `/cursos/2` | HTML | 200, detalle de Bootstrap |
| GET `/cursos/999` | HTML de error | 404, página `error/4xx` |
| GET `/cursos?q=<61 caracteres>` o `minHoras` inválido | HTML de error | 400 |
| GET `/api/v1/cursos` | JSON | 200, arreglo de tres objetos |
| GET `/api/v1/cursos/999` | Sin cuerpo | 404 |
| GET `/actuator/health` | JSON | 200, `status: UP` |

## 4. Responsabilidades

| Componente | Hace | No hace |
|---|---|---|
| `CourseController` | Expone JSON | Devolver nombres de plantilla |
| `CourseViewController` | Valida parámetros, llena el `Model`, elige la vista (constante) | Crear HTML o acceder a datos |
| `CourseService` | Buscar, localizar, resumir, filtrar por horas | Conocer HTTP o Thymeleaf |
| Plantillas | Presentar atributos del `Model` | Consultar servicios o decidir reglas |
| Fragmentos | Centralizar head, navegación y pie | Duplicar contenido de página |

## 5. Reto: filtro por carga horaria (`minHoras`)

- **Validación:** en `CourseViewController.validarMinHoras`. Recibe `String` para distinguir
  vacío (sin filtro) de inválido; no numérico o fuera de 0..200 lanza `ResponseStatusException(400)`.
- **Filtro:** en `CourseService.buscar(String, Integer)`, que recibe un valor ya validado.
- **Plantilla:** el formulario GET conserva `q` y `minHoras` con `th:value`; el estado vacío usa `curso.vacio`.
- **Pruebas nuevas:** `filtraPorHorasMinimasInclusivo`, `combinaTextoYHorasMinimas` (servicio);
  `filtroMinHorasReduceResultadosYConservaElCampo`, `combinaQYMinHorasYConservaAmbosCriterios`,
  `minHorasSinCoincidenciasMuestraEstadoVacioConOk`, `minHorasInvalidoDevuelve400` (MVC).

| Entrada | Resultado |
|---|---|
| `minHoras=16` | Bootstrap y Spring Boot |
| `q=boot&minHoras=17` | Solo Spring Boot |
| `q=boot&minHoras=16` | Bootstrap y Spring Boot (ver nota) |
| `minHoras=21` | Estado vacío, 200 |
| `minHoras=-1`, `minHoras=abc`, `minHoras=201` | 400 |
| sin `minHoras` o `minHoras=` | Comportamiento original |

**Nota sobre la guía:** el enunciado dice que `q=boot&minHoras=16` devuelve solo Spring Boot,
pero "boot" también está en "Bootstrap" (16 h, y 16 ≥ 16). Con los datos base, que no se pueden
modificar, el resultado correcto son dos cursos. Por eso las pruebas usan `minHoras=17` para
aislar Spring Boot. Por la misma razón `/api/v1/cursos?q=boot` devuelve primero Bootstrap (id 2),
no Spring Boot.

## 6. Cambios respecto a la semana 7

1. `CourseService`: `cantidad()`, `totalHoras()`, sobrecarga `buscar(texto, minHoras)`, texto nulo seguro.
2. `GET /cursos/resumen` + plantilla `resumen.html` + `.metric-card` en `site.css`.
3. `error/4xx.html` movida desde `cursos/4xx.html`: Spring Boot solo busca `templates/error/4xx.html`;
   en la ubicación anterior las pantallas de error no usaban la plantilla.
4. `layout.html`: se corrigió el hash SRI de Bootstrap (decía `sRI14kx…` con el dígito 1; el correcto
   es `sRIl4kx…` con la letra l). Con el hash errado el navegador bloquea el CSS de Bootstrap.
5. `application.properties`: `server.error.include-message=never` y `include-stacktrace=never`.
6. `CourseController.detalle` devuelve `ResponseEntity.notFound()` (404 sin cuerpo).
7. `PortalController`: un solo método para `/` y `/portal`.
8. Enlace "Resumen" en la navegación y textos nuevos en `messages.properties`.

## 7. Matriz funcional APF2 (ejecutar con el JAR)

Registre fecha, navegador, comando y evidencia (la captura debe mostrar URL o comando).

| ID | Acción | Resultado esperado | Estado |
|---|---|---|---|
| F01 | `curl -i http://localhost:8080/` | 302 hacia `/cursos` | □ |
| F02 | Abrir `/cursos` | 200 y tres tarjetas | □ |
| F03 | Buscar `Spring` | Una tarjeta; `q=Spring` visible | □ |
| F04 | Buscar `xyz` | Estado vacío; 200 | □ |
| F05 | Abrir `/cursos/2` | Bootstrap, código 2, 16 horas | □ |
| F06 | Abrir `/cursos/resumen` | 3 cursos y 48 horas | □ |
| F07 | Abrir `/cursos/999` | Página 4xx y estado 404 (Network) | □ |
| F08 | `q` de 61 caracteres | Página 4xx y estado 400 | □ |
| F09 | Abrir `/api/v1/cursos` | JSON con tres objetos | □ |
| F10 | Abrir `/actuator/health` | `status: UP` | □ |
| F11 | Recorrer con teclado | Foco visible y sin trampas | □ |
| F12 | `./mvnw clean test` | BUILD SUCCESS | □ |
| R01 | `/cursos?minHoras=16` | 2 tarjetas, campo conservado | □ |
| R02 | `/cursos?minHoras=21` | Estado vacío; 200 | □ |
| R03 | `/cursos?minHoras=abc` | 4xx; 400 | □ |

Nota: `curl` sin cabecera `Accept: text/html` recibe el error en JSON (sin mensaje ni traza);
el navegador envía `text/html` y recibe la página 4xx. Para verla con curl: `curl -i -H "Accept: text/html" http://localhost:8080/cursos/999`.

### Pruebas de interfaz

| Prueba | Procedimiento | Aprobación | Estado |
|---|---|---|---|
| 320 px | Modo responsivo | Sin desplazamiento horizontal | □ |
| Teclado | Tab / Shift+Tab | Foco visible, orden lógico | □ |
| Saltar contenido | Tab desde el inicio y Enter | El foco llega a `main` | □ |
| Zoom 200 % | Zoom del navegador | Texto y controles operables | □ |
| Sin CDN | Bloquear Bootstrap | Contenido semántico comprensible | □ |
| Consola | Console y Network | Sin errores; `/css/site.css` 200 | □ |

## 8. Preguntas de comprobación

1. **¿Por qué `@Controller` retorna un nombre de vista y `@RestController` datos?**
   `@Controller` pasa el `String` al `ViewResolver`, que localiza la plantilla (`cursos/lista`).
   `@RestController` equivale a `@Controller` + `@ResponseBody`: el valor se serializa con un
   `HttpMessageConverter` (JSON) y se escribe directo en el cuerpo, sin vista.
2. **¿Qué transporta `Model` y cuándo deja de existir?**
   Los atributos que la plantilla necesita (`cursos`, `q`, `minHoras`, `curso`, `cantidadCursos`,
   `totalHoras`). Vive durante una solicitud: se crea al entrar al controlador, se usa al renderizar
   y se descarta al terminar la respuesta. Tras un redirect no sobrevive.
3. **¿Por qué el resumen se calcula en `CourseService`?**
   Es regla de aplicación: se prueba sin Spring (`CourseServiceTests`), se reutiliza desde MVC o REST y la
   plantilla solo presenta. Al pasar a JPA cambia el servicio, no las vistas.
4. **¿`maxlength` en HTML vs. validar en el servidor?**
   `maxlength` (y `min`/`max` del campo numérico) mejora la experiencia pero el cliente controla ese HTML:
   se puede editar, saltar con la URL o con `curl`. El servidor es la frontera de confianza y devuelve 400.
5. **¿Cómo demuestra Network que `/cursos/999` conserva un 404?**
   En DevTools > Network, la fila de `999` muestra Status 404 aunque el navegador renderiza la página
   `error/4xx`; con `curl -i` aparece `HTTP/1.1 404`. La página amable no convierte el error en 200.
6. **¿Qué cambio permitiría sustituir la lista en memoria por JPA con menor impacto?**
   Que `CourseService` obtenga los datos de un repositorio en lugar de `List.of`, manteniendo sus
   métodos públicos y el `CourseDto`. Controladores, plantillas y pruebas MVC no cambian.

## 9. Defensa oral (reto)

- **¿Dónde valida?** En `CourseViewController` (`normalizar` y `validarMinHoras`); el servicio recibe
  valores ya validados y no conoce `HttpStatus`.
- **¿Por qué GET?** Consultar es seguro e idempotente, no cambia estado y deja los criterios en una URL compartible
  (`/cursos?q=boot&minHoras=17`).
- **¿Qué capa filtra?** `CourseService.buscar(String, Integer)`. La plantilla no filtra ni calcula.
- **¿Qué prueba falla si la plantilla deja de conservar el criterio?**
  `filtroMinHorasReduceResultadosYConservaElCampo` y `combinaQYMinHorasYConservaAmbosCriterios`
  (comprueban `value="16"`, `value="boot"` en el HTML) y `filtroConservaConsultaYReduceResultados` (`q`).

## 10. Reflexión individual (borrador: ajústelo con su experiencia)

- **Decisión que mejoró la mantenibilidad:** concentrar búsqueda, resumen y filtro en `CourseService.java`:
  MVC y REST lo comparten y la regla vive en un solo lugar; la plantilla no recalcula nada.
- **Fallo encontrado:** la plantilla de errores estaba en `templates/cursos/4xx.html`, y Spring Boot solo
  la toma desde `templates/error/`. La evidencia fue ver en Network el error genérico en lugar de la página 4xx.
  Se movió a `templates/error/4xx.html`. Además, el hash SRI de Bootstrap tenía `1` en vez de `l`, lo que
  bloquea el CSS en el navegador; se corrigió comparándolo con la guía.
- **Preparación para la semana 9 (sin implementarla):** `CourseService` ya es la única puerta a los datos y
  `CourseDto` es independiente de HTTP y de la vista; solo faltaría cambiar la fuente interna del servicio.
