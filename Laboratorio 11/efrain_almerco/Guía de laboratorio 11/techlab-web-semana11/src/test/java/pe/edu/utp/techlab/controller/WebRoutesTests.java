package pe.edu.utp.techlab.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

@WebMvcTest({CourseController.class, PortalController.class})
class WebRoutesTests {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CourseService service;

    private final CourseDto html = new CourseDto(1L, "HTML y CSS", 12, "Frontend");
    private final CourseDto bootstrap = new CourseDto(2L, "Bootstrap", 16, "Frontend");
    private final CourseDto spring = new CourseDto(3L, "Spring Boot", 20, "Backend");

    @BeforeEach
    void prepararDatos() {
        given(service.buscar("", null, null)).willReturn(List.of(html, bootstrap, spring));
        given(service.buscar("SPRING", null, null)).willReturn(List.of(spring));
        given(service.buscarPorId(2L)).willReturn(Optional.of(bootstrap));
        given(service.buscarPorId(999L)).willReturn(Optional.empty());
    }

    @Test
    void listadoYFiltroFuncionan() throws Exception {
        mvc.perform(get("/api/v1/cursos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3));

        mvc.perform(get("/api/v1/cursos").param("q", "SPRING"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(3));
    }

    @Test
    void detalleYErroresUsanEstadosHttp() throws Exception {
        mvc.perform(get("/api/v1/cursos/2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.titulo").value("Bootstrap"));

        mvc.perform(get("/api/v1/cursos/999"))
            .andExpect(status().isNotFound());

        mvc.perform(get("/api/v1/cursos/abc"))
            .andExpect(status().isBadRequest());

        mvc.perform(get("/api/v1/cursos").param("q", "x".repeat(61)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void metodoNoPermitidoYRedireccion() throws Exception {
        mvc.perform(post("/api/v1/cursos"))
            .andExpect(status().isMethodNotAllowed());

        mvc.perform(get("/portal"))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/cursos"));
    }
}
