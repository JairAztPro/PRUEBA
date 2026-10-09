package pe.edu.utp.techlab.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import pe.edu.utp.techlab.dto.CourseDto;
import pe.edu.utp.techlab.service.CourseService;

@WebMvcTest({CourseViewController.class, PortalController.class})
@Import(CourseService.class)
class CourseViewControllerTests {

    @Autowired
    private MockMvc mvc;

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
    void filtroConservaConsultaYReduceResultados() throws Exception {
        mvc.perform(get("/cursos").param("q", "Spring"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("q", "Spring"))
            .andExpect(model().attribute("cursos", hasSize(1)))
            .andExpect(content().string(containsString("Spring Boot")));
    }

    @Test
    void detalleRenderizaElCursoSolicitado() throws Exception {
        mvc.perform(get("/cursos/2"))
            .andExpect(status().isOk())
            .andExpect(view().name("cursos/detalle"))
            .andExpect(model().attribute("curso", new CourseDto(2L, "Bootstrap", 16)))
            .andExpect(content().string(containsString("Bootstrap")));
    }

    @Test
    void erroresConservanEstadosHttp() throws Exception {
        mvc.perform(get("/cursos/999"))
            .andExpect(status().isNotFound());

        mvc.perform(get("/cursos").param("q", "x".repeat(61)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void resumenMuestraCantidadYHoras() throws Exception {
        mvc.perform(get("/cursos/resumen"))
            .andExpect(status().isOk())
            .andExpect(view().name("cursos/resumen"))
            .andExpect(model().attribute("cantidadCursos", 3))
            .andExpect(model().attribute("totalHoras", 48))
            .andExpect(content().string(containsString("48")));
    }

    @Test
    void filtroMinHorasReduceResultadosYConservaElCampo() throws Exception {
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
    void portalRedirigeALaVistaDinamica() throws Exception {
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