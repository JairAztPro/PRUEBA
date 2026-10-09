package pe.edu.utp.techlab.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.utp.techlab.service.CourseService;

@WebMvcTest(CourseController.class)
@Import(CourseService.class)
class CourseControllerTests {

    @Autowired
    private MockMvc mvc;

    @Test
    void apiListaYFiltraCursos() throws Exception {
        mvc.perform(get("/api/v1/cursos").param("q", "boot"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].titulo").value("Bootstrap"))
            .andExpect(jsonPath("$[1].id").value(3))
            .andExpect(jsonPath("$[1].titulo").value("Spring Boot"));
    }

    @Test
    void apiDevuelve404SinCuerpoParaIdInexistente() throws Exception {
        mvc.perform(get("/api/v1/cursos/999"))
            .andExpect(status().isNotFound())
            .andExpect(content().string(""));
    }
}
