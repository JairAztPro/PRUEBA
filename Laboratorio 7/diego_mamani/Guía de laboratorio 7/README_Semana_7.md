# TechLab Web · Semana 7

Thymeleaf: expresiones, atributos y plantillas. El catálogo pasa a generarse en el servidor con Spring MVC y Thymeleaf, reutilizando el mismo `CourseService` que alimenta la API REST.

## Descripción
- `CourseViewController` (`@Controller`, `/cursos`): lista con búsqueda por `GET ?q=` (máximo 60 caracteres, si no 400) y detalle `/cursos/{id}` (404 si no existe). Nombres de vista fijos.
- `PortalController`: `/` y `/portal` redirigen (302) a `/cursos`.
- Plantillas: `fragments/layout.html` (head, cabecera, pie), `cursos/lista.html`, `cursos/detalle.html` y la vista de error 4xx.
- Textos externalizados en `messages.properties`; estilos en `static/css/site.css`.
- Se conserva `CourseController` y la API REST `/api/v1/cursos`.

## Requisitos
JDK 25, Spring Boot 4.1.1 (Thymeleaf lo administra Spring Boot) y conexión inicial para Maven.

## Ejecución
```bash
# Windows PowerShell
.\mvnw.cmd spring-boot:run
# macOS / Linux
./mvnw spring-boot:run
```

## Rutas
| Ruta | Resultado |
|---|---|
| `/cursos` | 200, tres tarjetas |
| `/cursos?q=Spring` | 200, una tarjeta y el campo conserva `Spring` |
| `/cursos?q=NoExiste` | 200, estado vacío |
| `/cursos/2` | 200, detalle de Bootstrap (16 horas) |
| `/cursos/999` | 404 |
| `q` de 61 caracteres | 400 |
| `/api/v1/cursos` | 200, JSON de tres elementos |
| `/portal` | 302 a `/cursos` |

## Pruebas
```bash
./mvnw clean test
```
`CourseViewControllerTests` (5, MockMvc): nombre de vista, atributos del Model, contenido renderizado, estados 404/400 y redirección. Suite completa del proyecto: 12 pruebas, 0 fallos, 0 errores, 0 omitidas (JDK 25.0.4). MockMvc no sustituye las pruebas manuales de navegador.

## Empaquetado
```bash
./mvnw clean package
java -jar target/techlab-web-0.0.1-SNAPSHOT.jar
```

## Seguridad y accesibilidad
- `th:text` escapa el HTML; no se usa `th:utext` con datos del usuario.
- El nombre de la plantilla nunca se construye con parámetros de la solicitud.
- Enlace "Saltar al contenido", `label` asociado al campo `q`, foco visible y CSS externo.
- Bootstrap con versión fija, integridad SRI y `crossorigin`.

## Alcance
Sin base de datos, formularios de escritura, autenticación ni autorización (semanas posteriores). En esta versión `/` redirige a `/cursos`; el `index.html` del APF1 sigue en `static/` y se accede por su ruta directa.

## Evidencias
Matriz M1 a M8, pruebas de 320/768/1280 px, teclado y zoom, y capturas en `evidencias/`.

## Créditos
Autoría: _(completar)_. Bootstrap 5.3.8 vía CDN (licencia MIT).


## PREGUNTAS DE REFLEXIÓN

1. Explique por qué devolver cursos/lista desde @Controller no escribe ese texto en la respuesta.
Porque en @Controller el String se interpreta como nombre lógico de vista. El ViewResolver localiza templates/cursos/lista.html, Thymeleaf la procesa con el Model y lo que se envía es el HTML resultante. Sin @ResponseBody, el texto no va al cuerpo.

2. ¿Qué diferencia existe entre ${curso.titulo} y *{titulo}?
${...} evalúa una variable del contexto (aquí, el atributo curso del Model). *{...} evalúa una propiedad del objeto seleccionado con th:object, por lo que dentro de th:object="${curso}" ambas dan el mismo resultado, pero *{} evita repetir el nombre del objeto.

3. ¿Por qué @{} es preferible a concatenar rutas manualmente?
Porque construye URLs conscientes del contexto de despliegue y codifica correctamente variables de ruta y parámetros. Concatenar a mano se rompe con prefijos de contexto y caracteres especiales.

4. ¿Qué problema de mantenimiento resuelve un fragmento?
La duplicación: cabecera, pie y head viven en un solo archivo (layout.html). Si cambia una ruta o el nombre de la aplicación, se modifica en un lugar y no en cada plantilla.

5. ¿Por qué la búsqueda utiliza GET y conserva q en la URL?
Porque consultar no cambia estado. Con GET la URL es compartible, guardable y recargable, y th:value devuelve el criterio al campo después de buscar.

6. ¿Qué riesgo introduce th:utext cuando recibe contenido no confiable?
th:utext no escapa el HTML, así que interpretaría el marcado enviado por el usuario y abriría la puerta a XSS (ejecución de scripts inyectados). th:text escapa por defecto.

7. ¿Qué demuestra MockMvc y qué debe verificarse todavía en un navegador?
MockMvc demuestra rutas, nombre de vista, atributos del Model, contenido renderizado, estados de error y redirecciones sin abrir un puerto. En el navegador falta verificar teclado y foco, diseño responsivo, zoom, recursos CSS/CDN y que no queden expresiones th:* sin procesar.

8. ¿Por qué el origen de datos no debe consultarse desde una plantilla?
Porque la plantilla debe solo presentar. Consultar datos o decidir reglas ahí mezcla responsabilidades, dificulta las pruebas y obligaría a modificar las vistas cuando el origen cambie (por ejemplo, al pasar a persistencia en la semana 9). La consulta pertenece al servicio.
