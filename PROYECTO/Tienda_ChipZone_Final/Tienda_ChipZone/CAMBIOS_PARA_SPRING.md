# Semana 13 - Spring Security: cambios y añadidos

Todas las rutas son relativas a `Backend/`. Paquete base: `src/main/java/pe/edu/utp/techlab/`.

## 1. Archivos nuevos

| Archivo | Para qué sirve |
|---|---|
| `config/SecurityConfig.java` | Núcleo de la semana. `PasswordEncoder` (BCrypt), `UserDetailsService` en memoria y dos `SecurityFilterChain`: `apiSecurity` (`@Order(1)`, `/api/**`) y `webSecurity` (resto) |
| `controller/LoginController.java` | `GET /login` devuelve la vista; el `POST /login` lo procesa Spring Security |
| `controller/PerfilController.java` | Reto: `GET /perfil` ("Mi sesión"), ruta privada |
| `web/SecurityView.java` | Record con `authenticated` y `username` para las vistas |
| `web/GlobalViewModel.java` | `@ControllerAdvice` que publica el modelo `security` en todas las plantillas |
| `src/main/resources/templates/auth/login.html` | Formulario de inicio de sesión (token CSRF, mensajes de error y logout) |
| `src/main/resources/templates/auth/perfil.html` | Vista "Mi sesión": solo el nombre autenticado |
| `src/test/java/.../security/WebSecurityTests.java` | Pruebas del panel: público/privado, redirección a login, POST con y sin CSRF, logout, `/perfil` |
| `src/test/java/.../security/ApiSecurityTests.java` | Pruebas de la API: lectura pública, 401 sin Basic o con clave incorrecta, 201/204 con Basic, sin `JSESSIONID` |
| `src/test/resources/config/application.properties` | Aporta la clave temporal solo a las pruebas (no sustituye al `application.properties` principal) |
| `CAMBIOS_SEMANA13.md` | Este documento |

## 2. Archivos modificados

| Archivo | Cambio |
|---|---|
| `pom.xml` | Parent de Spring Boot 4.1.1 → 4.1.2; dependencias `spring-boot-starter-security` y `spring-security-test` (test) |
| `src/main/resources/application.properties` | Al final: `app.security.demo-username` (por defecto `docente`), `app.security.demo-password` (obligatoria, sin valor por defecto), cookie de sesión `http-only`, `same-site=lax`, timeout 30 min |
| `.env.example` | Variables `APP_DEMO_USERNAME` y `APP_DEMO_PASSWORD` de ejemplo |
| `src/main/resources/messages.properties` | Al final: textos `nav.login`, `nav.logout`, `nav.perfil`, `login.*`, `perfil.*` |
| `src/main/resources/static/css/panel.css` | Al final: `.login-panel` y foco visible (`:focus-visible`) |
| `templates/fragments/layout.html` | Cabecera: enlace "Nuevo producto" y "Mi sesión" solo con sesión; botón "Iniciar sesión" o "Sesión de X" + "Cerrar sesión" (POST con CSRF) |
| `templates/productos/lista.html` | Botones Nuevo, Editar y Eliminar con `th:if="${security?.authenticated}"` |
| `templates/productos/detalle.html` | Botones Editar y Eliminar con el mismo `th:if` |
| `src/test/.../controller/WebRoutesTests.java` | `@Import(SecurityConfig.class)`; helper `autenticado(...)` que agrega Basic a POST, PUT y DELETE |
| `src/test/.../controller/ProductoVistaTests.java` | `@Import(SecurityConfig.class)`, `@WithMockUser` y helper `conCsrf(...)` en los POST |
| `README.md` | Sección "Seguridad - Semana 13" (antes de "Estructura") con reglas y comandos `curl` |

## 3. Reglas de acceso resultantes

**API (`/api/**`, Basic, sin sesión, sin CSRF)**

| Solicitud | Resultado |
|---|---|
| `GET /api/v1/productos` y `/{id}` | 200 público (lo consume el `Frontend`) |
| `POST`, `PUT`, `DELETE` sin Basic o con clave incorrecta | 401 con `WWW-Authenticate` |
| `POST`, `PUT`, `DELETE` con Basic correcto | Llega al controlador (201, 200, 204) |

**Panel (`/admin/**`, formulario, sesión, CSRF)**

| Solicitud | Resultado |
|---|---|
| `GET /`, `/portal`, `/login`, `/error`, `/css/**`, `/js/**`, `/actuator/health` | Público |
| `GET /admin/productos` y `/admin/productos/{id}` | 200 público |
| `GET /admin/productos/nuevo` y `/{id}/editar`, `GET /perfil` sin sesión | 302 a `/login` |
| `POST` del panel con sesión pero sin token CSRF | 403 |
| `POST` del panel con sesión y token CSRF | Continúa al controlador |
| `POST /logout` | Cierra sesión y redirige a `/login?logout` |

## 4. Decisiones de adaptación

- La lectura de la API es pública porque `Frontend/js/api.js` y `productos-api.js` hacen `fetch` sin credenciales. La guía original protegía todo `/api/**`.
- El listado y el detalle del panel son públicos, igual que el catálogo de la guía. Para volverlos privados, quita esas dos reglas `permitAll` de `webSecurity`.
- `CorsConfig` no cambió; la cadena API agrega `.cors(Customizer.withDefaults())` para que siga aplicándose.
- CSRF se desactiva solo en la cadena API (sin estado). En el panel sigue activo.

## 5. Cómo ejecutar

```
export APP_DEMO_PASSWORD='Laboratorio-S13-Temporal!'     (PowerShell: $env:APP_DEMO_PASSWORD='...')
./mvnw clean test
./mvnw spring-boot:run
```

Sin `APP_DEMO_PASSWORD` la aplicación no inicia (es intencional). Los comandos `curl` de comprobación están en `README.md`.
