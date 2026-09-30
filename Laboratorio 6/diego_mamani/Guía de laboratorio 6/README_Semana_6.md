# TechLab Web · Semana 6

Spring Web y arquitectura MVC. Se incorpora al proyecto de la semana 5 una API REST de consulta de cursos, con separación entre transporte HTTP (controlador) y lógica de aplicación (servicio).

## Descripción
- `CourseDto` (record con `id`, `titulo`, `horas`) y `CourseService` con datos inmutables en memoria (`buscar`, `buscarPorId`).
- `CourseController` (`@RestController`) en `/api/v1/cursos`, con inyección por constructor.
- `PortalController` con redirecciones internas fijas.
- Catálogo estático `catalogo.html` + `js/catalogo.js`, que consume la API con `fetch` y muestra el texto con `textContent` (sin insertar HTML recibido).

## Requisitos
JDK 25 y conexión inicial para Maven. Se usa el wrapper (`mvnw`).

## Ejecución
```bash
# Windows PowerShell
.\mvnw.cmd spring-boot:run
# macOS / Linux
./mvnw spring-boot:run
```

## Rutas y resultados esperados
| Solicitud | Resultado |
|---|---|
| `GET /api/v1/cursos` | 200, tres cursos |
| `GET /api/v1/cursos?q=Spring` | 200, un curso (id 3) |
| `GET /api/v1/cursos?q=ZZZ` | 200, `[]` |
| `GET /api/v1/cursos/2` | 200, Bootstrap |
| `GET /api/v1/cursos/999` | 404 |
| `GET /api/v1/cursos/abc` | 400 |
| `GET /api/v1/cursos` con `q` de 61 caracteres | 400 |
| `POST /api/v1/cursos` | 405 |
| `GET /portal` | 302 (en esta semana, a `/catalogo.html`) |

## Pruebas
```bash
./mvnw clean test
```
- `CourseServiceTests` (3): consulta vacía, normalización de espacios y mayúsculas, ID desconocido.
- `WebRoutesTests` (3, con MockMvc): listado y filtro, detalle y errores, método no permitido y redirección.

## Empaquetado
```bash
./mvnw clean package
java -jar target/techlab-web-0.0.1-SNAPSHOT.jar
```

## Alcance y seguridad
Solo consultas GET; sin base de datos ni autenticación. No se añadió CORS (HTML y API comparten origen). Se evita `innerHTML` para datos recibidos y las redirecciones son fijas (sin destino controlado por el usuario). Solo `health` expuesto en Actuator.

## Evidencias
Matriz de pruebas 01 a 14, resultados reales y capturas (incluida la pestaña Network) en `evidencias/`.

## Créditos
Autoría: _(completar)_. Bootstrap 5.3.8 vía CDN (licencia MIT).


## PREGUNTAS DE REFLEXIÓN

1. ¿Qué responsabilidad cumple DispatcherServlet antes del controlador?
Es el controlador frontal: recibe las solicitudes que entrega Tomcat, consulta los mapeos para hallar el método adecuado, hace que los resolutores de argumentos conviertan los parámetros y recién entonces invoca al controlador; después procesa su resultado.

2. ¿Por qué un JSON servido por Spring MVC no es una plantilla Thymeleaf?
Porque en @RestController el objeto lo serializa un HttpMessageConverter directo al cuerpo y es JavaScript en el cliente quien lo presenta. Thymeleaf es un motor de plantillas que, vía ViewResolver, genera HTML en el servidor.

3. ¿Qué diferencia existe entre Model, DTO y entidad de persistencia?
Model: contenedor de atributos que la vista puede usar durante la solicitud. DTO: objeto inmutable que transporta datos (CourseDto). Entidad de persistencia: clase mapeada a una tabla (@Entity). Ninguno es sinónimo de los otros.

4. ¿Cómo cambia el significado de String entre Controller y RestController?
En @Controller, un String es el nombre lógico de una vista (o una redirección con redirect:). En @RestController, por incluir @ResponseBody, se escribe como cuerpo de la respuesta.

5. ¿Cuándo conviene RequestParam y cuándo PathVariable?
PathVariable identifica un recurso concreto dentro de la ruta (/api/v1/cursos/2). RequestParam sirve para criterios opcionales de consulta o filtro (?q=Spring).

6. ¿Por qué una búsqueda sin coincidencias responde 200 y no 404?
Porque la consulta es válida y el recurso colección existe; simplemente está vacío, y la respuesta correcta es una lista vacía. El 404 se reserva para un recurso identificado que no existe (por ejemplo, el ID 999).

7. ¿Qué produce 400 antes de ejecutar detalle y qué caso decide el controlador?
El 400 antes de ejecutar el método lo produce Spring al no poder convertir abc a long. El controlador decide el 400 cuando q supera los 60 caracteres (y el 404 cuando no encuentra el ID).

8. ¿Por qué fetch requiere revisar ok además de usar catch?
Porque fetch solo rechaza la promesa ante fallos de red; un 404 o 500 se resuelve normalmente. Por eso se revisa respuesta.ok antes de leer el JSON, y el catch cubre los fallos de red y los errores lanzados.

9. ¿Qué demuestra MockMvc y qué debe comprobar el navegador?
MockMvc demuestra rutas, estados HTTP, JSON y redirecciones sin abrir un puerto. El navegador debe comprobar presentación, accesibilidad, fetch, recursos estáticos y el funcionamiento del JAR en Tomcat.

10. ¿Cómo reutilizará CourseService al incorporar vistas dinámicas?
Lo inyectará por constructor en un nuevo @Controller (CourseViewController), que pondrá los resultados en el Model y devolverá un nombre de vista. Así la API y las vistas usan la misma fuente de datos sin duplicar la lista.
