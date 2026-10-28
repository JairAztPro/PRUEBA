package pe.edu.utp.techlab.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.utp.techlab.dto.CategoryDto;
import pe.edu.utp.techlab.dto.CourseDto;
import pe.edu.utp.techlab.service.CourseService;

@WebMvcTest({CourseViewController.class, PortalController.class})
class CourseViewControllerTests {
    @Autowired private MockMvc mvc;
    @MockitoBean private CourseService service;
    private final CourseDto course = new CourseDto(1L,"HTML y CSS",12,1L,"Front-end");
    @BeforeEach void setup() {
        given(service.buscar("", null, null)).willReturn(List.of(course));
        given(service.buscar("", null)).willReturn(List.of(course));
        given(service.listarCategorias()).willReturn(List.of(new CategoryDto(1,"Front-end"),new CategoryDto(2,"Back-end")));
    }
    @Test void listadoRenderizaCursosYCategorias() throws Exception {
        mvc.perform(get("/cursos")).andExpect(status().isOk()).andExpect(view().name("cursos/lista"))
            .andExpect(model().attribute("cursos",hasSize(1))).andExpect(content().string(org.hamcrest.Matchers.containsString("Front-end")));
    }
    @Test void formularioNuevoCargaCategorias() throws Exception {
        mvc.perform(get("/cursos/nuevo")).andExpect(status().isOk()).andExpect(view().name("cursos/formulario"))
            .andExpect(model().attribute("categorias",hasSize(2)));
    }
    @Test void detalleMuestraCategoriaRelacionada() throws Exception {
        given(service.buscarPorId(1L)).willReturn(Optional.of(course));
        mvc.perform(get("/cursos/1")).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("Front-end")));
    }
    @Test void raizRedirigeAlCatalogo() throws Exception { mvc.perform(get("/")).andExpect(status().isFound()).andExpect(header().string("Location","/cursos")); }
}
