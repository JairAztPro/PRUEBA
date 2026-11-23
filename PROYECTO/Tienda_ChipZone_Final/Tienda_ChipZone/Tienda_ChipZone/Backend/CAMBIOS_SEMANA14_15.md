# Semanas 14 y 15 - Usuarios en MySQL, roles y JWT: cambios y añadidos

Todas las rutas son relativas a `Backend/`. Paquete base: `src/main/java/pe/edu/utp/techlab/`.
Punto de partida: el proyecto de la semana 13 (rama de la semana 13). Las migraciones V1 a V3 **no se tocaron**.

## 1. Adaptaciones respecto a las guías (importante para la defensa)

| Guía | ChipZone | Motivo |
|---|---|---|
| `/cursos`, `CourseService`, `/api/v1/cursos` | `/admin/productos`, `ProductoService`, `/api/v1/productos` | El proyecto trabaja con productos |
| La API exige credencial incluso para `GET` | `GET /api/v1/productos` y `/{id}` siguen **públicos** | `Frontend/js/api.js` hace `fetch` sin credenciales; exigir JWT romperia la tienda para cualquier visitante (decisión ya tomada en la semana 13) |
| Semana 14 deja la API de solo lectura con Basic | Se implementa directamente el estado final de la semana 15 (escrituras con Bearer + ADMIN) | Solo se evalúa el proyecto final; evita reescribir las pruebas dos veces |
| `TECHLAB_DB_URL` / `TECHLAB_DB_USER` | Se mantiene `DB_URL` / `DB_USER` / `DB_PASSWORD` | Es la configuración existente del proyecto |
| Base de pruebas con colación `utf8mb4_0900_ai_ci` y `GRANT` | `utf8mb4_unicode_ci`, sin `GRANT` con `root` | XAMPP/MariaDB no soporta `0900`; V1 ya usa `unicode_ci` |
| Java 25 | Java 21 (sin cambios en `pom.xml`) | El código de las guías compila en 21 |
| Pruebas con `TECHLAB_TEST_DB_URL` en `@TestPropertySource` | Las pruebas usan `DB_URL` como el resto | Un solo punto de configuración |
| Clave JWT de pruebas por variable de entorno | Clave fija pública en `src/test/resources/config/application.properties` | Las pruebas no dependen del entorno; **nunca** usar esa clave fuera de pruebas |

Extras que **no** pide la guía y se mantienen de la semana 13: `/portal`, `/actuator/health`, `.cors(Customizer.withDefaults())` en la
cadena API y la cookie de sesión `http-only` + `same-site=lax`.

## 2. Archivos nuevos

| Archivo | Para qué sirve |
|---|---|
| `db/migration/V4__create_users_and_roles.sql` | Tablas `app_users` y `app_user_roles` (UTF8MB4, `unicode_ci`) |
| `security/model/UserAccount.java` | Entidad JPA de la identidad (solo hash BCrypt) |
| `security/repository/UserAccountRepository.java` | `findByUsername`, `existsByUsername` |
| `security/service/JpaUserDetailsService.java` | Adapta la cuenta a Spring Security (única fuente de `UserDetailsService`) |
| `security/seed/LabUserSeeder.java` | Crea `admin` y `estudiante` solo con perfil `lab` y si no existen |
| `security/dto/LoginRequest.java`, `TokenResponse.java` | Entrada y salida del endpoint de token |
| `security/service/TokenService.java` | Autentica con BCrypt y emite el JWT (600 s) |
| `config/JwtConfig.java` | Clave HS256, `JwtEncoder`, `JwtDecoder` con validadores y conversor de roles |
| `controller/TokenController.java` | `POST /api/v1/auth/token` |
| `application-lab.properties` | `server.address=127.0.0.1` |
| `scripts/probar_jwt.py` | Cliente de prueba sin exponer clave ni token |
| `test/.../security/PersistentSecurityIntegrationTests.java` | Login real, roles, CSRF y API con Bearer contra MySQL |
| `test/.../security/JwtSecurityIntegrationTests.java` | JWT firmados reales: vencido, audiencia, emisor, firma y sin `exp` |

