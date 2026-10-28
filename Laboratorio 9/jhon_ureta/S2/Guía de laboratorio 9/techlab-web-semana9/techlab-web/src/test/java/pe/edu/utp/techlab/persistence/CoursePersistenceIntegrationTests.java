package pe.edu.utp.techlab.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import pe.edu.utp.techlab.dto.CourseDto;
import pe.edu.utp.techlab.repository.CourseRepository;
import pe.edu.utp.techlab.service.CourseService;

/**
 * Requiere MySQL healthy (docker compose up -d mysql) y DB_PASSWORD definida
 * en la misma terminal donde se ejecuta Maven.
 */
@SpringBootTest
class CoursePersistenceIntegrationTests {

    @Autowired
    private CourseService service;

    @Autowired
    private CourseRepository repository;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void flywayCargaElCatalogoInicial() {
        assertEquals(3L, service.cantidad());
        assertEquals(48, service.totalHoras());

        List<String> titulos = service.buscar("").stream()
            .map(CourseDto::titulo)
            .toList();

        assertEquals(
            List.of("HTML y CSS", "Bootstrap", "Spring Boot"),
            titulos
        );
    }

    @Test
    void repositorioBuscaSinDistinguirMayusculas() {
        var resultado = repository
            .findByTituloContainingIgnoreCaseOrderByIdAsc("boot");

        // "boot" aparece en "Bootstrap" y en "Spring Boot"
        assertEquals(2, resultado.size());
        assertEquals("Bootstrap", resultado.getFirst().getTitulo());
        assertEquals("Spring Boot", resultado.getLast().getTitulo());
    }

    @Test
    void repositorioFiltraPorHorasMinimasInclusivo() {
        var resultado = repository.findByHorasGreaterThanEqualOrderByIdAsc(16);
        assertEquals(2, resultado.size());

        var combinado = repository
            .findByTituloContainingIgnoreCaseAndHorasGreaterThanEqualOrderByIdAsc(
                "boot", 17);
        assertEquals(1, combinado.size());
        assertEquals("Spring Boot", combinado.getFirst().getTitulo());
    }

    @Test
    void repositorioConsultaPorCategoria() {
        var frontend = repository.findByCategoriaIgnoreCaseOrderByIdAsc("frontend");
        assertEquals(
            List.of("HTML y CSS", "Bootstrap"),
            frontend.stream().map(c -> c.getTitulo()).toList()
        );

        var backend = service.buscarPorCategoria(" BACKEND ");
        assertEquals(1, backend.size());
        assertEquals("Spring Boot", backend.getFirst().titulo());
        assertEquals("Backend", backend.getFirst().categoria());
    }

    @Test
    void flywayRegistraLasTresMigracionesComoExitosas() {
        List<String> versiones = jdbc.queryForList(
            "select version from flyway_schema_history "
                + "where success = 1 order by installed_rank",
            String.class);
        assertEquals(List.of("1", "2", "3"), versiones);
    }
}
