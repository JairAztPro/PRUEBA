# Preguntas de comprobación | Semana 5

**1. ¿Qué responsabilidad añade Spring Boot al portal construido con Bootstrap?**
Añade un proceso Java de servidor. Spring Boot crea el contexto de la aplicación, autoconfigura Spring MVC, arranca un Tomcat embebido que escucha solicitudes HTTP, entrega los recursos estáticos del classpath, expone el estado con Actuator y permite empaquetar todo en un JAR. El navegador sigue interpretando HTML, CSS y JavaScript; el frontend no cambia, cambia quién lo entrega y lo que podrá hacer el servidor en las semanas siguientes (lógica, datos y respuestas dinámicas).

**2. ¿Por qué Spring Boot no reemplaza a Spring Framework?**
Porque Spring Boot se apoya en Spring Framework: el contenedor IoC, la inyección de dependencias y Spring MVC siguen siendo del Framework. Boot aporta arranque rápido, starters, autoconfiguración, servidor embebido y configuración externa; es decir, configura y organiza el Framework, pero no lo sustituye. Sin Spring Framework, Boot no tendría qué configurar.

**3. ¿Qué tres capacidades concentra `@SpringBootApplication`?**
`@SpringBootConfiguration` (declara la clase como fuente de configuración), `@EnableAutoConfiguration` (activa configuraciones según las dependencias y propiedades presentes) y `@ComponentScan` (busca componentes desde el paquete de la clase principal hacia sus subpaquetes).

**4. ¿Por qué la clase principal debe ubicarse en un paquete raíz?**
Porque `@ComponentScan` parte del paquete de la clase principal y baja por sus subpaquetes. Si la clase está en el paquete por defecto o en un subpaquete lateral, los componentes de otros paquetes no se detectan (o el escaneo abarca más de lo debido). Ubicada en `pe.edu.utp.techlab`, todo lo que esté debajo, como `pe.edu.utp.techlab.bootstrap.StartupReporter`, queda incluido.

**5. ¿Qué diferencia existe entre un starter y una dependencia aislada?**
Un starter es un descriptor que agrupa dependencias coherentes y compatibles para una capacidad (por ejemplo, `spring-boot-starter-webmvc` trae Spring MVC, Tomcat embebido y las bibliotecas necesarias). Una dependencia aislada es una sola biblioteca: habría que buscar, elegir versiones y combinar manualmente todas las demás, con riesgo de incompatibilidades.

**6. ¿Por qué no debe declararse la versión de Spring Framework dentro del POM?**
Porque el `parent` `spring-boot-starter-parent` (con su BOM) ya administra un conjunto de versiones probadas de Spring Framework, Tomcat y dependencias transitivas. Forzar una versión individual puede romper esa compatibilidad y generar errores difíciles de diagnosticar.

**7. ¿Qué problema evita Maven Wrapper en un equipo de estudiantes?**
Evita que cada equipo use una versión distinta de Maven, o no tenga ninguna instalada. El wrapper descarga y usa la versión fijada en `.mvn/wrapper` (3.9.16), de modo que `mvnw test` y `mvnw package` se comportan igual en todos los equipos y no exigen un Maven global.

**8. ¿Por qué `index.html` funciona en la raíz sin un controlador?**
Porque la autoconfiguración de Spring MVC registra un manejador de recursos estáticos para el classpath `static/` y una página de bienvenida: si existe `static/index.html`, la ruta `/` se responde con ese archivo. Por eso no hace falta escribir código Java para servir el portal.

**9. ¿Qué demuestra `contextLoads` y qué no demuestra todavía?**
Demuestra que Spring puede construir el `ApplicationContext`: la configuración es válida, los beans se pueden crear y no hay fallos de arranque. No demuestra que las rutas respondan bien, que la lógica de negocio sea correcta, que el portal se vea o funcione, ni que existan integraciones con datos. Es una prueba de humo; las pruebas funcionales llegarán con los controladores.

**10. ¿Qué diferencia existe entre ejecutar `spring-boot:run` y `java -jar`?**
`spring-boot:run` lo ejecuta Maven desde el proyecto, con las clases compiladas en `target/classes` y el classpath resuelto por Maven; sirve para desarrollo (con DevTools activo) y necesita el código fuente y el wrapper. `java -jar` ejecuta el JAR ya empaquetado, con sus dependencias y el Tomcat embebido dentro, y solo necesita un JDK: es el artefacto que se distribuye y el que demuestra que la aplicación no depende del IDE ni de Maven. Además, tras editar código hay que volver a ejecutar `clean package`, o el JAR seguirá con la versión anterior.

**11. ¿Por qué se expone únicamente `health` en esta práctica?**
Por el principio de mínima exposición. Actuator puede publicar información interna (entorno, beans, configuración, métricas) que no debe quedar accesible sin necesidad. Para verificar que la aplicación está viva basta `health`, y con `show-details=never` la respuesta no revela componentes internos.

**12. ¿Cómo demuestra `StartupReporter` la inversión de control y la inyección por constructor?**
Nadie escribe `new StartupReporter(...)`: al estar anotada con `@Component` y estar bajo el paquete raíz, el contenedor la detecta, la crea y controla su ciclo de vida (inversión de control). La clase declara en su constructor que necesita un `Environment` y Spring se lo entrega (inyección de dependencias por constructor); la clase no lo crea internamente. Además, como `ApplicationRunner`, el contenedor invoca `run` después de crear el contexto. La dependencia explícita facilita probar la clase con un `Environment` alterno.

**13. ¿Qué ventaja aporta cambiar el puerto con un perfil en vez de editar el código?**
Permite usar el mismo JAR y el mismo código fuente en distintos entornos: el valor específico del equipo (8081) vive en `application-local.properties` y se activa con `--spring.profiles.active=local`. No se modifica la configuración base, no hay que recompilar ni reempaquetar, se reduce el riesgo de subir cambios personales al repositorio y volver a 8080 es simplemente ejecutar sin el perfil.

**14. ¿Qué partes del proyecto actual serán reemplazadas o extendidas al incorporar Spring MVC?**
Se extenderán: la aplicación ganará subpaquetes bajo `pe.edu.utp.techlab` con controladores que atiendan rutas y devuelvan respuestas dinámicas; las pruebas incorporarán casos funcionales sobre esas rutas (apoyadas en `spring-boot-starter-webmvc-test`); y `application.properties` podrá recibir nuevos valores de configuración. Se mantienen la clase principal, el paquete raíz, `StartupReporter`, Actuator y el perfil `local`. Del frontend estático, lo que hoy se sirve solo por la página de bienvenida automática pasará gradualmente a ser entregado o complementado por rutas controladas (según lo que defina la semana 6), mientras los recursos CSS, JS e imágenes seguirán en `static`. Si se añade Thymeleaf, las vistas dinámicas irán en `templates`, que hoy se reserva para ese fin.
