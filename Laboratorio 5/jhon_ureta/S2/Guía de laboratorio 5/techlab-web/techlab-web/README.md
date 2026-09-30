# TechLab Web

Aplicación Spring Boot que sirve el frontend del APF1 (tienda ChipZone: HTML, CSS, JavaScript y Bootstrap) desde un servidor Tomcat embebido. Corresponde al laboratorio de la semana 5 del curso *Marcos de Desarrollo Web*: el frontend de la semana 4 deja de abrirse con Live Server y pasa a ejecutarse como una aplicación Java empaquetada en un JAR.

## Requisitos

- JDK 25 completo (no solo JRE), con `JAVA_HOME` apuntando a la raíz del JDK y su carpeta `bin` en el `PATH`.
- Conexión a internet durante la primera ejecución, para que Maven descargue el wrapper y las dependencias. El portal también carga desde internet Bootstrap, Bootstrap Icons y las imágenes de producto.
- No hace falta instalar Maven ni Tomcat: se usa el Maven Wrapper del proyecto y el servidor embebido de Spring Boot.

Versiones usadas: Spring Boot 4.1.1, Java 25, Maven (wrapper), empaquetado JAR. Spring Framework y Tomcat los administra Spring Boot; no se fijan en el `pom.xml`.

## Ejecución

Desde la carpeta que contiene `pom.xml`:

```bash
# Windows PowerShell
.\mvnw.cmd spring-boot:run

# macOS o Linux
./mvnw spring-boot:run
```

Luego abrir:

- Portal: <http://localhost:8080/> (`index.html` lleva a `html/inicio.html`)
- Estado de la aplicación: <http://localhost:8080/actuator/health> (responde HTTP 200 con `{"status":"UP"}`)

Al iniciar, el log muestra `techlab-web disponible en http://localhost:8080` (componente `StartupReporter`).

## Pruebas

```bash
# Windows PowerShell
.\mvnw.cmd test

# macOS o Linux
./mvnw test
```

La prueba `contextLoads` verifica que Spring puede crear el `ApplicationContext`. Es una prueba de humo: no reemplaza las pruebas funcionales de las próximas semanas.

Último resultado: _completar con la fecha y el resumen de Maven (`BUILD SUCCESS`, fallos 0) después de ejecutar el comando._

## Empaquetado

```bash
# Windows PowerShell
.\mvnw.cmd clean package

# macOS o Linux
./mvnw clean package

# Todos los sistemas
java -jar target/techlab-web-0.0.1-SNAPSHOT.jar
```

El artefacto generado es `target/techlab-web-0.0.1-SNAPSHOT.jar`. La carpeta `target` no se versiona.

## Perfiles

| Perfil | Puerto | Cómo se activa |
|---|---|---|
| Base (sin perfil) | 8080 | Es el valor por defecto de `application.properties`. |
| `local` | 8081 | Argumento del JAR: `--spring.profiles.active=local` |

`local` es un perfil de desarrollo que solo cambia el puerto; no contiene secretos. El mismo JAR se ejecuta con y sin perfil:

```bash
# macOS o Linux
java -jar target/techlab-web-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=local

# Windows PowerShell
java -jar target/techlab-web-0.0.1-SNAPSHOT.jar `
  --spring.profiles.active=local
```

## Alcance

Esta versión sirve únicamente el frontend estático desde `src/main/resources/static`. No incluye controladores, base de datos, seguridad ni plantillas Thymeleaf; esos elementos se incorporarán en semanas posteriores. Actuator solo expone `health`, sin detalles internos.

## Estructura

```text
techlab-web/
├── pom.xml
├── mvnw, mvnw.cmd, .mvn/            Maven Wrapper
├── src/main/java/pe/edu/utp/techlab/
│   ├── TechLabWebApplication.java   Clase principal (paquete raíz)
│   └── bootstrap/StartupReporter.java
├── src/main/resources/
│   ├── application.properties
│   ├── application-local.properties
│   └── static/
│       ├── index.html               Página de bienvenida: lleva a html/inicio.html
│       ├── html/                    inicio, catalogo, carrito y mi_cuenta (APF1)
│       ├── css/styles.css
│       └── js/                      carrito, catalogo, cuenta, inicio y productos
├── src/test/java/pe/edu/utp/techlab/TechLabWebApplicationTests.java
└── evidencias/                      Matriz de pruebas, respuestas y capturas
```

El APF1 organiza sus páginas en `html/` y las enlaza con rutas relativas (`../css/styles.css`, `../js/...`, `catalogo.html`). Se conservó esa estructura para no modificar los enlaces ni las redirecciones de los scripts; por eso `static/index.html` solo redirige a `html/inicio.html`.

## Cambios respecto al APF1

Los archivos CSS y JavaScript son idénticos a los del APF1. En el HTML solo se hicieron dos correcciones:

1. `html/inicio.html`: el ícono de cuenta enlazaba a `mi-cuenta.html` (con guion), archivo que no existe; ahora apunta a `mi_cuenta.html`.
2. `html/carrito.html`: el final de la página cargaba dos veces `carrito.js` y dos versiones de Bootstrap (5.3.8 y 5.3.3). Se eliminó la carga duplicada y se conservó la de Bootstrap 5.3.3 junto con una sola carga de `carrito.js`, para que cada clic en +/− y en Finalizar compra se ejecute una vez.

## Evidencias

Se guardan en la carpeta `evidencias/` (en la raíz del proyecto, nunca dentro de `static`):

- `matriz-de-pruebas.md`: casos CP-01 a CP-12 con resultado real y estado.
- `preguntas-de-comprobacion.md`: respuestas a las preguntas de comprobación.
- `capturas/`: versión de Java y runtime del IDE, log de inicio, portal en `localhost:8080`, `/actuator/health`, `BUILD SUCCESS` de `test` y `clean package`, y ejecución del JAR (puertos 8080 y 8081).

## Créditos

- Autoría: _nombre del estudiante y sección_.
- Frontend: tienda ChipZone del APF1.
- Bootstrap 5.3 (licencia MIT) y Bootstrap Icons (licencia MIT), cargados desde el CDN de jsDelivr.
- Imágenes de producto: el APF1 las enlaza directamente desde sitios externos (Unsplash, ASUS, Falabella, Mercado Libre, Infotec y miniaturas de Google). _Completar con la autoría o licencia de cada imagen, o reemplazarlas por archivos propios en `static/assets/img`._
- Spring Boot y Spring Framework (licencia Apache 2.0).
