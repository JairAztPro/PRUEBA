package pe.edu.utp.techlab.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.utp.techlab.config.SecurityConfig;
import pe.edu.utp.techlab.controller.LoginController;
import pe.edu.utp.techlab.controller.PerfilController;
import pe.edu.utp.techlab.controller.PortalController;
import pe.edu.utp.techlab.controller.ProductoVistaController;
import pe.edu.utp.techlab.dto.CategoriaDto;
import pe.edu.utp.techlab.dto.ProductoDto;
import pe.edu.utp.techlab.dto.ProductoInputDto;
import pe.edu.utp.techlab.service.ProductoService;
import pe.edu.utp.techlab.web.GlobalViewModel;

/** Frontera del panel Thymeleaf: anonimo frente a autenticado, POST con y sin CSRF, ruta publica frente a privada. */
@WebMvcTest({
        ProductoVistaController.class,
        LoginController.class,
        PerfilController.class,
        PortalController.class,
        GlobalViewModel.class
})
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
        "app.security.demo-username=docente",
        "app.security.demo-password=Laboratorio-S13-Temporal!"
})
class WebSecurityTests {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ProductoService service;

    private ProductoDto producto;

    @BeforeEach
    void prepararDatos() {
        producto = new ProductoDto(2L, "RTX 4060 Gaming", "Tarjeta Grafica MSI RTX 4060 Gaming",
                "msi", "gaming", 3L, 749, null, true, "Tarjeta grafica.", "Ray tracing.",
                List.of(), List.of(), List.of());
        given(service.buscar("", "")).willReturn(List.of(producto));
        given(service.buscarPorId(2L)).willReturn(Optional.of(producto));
        given(service.categorias()).willReturn(List.of(new CategoriaDto(3L, "gaming")));
    }

    @Test
    void catalogoEsPublico() throws Exception {
        mvc.perform(get("/admin/productos"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/lista"));
    }

    @Test
    void detalleEsPublico() throws Exception {
        mvc.perform(get("/admin/productos/2"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/detalle"));
    }

    @Test
    void paginaDeLoginEsPublica() throws Exception {
        mvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    void formularioProtegidoRedirigeAlLogin() throws Exception {
        mvc.perform(get("/admin/productos/nuevo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void edicionProtegidaRedirigeAlLogin() throws Exception {
        mvc.perform(get("/admin/productos/2/editar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void usuarioAutenticadoAbreFormulario() throws Exception {
        mvc.perform(get("/admin/productos/nuevo").with(user("docente")))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/formulario"));
    }

    @Test
    void anonimoNoPuedeEnviarElFormulario() throws Exception {
        mvc.perform(post("/admin/productos")
                        .with(csrf())
                        .param("nombre", "Mouse Nuevo")
                        .param("marca", "msi")
                        .param("categoriaId", "1")
                        .param("precio", "89"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void postSinCsrfEsRechazado() throws Exception {
        mvc.perform(post("/admin/productos")
                        .with(user("docente"))
                        .param("nombre", "Mouse Nuevo")
                        .param("marca", "msi")
                        .param("categoriaId", "1")
                        .param("precio", "89"))
                .andExpect(status().isForbidden());
    }

    @Test
    void eliminarSinCsrfEsRechazado() throws Exception {
        mvc.perform(post("/admin/productos/2/eliminar").with(user("docente")))
                .andExpect(status().isForbidden());
    }

    @Test
    void postAutenticadoConCsrfContinuaAlControlador() throws Exception {
        given(service.crear(any(ProductoInputDto.class))).willReturn(new ProductoDto(
                48L, "Mouse Nuevo", "Mouse Nuevo", "msi", "accesorios", 1L, 89, null, true, null, null,
                List.of(), List.of(), List.of()));

        mvc.perform(post("/admin/productos")
                        .with(user("docente"))
                        .with(csrf())
                        .param("nombre", "Mouse Nuevo")
                        .param("marca", "msi")
                        .param("categoriaId", "1")
                        .param("precio", "89"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/productos/48"));
    }

    @Test
    void logoutPorGetNoCierraLaSesion() throws Exception {
        mvc.perform(get("/logout").with(user("docente")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void logoutPorPostConCsrfRedirigeAlLogin() throws Exception {
        mvc.perform(post("/logout").with(user("docente")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }

    @Test
    void perfilAnonimoRedirigeAlLogin() throws Exception {
        mvc.perform(get("/perfil"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void perfilAutenticadoMuestraLaVista() throws Exception {
        mvc.perform(get("/perfil").with(user("docente")))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/perfil"));
    }
}
