# TechLab Web · Semana 5

Primer proyecto web con Spring Boot del curso **Marcos de Desarrollo Web**. Migra el portal estático aprobado en el APF1 a una aplicación Java ejecutable, que sirve el frontend con un Tomcat embebido.

## Descripción
- Proyecto generado con Spring Initializr: Spring Boot 4.1.1, Java 25, Maven, empaquetado JAR, paquete raíz `pe.edu.utp.techlab`.
- El frontend del APF1 (`index.html`, `css/`, `js/`, `html/`) se sirve desde `src/main/resources/static`.
- `StartupReporter` (paquete `bootstrap`) registra en el log el nombre y el puerto al iniciar.

## Requisitos
- JDK 25 (LTS) activo en terminal e IDE: `java --version`.
- Conexión a internet en la primera ejecución (dependencias de Maven).
- No se requiere Maven ni Tomcat instalados: se usa el wrapper (`mvnw`).

## Ejecución
```bash
# Windows PowerShell
.\mvnw.cmd spring-boot:run
# macOS / Linux
./mvnw spring-boot:run
```
Abrir `http://localhost:8080/` y `http://localhost:8080/actuator/health` (debe responder `{"status":"UP"}`).

## Pruebas
```bash
./mvnw clean test
```
Prueba de esta semana: `contextLoads` (comprueba que el contexto de Spring se crea).

## Empaquetado
```bash
./mvnw clean package
java -jar target/techlab-web-0.0.1-SNAPSHOT.jar
```

## Perfiles
- Base: puerto **8080** (`application.properties`).
- `local`: puerto **8081** (`application-local.properties`), solo desarrollo, sin secretos.
```bash
java -jar target/techlab-web-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```
Sin el argumento, la aplicación vuelve a 8080.

## Alcance
Frontend estático servido por Spring Boot. No incluye controladores, base de datos, seguridad ni Thymeleaf. Solo se expone el endpoint `health` de Actuator, sin detalles internos.

## Evidencias
Matriz de pruebas CP-01 a CP-12 y capturas en `evidencias/` (nunca dentro de `static/`).

## Créditos
Autoría: _(completar)_. Bootstrap 5.3.8 y Bootstrap Icons 1.13.1 vía CDN (licencia MIT). Imágenes del APF1: _(indicar origen y licencia)_.


## PREGUNTAS DE REFLEXIÓN 

1. ¿Qué responsabilidad añade Spring Boot al portal construido con Bootstrap?
Añade un proceso Java de servidor: un Tomcat embebido que atiende HTTP, el contexto de Spring con autoconfiguración, configuración externa, monitoreo básico (Actuator), pruebas y empaquetado en un JAR. El navegador sigue interpretando HTML, CSS y JavaScript; Java no reemplaza el frontend.

2. ¿Por qué Spring Boot no reemplaza a Spring Framework?
Porque Spring Boot no es un framework alternativo: configura y organiza Spring Framework (contenedor IoC, inyección de dependencias, Spring MVC). Sin Spring Framework, Boot no tiene nada que arrancar ni autoconfigurar.

3. ¿Qué tres capacidades concentra @SpringBootApplication?
@SpringBootConfiguration (declara la clase como fuente de configuración), @EnableAutoConfiguration (activa configuraciones según las dependencias y propiedades presentes) y @ComponentScan (busca componentes desde el paquete de la clase principal).

4. ¿Por qué la clase principal debe ubicarse en un paquete raíz?
Porque @ComponentScan explora desde su paquete hacia los subpaquetes. Si estuviera en el paquete por defecto o en un subpaquete lateral, Spring no encontraría los controladores, servicios y componentes que están fuera de esa rama.

5. ¿Qué diferencia existe entre un starter y una dependencia aislada?
Un starter es un descriptor que agrupa varias dependencias coherentes y con versiones compatibles para una capacidad (por ejemplo, spring-boot-starter-webmvc trae Spring MVC, Tomcat y Jackson). Una dependencia aislada es una sola biblioteca cuya versión y compatibilidad debe resolver el desarrollador.

6. ¿Por qué no debe declararse la versión de Spring Framework dentro del POM?
Porque el parent de Spring Boot administra un conjunto de versiones probadas en conjunto. Forzar una versión individual puede causar incompatibilidades difíciles de diagnosticar.

7. ¿Qué problema evita Maven Wrapper en un equipo de estudiantes?
Evita que cada persona tenga que instalar una versión de Maven distinta. El wrapper descarga y usa la versión fijada por el proyecto, por lo que el build es reproducible en cualquier equipo.

8. ¿Por qué index.html funciona en la raíz sin un controlador?
Porque Spring Boot sirve automáticamente los recursos del classpath static y usa static/index.html como página de bienvenida para la ruta /.

9. ¿Qué demuestra contextLoads y qué no demuestra todavía?
Demuestra que el ApplicationContext se crea sin errores: la configuración es válida y los beans pueden construirse. No demuestra comportamiento funcional: rutas, respuestas, interfaz, recursos estáticos ni accesibilidad.

10. ¿Qué diferencia existe entre ejecutar spring-boot:run y java -jar?
spring-boot:run arranca la aplicación desde Maven usando el proyecto y su classpath de desarrollo. java -jar ejecuta el artefacto empaquetado, independiente de Maven y del IDE. Solo lo segundo demuestra que el producto funciona fuera del entorno de desarrollo.

11. ¿Por qué se expone únicamente health en esta práctica?
Por el principio de mínima exposición: los demás endpoints de Actuator pueden revelar información interna. Con exposure.include=health y show-details=never solo se informa el estado UP/DOWN.

12. ¿Cómo demuestra StartupReporter la inversión de control y la inyección por constructor?
StartupReporter es un @Component que nadie instancia manualmente: el contenedor lo detecta con el escaneo, lo crea y le entrega el Environment por el constructor. La clase no crea su dependencia; la recibe (IoC).

13. ¿Qué ventaja aporta cambiar el puerto con un perfil en vez de editar el código?
El mismo JAR y el mismo código sirven para distintos entornos: solo cambia la configuración externa (application-local.properties, activada con --spring.profiles.active=local). Se evita mantener valores del equipo en el código o alterar la configuración base.

14. ¿Qué partes del proyecto actual serán reemplazadas o extendidas al incorporar Spring MVC?
Se extienden: se agregan controladores, servicios y DTOs bajo pe.edu.utp.techlab, y nuevas dependencias y rutas. La raíz deja de depender solo de la página de bienvenida automática cuando un controlador atiende /. Se conservan la clase principal, la configuración y el frontend estático.