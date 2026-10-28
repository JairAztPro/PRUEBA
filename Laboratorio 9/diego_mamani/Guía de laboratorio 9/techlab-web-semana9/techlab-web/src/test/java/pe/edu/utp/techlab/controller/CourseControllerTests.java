package pe.edu.utp.techlab.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.utp.techlab.dto.CourseDto;
import pe.edu.utp.techlab.service.CourseService;

@WebMvcTest(CourseController.class)
class CourseControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CourseService service;

    @Test
    void apiListaYFiltraCursos() throws Exception {
        var bootstrap = new CourseDto(2L, "Bootstrap", 16, "Frontend");
        var spring = new CourseDto(3L, "Spring Boot", 20, "Backend");
        given(service.buscar("boot")).willReturn(List.of(bootstrap, spring));

        mvc.perform(get("/api/v1/cursos").param("q", "boot"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].titulo").value("Bootstrap"))
            .andExpect(jsonPath("$[1].id").value(3))
            .andExpect(jsonPath("$[1].titulo").value("Spring Boot"));
    }

    @Test
    void apiIncluyeLaCategoriaEnElJson() throws Exception {
        given(service.buscar("")).willReturn(List.of(
            new CourseDto(3L, "Spring Boot", 20, "Backend")));

        mvc.perform(get("/api/v1/cursos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].categoria").value("Backend"));
    }

    @Test
    void apiDevuelve404SinCuerpoParaIdInexistente() throws Exception {
        given(service.buscarPorId(999L)).willReturn(Optional.empty());

        mvc.perform(get("/api/v1/cursos/999"))
            .andExpect(status().isNotFound())
            .andExpect(content().string(""));
    }
}
