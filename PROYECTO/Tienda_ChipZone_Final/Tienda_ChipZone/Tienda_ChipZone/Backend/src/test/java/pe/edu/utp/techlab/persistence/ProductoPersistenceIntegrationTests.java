package pe.edu.utp.techlab.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import pe.edu.utp.techlab.dto.ProductoDto;
import pe.edu.utp.techlab.repository.ProductoRepository;
import pe.edu.utp.techlab.service.ProductoService;

/**
 * Pruebas contra MySQL real (Docker). Verifican que Flyway aplico V1 y V2
 * y que Hibernate (ddl-auto=validate) mapea correctamente el esquema.
 */
@SpringBootTest
class ProductoPersistenceIntegrationTests {

    @Autowired
    private ProductoService service;

    @Autowired
    private ProductoRepository repository;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void flywayCargaElCatalogoInicial() {
        assertEquals(47L, service.cantidad());

        List<String> primeros = service.buscar("", "").stream()
                .limit(3)
                .map(ProductoDto::nombre)
                .toList();

        assertEquals(
                List.of("ROG Strix B550-F", "RTX 4060 Gaming", "RAM DDR4 16GB"),
                primeros);
    }

    @Test
    void flywayRegistroLasMigracionesAplicadas() {
        Integer exitosas = jdbc.queryForObject(
                "select count(*) from flyway_schema_history where success = 1", Integer.class);
        assertEquals(3, exitosas);
    }

    @Test
    void repositorioFiltraPorCategoriaSinDistinguirMayusculas() {
        var gaming = repository.findByCategoria_NombreIgnoreCaseOrderByIdAsc("GAMING");
        assertEquals(14, gaming.size());
        assertEquals(1L, gaming.getFirst().getId());
    }

    @Test
    void repositorioBuscaPorNombreOMarca() {
        var resultado = repository.buscar("ryzen", "");
        assertEquals(1, resultado.size());
        assertEquals("Ryzen 7 5800X3D", resultado.getFirst().getNombre());
    }

    @Test
    void coleccionesSeCargaDentroDeLaTransaccionDelServicio() {
        var rtx = service.buscarPorId(2L).orElseThrow();
        assertEquals(3, rtx.imagenes().size());
        assertEquals(3, rtx.etiquetas().size());
        assertEquals(5, rtx.especificaciones().size());
        assertEquals("Arquitectura", rtx.especificaciones().getFirst().clave());
    }
}
