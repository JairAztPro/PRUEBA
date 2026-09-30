package pe.edu.utp.techlab.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductoServiceTests {

    @Autowired
    private ProductoService service;

    @Test
    void consultaVaciaDevuelveTodos() {
        assertEquals(9, service.buscar("", "").size());
    }

    @Test
    void busquedaNormalizaEspaciosYMayusculas() {
        var resultado = service.buscar("  RYZEN ", "");
        assertEquals(1, resultado.size());
        assertEquals(4L, resultado.getFirst().id());
    }

    @Test
    void filtraPorCategoria() {
        assertEquals(5, service.buscar("", "gaming").size());
        assertEquals(4, service.buscar("", "Accesorios").size());
    }

    @Test
    void calculaElPrecioFinalConDescuento() {
        var conDescuento = service.buscarPorId(1L).orElseThrow();
        assertTrue(conDescuento.tieneDescuento());
        assertEquals(719, conDescuento.precioFinal());
        assertEquals(180, conDescuento.ahorro());

        var sinDescuento = service.buscarPorId(2L).orElseThrow();
        assertEquals(749, sinDescuento.precioFinal());
        assertEquals(0, sinDescuento.ahorro());
    }

    @Test
    void idDesconocidoNoExiste() {
        assertTrue(service.buscarPorId(999L).isEmpty());
    }

    @Test
    void listaLasCategoriasSinRepetir() {
        assertEquals(java.util.List.of("accesorios", "gaming"), service.categorias());
    }
}