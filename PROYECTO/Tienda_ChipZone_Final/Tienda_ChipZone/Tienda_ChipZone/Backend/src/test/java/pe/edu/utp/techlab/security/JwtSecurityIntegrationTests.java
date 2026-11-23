package pe.edu.utp.techlab.security;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.techlab.security.model.UserAccount;
import pe.edu.utp.techlab.security.repository.UserAccountRepository;
import pe.edu.utp.techlab.security.service.TokenService;

/**
 * Semana 15: emision y validacion REALES de JWT a traves de los filtros (no jwt() simulado). La clave
 * de prueba esta en src/test/resources/config/application.properties y es publica: solo sirve aqui.
 * Contrato de ChipZone: GET de la API publico; POST, PUT y DELETE solo con Bearer y rol ADMIN.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class JwtSecurityIntegrationTests {

    private static final String PASSWORD = "Solo-prueba-15!";
    private static final String ISSUER = "https://techlab.local";
    private static final String AUDIENCE = "techlab-api";
    private static final String ESCRITURA = "/api/v1/productos";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserAccountRepository users;

    @Autowired
    private PasswordEncoder passwords;

    @Autowired
    private TokenService tokens;

    @Autowired
    private JwtEncoder encoder;

    private String admin;
    private String student;

    @BeforeEach
    void prepare() {
        String id = UUID.randomUUID().toString().replace("-", "");
        admin = "admin_" + id;
        student = "student_" + id;
        users.save(new UserAccount(admin, passwords.encode(PASSWORD), true,
                Set.of("ROLE_USER", "ROLE_ADMIN")));
        users.save(new UserAccount(student, passwords.encode(PASSWORD), true,
                Set.of("ROLE_USER")));
        users.flush();
    }

    private String bearer(String username) {
        return "Bearer " + tokens.issue(username, PASSWORD).access_token();
    }

    private String sign(JwtEncoder signer, String issuer, String audience, Instant issued, Instant expires) {
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer(issuer)
                .subject(student).audience(List.of(audience))
                .issuedAt(issued).expiresAt(expires)
                .claim("roles", List.of("ROLE_USER")).build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();
        return "Bearer " + signer.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    private String signWithoutExpiry(Instant issued) {
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer(ISSUER)
                .subject(student).audience(List.of(AUDIENCE)).issuedAt(issued)
                .claim("roles", List.of("ROLE_USER")).build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();
        return "Bearer " + encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    @Test
    void credencialesEmitenUnTokenRealSinCache() throws Exception {
        String body = "{\"username\":\"" + student + "\",\"password\":\"" + PASSWORD + "\"}";
        mvc.perform(post("/api/v1/auth/token")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", containsString("no-store")))
                .andExpect(jsonPath("$.access_token").isString())
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_in").value(600));

        String invalid = "{\"username\":\"" + student + "\",\"password\":\"incorrecta\"}";
        mvc.perform(post("/api/v1/auth/token")
                        .contentType(MediaType.APPLICATION_JSON).content(invalid))
                .andExpect(status().isUnauthorized());

        String disabled = "off_" + UUID.randomUUID().toString().replace("-", "");
        users.saveAndFlush(new UserAccount(disabled, passwords.encode(PASSWORD), false,
                Set.of("ROLE_USER")));
        String disabledBody = "{\"username\":\"" + disabled + "\",\"password\":\"" + PASSWORD + "\"}";
        mvc.perform(post("/api/v1/auth/token")
                        .contentType(MediaType.APPLICATION_JSON).content(disabledBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cuerpoDeLoginInvalidoResponde400() throws Exception {
        mvc.perform(post("/api/v1/auth/token")
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bearerYRolesProtegenLasEscrituras() throws Exception {
        // La lectura sigue siendo publica (la consume el Frontend sin credenciales).
        mvc.perform(get("/api/v1/productos")).andExpect(status().isOk());

        mvc.perform(post(ESCRITURA).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        // Basic ya no autentica la API.
        mvc.perform(post(ESCRITURA).with(httpBasic(admin, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(post(ESCRITURA).header("Authorization", bearer(student))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
        // ADMIN supera la seguridad; el 400 viene de la validacion del cuerpo.
        mvc.perform(post(ESCRITURA).header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rechazaVencidoAudienciaEmisorYFirmaIncorrectos() throws Exception {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        String expired = sign(encoder, ISSUER, AUDIENCE, now.minusSeconds(600), now.minusSeconds(300));
        String wrongAud = sign(encoder, ISSUER, "otro-servicio", now, now.plusSeconds(600));
        String wrongIss = sign(encoder, "https://otro.local", AUDIENCE, now, now.plusSeconds(600));
        JwtEncoder attacker = NimbusJwtEncoder.withSecretKey(new SecretKeySpec(new byte[32], "HmacSHA256"))
                .algorithm(MacAlgorithm.HS256).build();
        String wrongKey = sign(attacker, ISSUER, AUDIENCE, now, now.plusSeconds(600));
        String missingExp = signWithoutExpiry(now);

        // Se prueba sobre una escritura: un Bearer invalido se rechaza con 401 antes de evaluar el rol.
        for (String invalid : List.of(expired, wrongAud, wrongIss, wrongKey, missingExp)) {
            mvc.perform(post(ESCRITURA).header("Authorization", invalid)
                            .contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    void lasReglasDeLaSesionWebSonIndependientes() throws Exception {
        mvc.perform(get("/admin/productos")).andExpect(status().isOk());
        mvc.perform(get("/admin/productos/nuevo")).andExpect(status().is3xxRedirection());
        mvc.perform(post("/admin/productos").with(user(admin).roles("ADMIN")))
                .andExpect(status().isForbidden());

        MockHttpSession session = (MockHttpSession) mvc.perform(post("/login")
                        .with(csrf()).param("username", admin).param("password", PASSWORD))
                .andExpect(status().is3xxRedirection())
                .andReturn().getRequest().getSession(false);
        Assertions.assertNotNull(session);

        // La cookie de sesion web NO es una credencial de la API.
        mvc.perform(post(ESCRITURA).session(session)
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
    }
}
