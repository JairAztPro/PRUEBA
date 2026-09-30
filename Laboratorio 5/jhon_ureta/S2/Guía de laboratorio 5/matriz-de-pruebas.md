# Matriz de pruebas | Semana 5

Proyecto: TechLab Web (Spring Boot 4.1.1, Java 25, Maven).
Estudiante: _nombre_ | Fecha de ejecución: _dd/mm/aaaa_ | Sistema operativo: _Windows / macOS / Linux_

Instrucciones: ejecutar cada caso en orden, copiar el resultado real observado (no el esperado), marcar Pasa o Falla y, si falla, anotar la causa real y la corrección aplicada. Reiniciar o "volver a intentar" no cuenta como corrección.

## 1. Casos obligatorios

| ID | Procedimiento | Resultado esperado | Resultado real | Estado | Corrección aplicada | Evidencia |
|---|---|---|---|---|---|---|
| CP-01 | Ejecutar `java --version`. | JDK activo 25. |  | Pendiente |  | 01, 02 |
| CP-02 | Ejecutar el proyecto base con Maven Wrapper (`mvnw spring-boot:run`). | Inicia y Tomcat escucha en 8080. |  | Pendiente |  | 04 |
| CP-03 | Abrir la raíz `http://localhost:8080/` en el navegador. | HTTP 200; `index.html` lleva a `html/inicio.html` y el portal ChipZone queda visible. |  | Pendiente |  | 05 |
| CP-04 | Revisar CSS, JS, imágenes y CDN en DevTools Network. | Sin 404 ni solicitudes bloqueadas esenciales. |  | Pendiente |  | 06 |
| CP-05 | Repetir los casos esenciales del APF1 (ver sección 2.1). | El APF1 conserva su comportamiento. |  | Pendiente |  | 07 |
| CP-06 | Consultar `/actuator/health`. | HTTP 200 y `status` UP, sin detalles internos. |  | Pendiente |  | 08 |
| CP-07 | Revisar el log después del inicio. | `StartupReporter` informa nombre y puerto. |  | Pendiente |  | 09 |
| CP-08 | Ejecutar `mvnw test`. | BUILD SUCCESS y cero fallos. |  | Pendiente |  | 10 |
| CP-09 | Ejecutar `mvnw clean package` y luego `java -jar target/techlab-web-0.0.1-SNAPSHOT.jar`. | El JAR inicia y sirve el portal. |  | Pendiente |  | 11, 12 |
| CP-10 | Activar el perfil local (`--spring.profiles.active=local`). | El mismo JAR escucha en 8081. |  | Pendiente |  | 13 |
| CP-11 | Ejecutar el JAR sin perfil después del reto. | La aplicación vuelve a 8080. |  | Pendiente |  | 12 |
| CP-12 | Revisar `git status`. | No hay `target`, secretos ni archivos del IDE versionados. |  | Pendiente |  | 14 |

## 2. Guía de qué observar en cada caso

| ID | Qué anotar en "Resultado real" |
|---|---|
| CP-01 | Primera línea de `java --version` y valor de `JAVA_HOME`. |
| CP-02 | Línea del log con el puerto (`Tomcat started on port 8080`). |
| CP-03 | Código de estado de `/` y de `/html/inicio.html` en Network, y título de la página (ChipZone \| Inicio). |
| CP-04 | Número de solicitudes locales (`html`, `css`, `js`) con estado distinto de 200 o 304 (debe ser 0). Anotar aparte las imágenes o CDN externos que fallen o se bloqueen, con su URL. |
| CP-05 | Resultado de cada comprobación de la sección 2.1. |
| CP-06 | Código HTTP y cuerpo exacto de la respuesta. |
| CP-07 | Texto completo de la línea de log de `StartupReporter`. |
| CP-08 | Resumen de Maven: `Tests run`, `Failures`, `Errors` y `BUILD SUCCESS`. |
| CP-09 | Tamaño del JAR y confirmación de que se inició desde una terminal sin el IDE. |
| CP-10 | Puerto del log y respuestas de `/` y `/actuator/health` en 8081. |
| CP-11 | Puerto del log al ejecutar sin el argumento de perfil. |
| CP-12 | Salida de `git status` y confirmación de que `target/` está en `.gitignore`. |

### 2.1 Comprobaciones de CP-05 (repetir en Spring Boot)

| Página | Comprobación | Resultado |
|---|---|---|
| Todas | Menú colapsable en 360 px y menú desplegable Categorías. |  |
| Todas | Navegación con teclado (Tab, Enter, enlace "Saltar al contenido"). |  |
| Inicio | Búsqueda: lleva a `catalogo.html?buscar=...` y filtra. |  |
| Inicio | Pestañas de productos destacados y botón de favoritos. |  |
| Inicio | Validación del formulario de newsletter. |  |
| Inicio | Ícono de cuenta abre `mi_cuenta.html` (corregido respecto al APF1). |  |
| Catálogo | Filtros por categoría desde el menú, precio, stock, orden y "limpiar". |  |
| Catálogo | Modal o detalle de producto, si aplica. |  |
| Carrito | Botones + y − cambian la cantidad de a 1 y recalculan el total. |  |
| Carrito | Botones de complementos (upsell) y pasos de envío y pago. |  |
| Carrito | Finalizar compra muestra un solo mensaje de confirmación. |  |
| Mi cuenta | Pestañas y validación del formulario de perfil. |  |

## 3. Criterios de salida de la semana

- [ ] Java 25 es el JDK activo en la terminal y en Visual Studio Code.
- [ ] Proyecto generado con Spring Boot 4.1.1, Java 25, Maven y empaquetado JAR.
- [ ] Dependencias web con `spring-boot-starter-webmvc`; sin versión manual de Spring Framework ni Tomcat.
- [ ] Clase principal en `pe.edu.utp.techlab` y aplicación iniciando sin errores.
- [ ] Portal del APF1 servido desde `http://localhost:8080/` conservando recursos y comportamiento (más las dos correcciones documentadas en el README).
- [ ] `/actuator/health` responde HTTP 200 con `status` UP.
- [ ] Prueba de contexto exitosa y JAR ejecutable generado.
- [ ] Repositorio sin secretos, archivos generados ni rutas absolutas del equipo.

## 4. Diagnóstico de fallos (completar solo si hubo alguno)

| ID | Primer mensaje de error completo | Primera sección "Caused by" | Clasificación (entorno, dependencias, compilación, arranque, HTTP, recurso estático) | Causa real | Corrección |
|---|---|---|---|---|---|
|  |  |  |  |  |  |
