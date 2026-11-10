package pe.edu.utp.techlab.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.utp.techlab.config.SecurityConfig;
import pe.edu.utp.techlab.controller.ProductoApiExceptionHandler;
import pe.edu.utp.techlab.controller.ProductoController;
import pe.edu.utp.techlab.dto.ProductoDto;
import pe.edu.utp.techlab.dto.ProductoInputDto;
import pe.edu.utp.techlab.service.ProductoService;

/**
 * Cadena API: Basic por solicitud, sin sesion y sin redirecciones a HTML. La lectura es publica porque la
 * tienda del Frontend la consume sin credenciales; las escrituras responden 401 sin Basic correcto.
 */
@WebMvcTest({
        ProductoController.class,
        ProductoApiExceptionHandler.class
})
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
        "app.security.demo-username=docente",
        "app.security.demo-password=Laboratorio-S13-Temporal!"
})
class ApiSecurityTests {

    private static final String JSON_PRODUCTO = """
            {"nombre":"Mouse Nuevo","marca":"msi","categoriaId":1,"precio":89,"stock":true}
            """;

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ProductoService service;

    private ProductoDto rtx() {
        return new ProductoDto(2L, "RTX 4060 Gaming", "Tarjeta Grafica MSI RTX 4060 Gaming",
                "msi", "gaming", 3L, 749, null, true, "Tarjeta grafica.", "Ray tracing.",
                List.of(), List.of(), List.of());
    }

    @Test
    void lecturaDelCatalogoEsPublicaYNoCreaSesion() throws Exception {
        given(service.buscar("", "")).willReturn(List.of(rtx()));

        mvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("RTX 4060 Gaming"))
                .andExpect(cookie().doesNotExist("JSESSIONID"));
    }

    @Test
    void detalleDeLaApiEsPublico() throws Exception {
        given(service.buscarPorId(2L)).willReturn(java.util.Optional.of(rtx()));

        mvc.perform(get("/api/v1/productos/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void crearSinCredencialesResponde401SinRedirigir() throws Exception {
        mvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_PRODUCTO))
                .andExpect(status().isUnauthorized())
                .andExpect(header().exists("WWW-Authenticate"));
    }

    @Test
    void crearConClaveIncorrectaResponde401() throws Exception {
        mvc.perform(post("/api/v1/productos")
                        .with(httpBasic("docente", "incorrecta"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_PRODUCTO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void crearConBasicCorrectoResponde201SinCsrfYSinSesion() throws Exception {
        given(service.crear(any(ProductoInputDto.class))).willReturn(new ProductoDto(
                48L, "Mouse Nuevo", "Mouse Nuevo", "msi", "accesorios", 1L, 89, null, true, null, null,
                List.of(), List.of(), List.of()));

        mvc.perform(post("/api/v1/productos")
                        .with(httpBasic("docente", "Laboratorio-S13-Temporal!"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_PRODUCTO))
                .andExpect(status().isCreated())
                .andExpect(cookie().doesNotExist("JSESSIONID"));
    }

    @Test
    void eliminarSinCredencialesResponde401() throws Exception {
        mvc.perform(delete("/api/v1/productos/2"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void eliminarConBasicCorrectoResponde204() throws Exception {
        mvc.perform(delete("/api/v1/productos/2")
                        .with(httpBasic("docente", "Laboratorio-S13-Temporal!")))
                .andExpect(status().isNoContent());
    }
}
