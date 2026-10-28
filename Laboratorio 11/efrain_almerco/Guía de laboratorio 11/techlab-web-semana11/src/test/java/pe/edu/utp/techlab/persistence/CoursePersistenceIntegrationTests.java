package pe.edu.utp.techlab.persistence;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.techlab.service.CourseService;

@SpringBootTest
@Transactional
class CoursePersistenceIntegrationTests {
    @Autowired private CourseService service;
    @Test void catalogoInicialMantieneCursosYCategorias() {
        var courses = service.buscar("");
        assertTrue(courses.size() >= 3);
        assertTrue(courses.stream().anyMatch(c -> c.titulo().equals("HTML y CSS") && c.categoriaNombre().equals("Front-end")));
        assertTrue(courses.stream().anyMatch(c -> c.titulo().equals("Spring Boot") && c.categoriaNombre().equals("Back-end")));
    }
}
