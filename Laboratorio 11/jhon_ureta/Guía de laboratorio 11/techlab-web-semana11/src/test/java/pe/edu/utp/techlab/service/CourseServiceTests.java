package pe.edu.utp.techlab.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.edu.utp.techlab.dto.CourseInputDto;
import pe.edu.utp.techlab.entity.Category;
import pe.edu.utp.techlab.entity.Course;
import pe.edu.utp.techlab.exception.InvalidCourseDataException;
import pe.edu.utp.techlab.mapper.CourseMapper;
import pe.edu.utp.techlab.repository.CategoryRepository;
import pe.edu.utp.techlab.repository.CourseRepository;

class CourseServiceTests {
    private CourseRepository courses;
    private CategoryRepository categories;
    private CourseService service;
    private CourseMapper mapper;
    private Category category(long id, String name) { Category c = mock(Category.class); given(c.getId()).willReturn(id); given(c.getNombre()).willReturn(name); return c; }
    @BeforeEach void setup() { courses=mock(CourseRepository.class); categories=mock(CategoryRepository.class); mapper=new CourseMapper(); service=new CourseService(courses,categories,mapper); }
    @Test void rechazaBusquedaDemasiadoLarga() { assertThrows(InvalidCourseDataException.class, () -> service.buscar("x".repeat(61))); }
    @Test void sumarHorasNuloDevuelveCero() { given(courses.sumarHoras()).willReturn(null); assertEquals(0, service.totalHoras()); }
    @Test void listarCategoriasOrdenadasComoDtos() {
        var c=category(2,"Back-end"); given(categories.findAllByOrderByNombreAsc()).willReturn(List.of(c));
        assertEquals("Back-end", service.listarCategorias().get(0).nombre());
    }
    @Test void noSeConsultaCategoriaCuandoNoHayFiltro() {
        given(courses.findAllByOrderByIdAsc()).willReturn(List.of());
        assertTrue(service.buscar("", null, null).isEmpty()); verifyNoInteractions(categories);
    }
    @Test void filtroPorCategoriaValidaSeAplicaEnRepositorio() {
        Category cat = category(2, "Back-end");
        given(categories.findById(2L)).willReturn(Optional.of(cat));
        given(courses.findByCategoria_IdOrderByIdAsc(2L)).willReturn(List.of());
        assertTrue(service.buscar("", null, 2L).isEmpty());
        verify(courses).findByCategoria_IdOrderByIdAsc(2L);
    }
}
