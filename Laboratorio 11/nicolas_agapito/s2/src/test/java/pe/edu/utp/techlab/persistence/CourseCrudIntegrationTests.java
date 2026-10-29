package pe.edu.utp.techlab.persistence;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.ConstraintViolationException;
import pe.edu.utp.techlab.dto.CourseInputDto;
import pe.edu.utp.techlab.exception.CategoryNotFoundException;
import pe.edu.utp.techlab.exception.CourseNotFoundException;
import pe.edu.utp.techlab.exception.DuplicateCourseTitleException;
import pe.edu.utp.techlab.service.CourseService;

@SpringBootTest
@Transactional
class CourseCrudIntegrationTests {
    @Autowired private CourseService service;
    @Autowired private JdbcTemplate jdbc;

    @Test void cursoPersisteConSuCategoria() {
        long categoryId = service.listarCategorias().stream().filter(c -> c.nombre().equals("Back-end")).findFirst().orElseThrow().id();
        String title = "Relaciones JPA " + System.nanoTime();
        var created = service.crear(new CourseInputDto(title, 24, categoryId));
        assertNotNull(created.id()); assertEquals(categoryId, created.categoriaId()); assertEquals("Back-end", created.categoriaNombre());
        Long fk = jdbc.queryForObject("SELECT categoria_id FROM cursos WHERE id = ?", Long.class, created.id());
        assertEquals(categoryId, fk);
    }
    @Test void servicioRechazaCategoriaInexistente() {
        assertThrows(CategoryNotFoundException.class, () -> service.crear(new CourseInputDto("Curso sin categoría real", 12, 999999L)));
    }
    @Test void foreignKeyImpideReferenciaInexistente() {
        assertThrows(DataIntegrityViolationException.class, () -> jdbc.update(
            "INSERT INTO cursos (titulo, horas, categoria_id) VALUES (?, ?, ?)", "FK inválida " + System.nanoTime(), 12, 999999L));
    }
    @Test void cicloCrudCompletoYReglasDeNegocio() {
        long categoryId = service.listarCategorias().get(0).id(); String title = "Prueba CRUD " + System.nanoTime();
        var created = service.crear(new CourseInputDto(title, 24, categoryId));
        var updated = service.actualizar(created.id(), new CourseInputDto(title + " actualizado", 32, categoryId));
        assertEquals(32, updated.horas()); assertEquals(title + " actualizado", service.buscarPorId(created.id()).orElseThrow().titulo());
        assertThrows(DuplicateCourseTitleException.class, () -> service.crear(new CourseInputDto("Spring Boot", 20, categoryId)));
        assertThrows(ConstraintViolationException.class, () -> service.crear(new CourseInputDto(" ", 0, categoryId)));
        service.eliminar(created.id()); assertTrue(service.buscarPorId(created.id()).isEmpty());
        assertThrows(CourseNotFoundException.class, () -> service.eliminar(999999L));
    }
}
