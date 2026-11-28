package pe.edu.utp.techlab.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.techlab.security.model.UserAccount;
import pe.edu.utp.techlab.security.repository.UserAccountRepository;
import pe.edu.utp.techlab.security.service.TokenService;

/**
 * Semana 14: arranca la aplicacion real (MySQL + Flyway V1 a V4) y crea cuentas temporales con nombres
 * unicos; {@code @Transactional} revierte los datos de cada prueba. No activa la semilla "lab".
 * La base la define DB_URL: para aislar por completo apunte DB_URL a una base como techlab_test.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PersistentSecurityIntegrationTests {

    private static final String PASSWORD = "Prueba-local-14!";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserAccountRepository users;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private TokenService tokens;

    private String admin;
    private String student;

    @BeforeEach
    void prepare() {
        String suffix = UUID.randomUUID().toString().replace("-", "");
        admin = "admin_" + suffix;
        student = "student_" + suffix;
        users.save(new UserAccount(admin, encoder.encode(PASSWORD), true,
                Set.of("ROLE_USER", "ROLE_ADMIN")));
        users.save(new UserAccount(student, encoder.encode(PASSWORD), true,
                Set.of("ROLE_USER")));
        users.flush();
    }

    @Test
    void catalogoPublicoYFormularioPrivado() throws Exception {
        mvc.perform(get("/admin/productos"))
                .andExpect(status().isOk());
        mvc.perform(get("/admin/productos/nuevo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void adminPersistidoIniciaSesionYAbreElFormulario() throws Exception {
        MvcResult login = mvc.perform(post("/login").with(csrf())
                        .param("username", admin).param("password", PASSWORD))
                .andExpect(redirectedUrl("/admin/productos"))
                .andExpect(authenticated().withUsername(admin))
                .andExpect(authenticated().withAuthentication(auth ->
                        assertThat(auth.getAuthorities().stream()
                                .map(a -> a.getAuthority()).toList())
                                .contains("ROLE_ADMIN", "ROLE_USER")))
                .andReturn();
        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);
        assertThat(session).isNotNull();

        mvc.perform(get("/admin/productos/nuevo").session(session))
                .andExpect(status().isOk());
    }

    @Test
    void estudiantePersistidoNoPuedeAbrirElFormularioDeAdmin() throws Exception {
        MvcResult login = mvc.perform(post("/login").with(csrf())
                        .param("username", student).param("password", PASSWORD))
                .andExpect(authenticated().withRoles("USER"))
                .andReturn();
        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);
        assertThat(session).isNotNull();

        mvc.perform(get("/admin/productos/nuevo").session(session))
                .andExpect(status().isForbidden());
    }

    @Test
    void claveIncorrectaYCuentaDeshabilitadaSonRechazadas() throws Exception {
        mvc.perform(post("/login").with(csrf())
                        .param("username", admin).param("password", "incorrecta"))
                .andExpect(redirectedUrl("/login?error"));

        String disabled = "off_" + UUID.randomUUID().toString().replace("-", "");
        users.saveAndFlush(new UserAccount(disabled, encoder.encode(PASSWORD), false,
                Set.of("ROLE_USER")));
        mvc.perform(post("/login").with(csrf())
                        .param("username", disabled).param("password", PASSWORD))
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void escriturasWebExigenRolYCsrf() throws Exception {
        // Sin token CSRF: 403 aunque sea ADMIN.
        mvc.perform(post("/admin/productos").with(user(admin).roles("ADMIN")))
                .andExpect(status().isForbidden());
        // USER con CSRF valido: 403 por rol.
        mvc.perform(post("/admin/productos").with(user(student).roles("USER")).with(csrf()))
                .andExpect(status().isForbidden());
        // ADMIN con CSRF valido: supera la seguridad; formulario vacio -> vuelve al formulario sin crear datos.
        mvc.perform(post("/admin/productos").with(user(admin).roles("ADMIN")).with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void apiLeePublicoYEscribeSoloConBearerAdmin() throws Exception {
        String userJwt = tokens.issue(student, PASSWORD).access_token();
        String adminJwt = tokens.issue(admin, PASSWORD).access_token();

        mvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk());
        mvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/v1/productos")
                        .header("Authorization", "Bearer " + userJwt)
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
        // ADMIN supera la seguridad; el 400 viene de la validacion del cuerpo.
        mvc.perform(post("/api/v1/productos")
                        .header("Authorization", "Bearer " + adminJwt)
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }
}
