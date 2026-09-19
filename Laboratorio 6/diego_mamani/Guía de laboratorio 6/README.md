# demoapp | Semana 6

Práctica de *Marcos de Desarrollo Web*: páginas dinámicas con Spring Boot y Thymeleaf. Un formulario recibe el nombre del alumno y una página de resultado le da la bienvenida.

Paquete base: `com.sem6.demoapp`. Versiones: Spring Boot 4.1.1, Java 25, Maven.

## Rutas

| Método | Ruta | Vista | Qué muestra |
|---|---|---|---|
| GET | `/home` | `home.html` | "Bienvenido, Carlos!" (el nombre lo envía el controlador) |
| GET | `/formulario` | `formulario.html` | Formulario con el campo nombre |
| POST | `/procesar` | `resultado.html` | "Bienvenido, <nombre ingresado>!" |

## Estructura

```text
demoapp/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/sem6/demoapp/
    │   │   ├── DemoApplication.java
    │   │   ├── controller/HomeController.java
    │   │   └── model/Usuario.java
    │   └── resources/
    │       ├── templates/   home.html, formulario.html, resultado.html
    │       └── static/      CSS, JS e imágenes (vacía por ahora)
    └── test/java/com/sem6/demoapp/DemoApplicationTests.java
```

## Ejecución

Desde la carpeta que contiene `pom.xml`:

```bash
mvn spring-boot:run
```

Si el proyecto tiene Maven Wrapper (`mvnw`), usa `./mvnw spring-boot:run` (macOS o Linux) o `.\mvnw.cmd spring-boot:run` (Windows).

Luego abre:

- <http://localhost:8080/home>
- <http://localhost:8080/formulario> → escribe tu nombre y pulsa Enviar; verás "Bienvenido, <tu nombre>!" en `/procesar`.

## Pruebas

```bash
mvn test
```

`contextLoads` comprueba que la aplicación arranca con sus controladores y plantillas registrados.
