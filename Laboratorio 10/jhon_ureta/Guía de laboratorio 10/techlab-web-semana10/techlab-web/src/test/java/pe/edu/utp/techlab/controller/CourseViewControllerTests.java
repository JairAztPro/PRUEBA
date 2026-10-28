package pe.edu.utp.techlab.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.utp.techlab.dto.CourseDto;
import pe.edu.utp.techlab.service.CourseService;

@WebMvcTest({CourseViewController.class, PortalController.class})
class CourseViewControllerTests {

    @Autowired
    private MockMvc mvc;

    // El servicio real ya no se importa: se simula para aislar la capa web.
    @MockitoBean
    private CourseService service;

    private final CourseDto html = new CourseDto(1L, "HTML y CSS", 12, "Frontend");
    private final CourseDto bootstrap = new CourseDto(2L, "Bootstrap", 16, "Frontend");
    private final CourseDto spring = new CourseDto(3L, "Spring Boot", 20, "Backend");

    @BeforeEach
    void prepararDatos() {
        // El controlador siempre invoca buscar(texto, minHoras).
        given(service.buscar("", null)).willReturn(List.of(html, bootstrap, spring));
    }

    @Test
    void listadoRenderizaModeloYHtml() throws Exception {
        mvc.perform(get("/cursos"))
            .andExpect(status().isOk())
            .andExpect(view().name("cursos/lista"))
            .andExpect(model().attribute("q", ""))
            .andExpect(model().attribute("cursos", hasSize(3)))
            .andExpect(content().string(containsString("Catálogo de cursos")));
    }

    @Test
    void listadoMuestraLaCategoriaDeCadaCurso() throws Exception {
        mvc.perform(get("/cursos"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Frontend")))
            .andExpect(content().string(containsString("Backend")));
    }

    @Test
    void filtroConservaConsultaYReduceResultados() throws Exception {
        given(service.buscar("Spring", null)).willReturn(List.of(spring));

        mvc.perform(get("/cursos").param("q", "Spring"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("q", "Spring"))
            .andExpect(model().attribute("cursos", hasSize(1)))
            .andExpect(content().string(containsString("Spring Boot")));
    }

    @Test
    void detalleRenderizaElCursoSolicitado() throws Exception {
        given(service.buscarPorId(2L)).willReturn(Optional.of(bootstrap));

        mvc.perform(get("/cursos/2"))
            .andExpect(status().isOk())
            .andExpect(view().name("cursos/detalle"))
            .andExpect(model().attribute("curso", bootstrap))
            .andExpect(content().string(containsString("Bootstrap")))
            .andExpect(content().string(containsString("Frontend")));
    }

    @Test
    void erroresConservanEstadosHttp() throws Exception {
        given(service.buscarPorId(999L)).willReturn(Optional.empty());

        mvc.perform(get("/cursos/999"))
            .andExpect(status().isNotFound());

        mvc.perform(get("/cursos").param("q", "x".repeat(61)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void resumenMuestraCantidadYHoras() throws Exception {
        given(service.cantidad()).willReturn(3L);
        given(service.totalHoras()).willReturn(48);

        mvc.perform(get("/cursos/resumen"))
            .andExpect(status().isOk())
            .andExpect(view().name("cursos/resumen"))
            .andExpect(model().attribute("cantidadCursos", 3L))
            .andExpect(model().attribute("totalHoras", 48))
            .andExpect(content().string(containsString("48")));
    }

    @Test
    void filtroMinHorasReduceResultadosYConservaElCampo() throws Exception {
        given(service.buscar("", 16)).willReturn(List.of(bootstrap, spring));

        mvc.perform(get("/cursos").param("minHoras", "16"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("minHoras", "16"))
            .andExpect(model().attribute("cursos", hasSize(2)))
            .andExpect(content().string(containsString("Bootstrap")))
            .andExpect(content().string(containsString("Spring Boot")))
            .andExpect(content().string(containsString("value=\"16\"")));
    }

    @Test
    void combinaQYMinHorasYConservaAmbosCriterios() throws Exception {
        // "boot" coincide con Bootstrap (16 h) y Spring Boot (20 h)
        given(service.buscar("boot", 16)).willReturn(List.of(bootstrap, spring));
        given(service.buscar("boot", 17)).willReturn(List.of(spring));

        mvc.perform(get("/cursos").param("q", "boot").param("minHoras", "16"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("cursos", hasSize(2)));

        mvc.perform(get("/cursos").param("q", "boot").param("minHoras", "17"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("q", "boot"))
            .andExpect(model().attribute("minHoras", "17"))
            .andExpect(model().attribute("cursos", hasSize(1)))
            .andExpect(content().string(containsString("Spring Boot")))
            .andExpect(content().string(containsString("value=\"boot\"")))
            .andExpect(content().string(containsString("value=\"17\"")));
    }

    @Test
    void minHorasSinCoincidenciasMuestraEstadoVacioConOk() throws Exception {
        given(service.buscar("", 21)).willReturn(List.of());

        mvc.perform(get("/cursos").param("minHoras", "21"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("cursos", hasSize(0)))
            .andExpect(content().string(containsString("No se encontraron cursos")));
    }

    @Test
    void minHorasVacioNoFiltra() throws Exception {
        mvc.perform(get("/cursos").param("minHoras", ""))
            .andExpect(status().isOk())
            .andExpect(model().attribute("cursos", hasSize(3)));
    }

    @Test
    void minHorasInvalidoDevuelve400() throws Exception {
        mvc.perform(get("/cursos").param("minHoras", "-1"))
            .andExpect(status().isBadRequest());
        mvc.perform(get("/cursos").param("minHoras", "201"))
            .andExpect(status().isBadRequest());
        mvc.perform(get("/cursos").param("minHoras", "abc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void portalRedirigeAlCatalogo() throws Exception {
        mvc.perform(get("/portal"))
            .andExpect(status().isFound())
            .andExpect(header().string("Location", "/cursos"));
    }

    @Test
    void raizRedirigeAlCatalogo() throws Exception {
        mvc.perform(get("/"))
            .andExpect(status().isFound())
            .andExpect(header().string("Location", "/cursos"));
    }
}