## 3. Archivos modificados

| Archivo | Cambio |
|---|---|
| `pom.xml` | + `spring-boot-starter-security-oauth2-resource-server` |
| `application.properties` | Se eliminan `app.security.demo-*`; + `app.jwt.secret-base64` (obligatoria, solo entorno), `issuer`, `audience` |
| `.env.example` | Se reemplazan `APP_DEMO_*` por `TECHLAB_ADMIN_*`, `TECHLAB_STUDENT_*` y `TECHLAB_JWT_SECRET_B64` |
| `config/SecurityConfig.java` | Se elimina el usuario en memoria; `AuthenticationProvider`; API con Resource Server JWT; panel con `hasRole("ADMIN")`; `@Order(1)` y `@Order(2)` |
| `web/SecurityView.java`, `web/GlobalViewModel.java` | + campo `admin` |
| `templates/fragments/layout.html` | El enlace "Nuevo producto" usa `security.admin` (Mi sesión y login siguen con `authenticated`) |
| `templates/productos/lista.html`, `detalle.html` | Nuevo, Editar y Eliminar usan `security.admin` |
| `test/.../controller/WebRoutesTests.java` | `autenticado()` ahora usa `jwt()` con `ROLE_ADMIN`; + `JwtConfig` y `@MockitoBean UserDetailsService` |
| `test/.../controller/ProductoVistaTests.java` | `@WithMockUser(roles = "ADMIN")`; + `JwtConfig` y `@MockitoBean UserDetailsService` |
| `test/.../security/WebSecurityTests.java` | Casos ADMIN y USER (403 en gestión); sin `@TestPropertySource` demo |
| `test/.../security/ApiSecurityTests.java` | Reescrita para Bearer (401, 403, 201, 204; Basic ya no autentica) |
| `test/.../persistence/ProductoPersistenceIntegrationTests.java` | Migraciones exitosas: 3 -> 4 |
| `test/resources/config/application.properties` | Clave JWT fija de pruebas (se retira la cuenta demo) |
| `README.md` | Sección de seguridad, migraciones V3/V4, pruebas, alcance y matriz S14/S15 |

## 4. Reglas de acceso resultantes

| Solicitud | Anónimo | USER | ADMIN |
|---|---|---|---|
| `GET /admin/productos` y detalle | 200 | 200 | 200 |
| `GET /admin/productos/nuevo` o `/{id}/editar` | 302 a login | 403 | 200 |
| `POST` del panel con CSRF válido | 302 a login | 403 | Permitido |
| `POST` del panel sin CSRF | 403 | 403 | 403 |
| `GET /api/v1/productos` y `/{id}` | 200 | 200 | 200 |
| `POST/PUT/DELETE /api/v1/productos...` con Bearer | 401 | 403 | Permitido (400 si el cuerpo es inválido) |
| Bearer inválido, vencido o con audiencia/emisor/firma erróneos | 401 | 401 | 401 |
| `POST /api/v1/auth/token` | 200 con credenciales válidas, 401 si no, 400 si el cuerpo es inválido | | |

## 5. Cómo probar (copia de prueba)

1. Crear la base de pruebas (XAMPP: phpMyAdmin o consola MySQL con `root`):
   ```sql
   CREATE DATABASE IF NOT EXISTS techlab_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. Ejecutar las pruebas apuntando `DB_URL` a esa base (Flyway aplica V1 a V4 y carga los 47 productos):
   ```
   export DB_URL='jdbc:mysql://localhost:3306/techlab_test?serverTimezone=UTC'      (PowerShell: $env:DB_URL='...')
   ./mvnw clean test                                                                (Windows: .\mvnw.cmd clean test)
   ```
   Si usan usuario/clave distintos de `root` sin clave, definan también `DB_USER` y `DB_PASSWORD`.
   No hace falta definir `TECHLAB_JWT_SECRET_B64` para las pruebas.
3. Ejecución manual: seguir la sección "Seguridad" del `README.md` (perfil `lab`, clave JWT y cuentas) y la matriz S14/S15.

Resultado de `clean test`: **pendiente de ejecutar** (completar en la matriz del README con fecha y resultado real).
