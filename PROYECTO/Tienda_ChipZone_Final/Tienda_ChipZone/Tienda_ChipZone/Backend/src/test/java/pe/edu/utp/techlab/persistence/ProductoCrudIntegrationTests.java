package pe.edu.utp.techlab.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.techlab.dto.ProductoDto;
import pe.edu.utp.techlab.dto.ProductoInputDto;
import pe.edu.utp.techlab.exception.DuplicateProductoNombreException;
import pe.edu.utp.techlab.exception.InvalidProductoDataException;
import pe.edu.utp.techlab.exception.ProductoNotFoundException;
import pe.edu.utp.techlab.service.ProductoService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import pe.edu.utp.techlab.exception.CategoriaNotFoundException;
/**
 * CRUD contra MySQL/MariaDB real. {@code @Transactional} en la clase hace rollback
 * automatico tras cada prueba, asi el catalogo de 47 productos queda intacto.
 */
@SpringBootTest
@Transactional
class ProductoCrudIntegrationTests {

    @Autowired
    private ProductoService service;

    @Autowired
    private JdbcTemplate jdbc;

    private ProductoInputDto entrada(String nombre) {
        ProductoInputDto in = new ProductoInputDto();
        in.setNombre(nombre);
        in.setMarca("  MSI ");
        in.setCategoriaId(1L);
        in.setPrecio(100);
        in.setDescuento(10);
        in.setStock(true);
        in.setImagenes(List.of("https://ejemplo.test/a.jpg", "/images/b.png"));
        in.setEtiquetas(List.of("RGB", "Inalambrico"));
        in.setEspecificaciones(List.of(new ProductoDto.Especificacion("Conexion", "USB")));
        return in;
    }

    @Test
    void cicloCrudCompletoSeEjecutaContraLaBaseReal() {
        String nombre = "Prueba CRUD " + System.nanoTime();
        long antes = service.cantidad();

        ProductoDto creado = service.crear(entrada(nombre));
        assertNotNull(creado.id());
        assertEquals(antes + 1, service.cantidad());
        assertEquals("msi", creado.marca());
        assertEquals("accesorios", creado.categoria());
        assertEquals(nombre, creado.nombreCompleto());
        assertEquals(2, creado.imagenes().size());
        assertEquals(List.of("RGB", "Inalambrico"), creado.etiquetas());
        assertEquals(1, creado.especificaciones().size());

        ProductoInputDto cambios = entrada(nombre + " actualizado");
        cambios.setPrecio(250);
        cambios.setStock(false);
        cambios.setImagenes(List.of("https://ejemplo.test/c.jpg"));
        cambios.setEtiquetas(List.of());
        ProductoDto actualizado = service.actualizar(creado.id(), cambios);
        assertEquals(250, actualizado.precio());
        assertFalse(actualizado.stock());
        assertEquals(1, actualizado.imagenes().size());
        assertTrue(actualizado.etiquetas().isEmpty());
        assertEquals(nombre + " actualizado",
                service.buscarPorId(creado.id()).orElseThrow().nombre());

        service.eliminar(creado.id());
        assertTrue(service.buscarPorId(creado.id()).isEmpty());
        assertEquals(antes, service.cantidad());
    }

    @Test
    void actualizarConservaElPropioNombreSinContarloComoDuplicado() {
        ProductoInputDto cambios = entrada("ROG Strix B550-F");
        cambios.setMarca("asus");
        cambios.setCategoriaId(3L);
        cambios.setPrecio(850);

        ProductoDto actualizado = service.actualizar(1L, cambios);

        assertEquals(850, actualizado.precio());
        assertEquals("ROG Strix B550-F", actualizado.nombre());
    }

    @Test
    void reglasDeFormaLasAplicaBeanValidationEnElServicio() {
        // Semana 11: ya no las comprueba el servicio a mano; las declara ProductoInputDto.
        assertThrows(ConstraintViolationException.class, () -> service.crear(new ProductoInputDto()));
        assertThrows(ConstraintViolationException.class, () -> service.crear(null));

        ProductoInputDto sinPrecio = entrada("Sin precio");
        sinPrecio.setPrecio(0);
        assertThrows(ConstraintViolationException.class, () -> service.crear(sinPrecio));

        ProductoInputDto descuentoAlto = entrada("Descuento alto");
        descuentoAlto.setDescuento(101);
        assertThrows(ConstraintViolationException.class, () -> service.crear(descuentoAlto));

        ProductoInputDto imagenMala = entrada("Imagen mala");
        imagenMala.setImagenes(List.of("javascript:alert(1)"));
        assertThrows(ConstraintViolationException.class, () -> service.crear(imagenMala));

        ConstraintViolationException vacio = assertThrows(ConstraintViolationException.class,
                () -> service.crear(new ProductoInputDto()));
        Set<String> mensajes = vacio.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        assertTrue(mensajes.contains("El nombre es obligatorio"));
        assertTrue(mensajes.contains("El precio es obligatorio"));
    }

    @Test
    void reglasDeNegocioRechazanDatosIncompletosYDuplicados() {
        assertThrows(InvalidProductoDataException.class, () -> service.buscar("x".repeat(61), ""));

        ProductoInputDto especIncompleta = entrada("Especificacion incompleta");
        especIncompleta.setEspecificaciones(List.of(new ProductoDto.Especificacion("Conexion", " ")));
        assertThrows(InvalidProductoDataException.class, () -> service.crear(especIncompleta));

        assertThrows(DuplicateProductoNombreException.class,
                () -> service.crear(entrada(" rog  strix b550-f ")));
        assertThrows(DuplicateProductoNombreException.class,
                () -> service.actualizar(2L, entrada("ROG Strix B550-F")));
    }

    @Test
    void actualizarOEliminarInexistenteLanzaNotFound() {
        assertThrows(ProductoNotFoundException.class, () -> service.actualizar(999_999L, entrada("Nada")));
        assertThrows(ProductoNotFoundException.class, () -> service.eliminar(999_999L));
    }

        @Test
    void categoriaInexistenteLaRechazaElServicio() {
        ProductoInputDto in = entrada("Categoria fantasma");
        in.setCategoriaId(9_999L);
        assertThrows(CategoriaNotFoundException.class, () -> service.crear(in));
    }

    @Test
    void laClaveForaneaRechazaUnaCategoriaInexistente() {
        assertThrows(DataIntegrityViolationException.class, () -> jdbc.update(
                "insert into productos (nombre, nombre_completo, marca, categoria_id, precio, stock) values (?,?,?,?,?,?)",
                "FK prueba", "FK prueba", "msi", 9_999L, 10, true));
    }

    @Test
    void elProductoPersisteSuCategoria() {
        ProductoDto creado = service.crear(entrada("Con categoria " + System.nanoTime()));
        Long fk = jdbc.queryForObject(
                "select categoria_id from productos where id = ?", Long.class, creado.id());
        assertEquals(1L, fk);
    }
}
