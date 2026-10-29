package pe.edu.utp.techlab.persistence;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.techlab.dto.CourseInputDto;
import pe.edu.utp.techlab.exception.CourseNotFoundException;
import pe.edu.utp.techlab.exception.DuplicateCourseTitleException;
import pe.edu.utp.techlab.exception.InvalidCourseDataException;
import pe.edu.utp.techlab.service.CourseService;

@SpringBootTest
@Transactional
class CourseCrudIntegrationTests {
    @Autowired private CourseService service;

    @Test void cicloCrudCompletoSeEjecutaContraMySql() {
        String titulo = "Prueba CRUD " + System.nanoTime();
        var creado = service.crear(new CourseInputDto(titulo, 24, "Backend"));
        assertNotNull(creado.id());
        assertEquals(24, creado.horas());
        assertEquals("Backend", creado.categoria());
        var actualizado = service.actualizar(creado.id(), new CourseInputDto(titulo + " actualizado", 32, "Java"));
        assertEquals(32, actualizado.horas());
        assertEquals(titulo + " actualizado", service.buscarPorId(creado.id()).orElseThrow().titulo());
        service.eliminar(creado.id());
        assertTrue(service.buscarPorId(creado.id()).isEmpty());
    }

    @Test void reglasDeNegocioRechazanDatosInvalidosYDuplicados() {
        assertThrows(InvalidCourseDataException.class, () -> service.crear(new CourseInputDto(" ", 0)));
        assertThrows(InvalidCourseDataException.class, () -> service.buscar("x".repeat(61)));
        assertThrows(DuplicateCourseTitleException.class, () -> service.crear(new CourseInputDto(" spring boot ", 20)));
        assertThrows(CourseNotFoundException.class, () -> service.eliminar(999999L));
    }
}
