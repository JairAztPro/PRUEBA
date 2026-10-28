package pe.edu.utp.techlab.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pe.edu.utp.techlab.dto.CategoriaDto;


/** Prueba el servicio contra MySQL real (requiere `docker compose up -d mysql`). */
@SpringBootTest
class ProductoServiceTests {

    @Autowired
    private ProductoService service;

    @Test
    void consultaVaciaDevuelveTodos() {
        assertEquals(47, service.buscar("", "").size());
    }

    @Test
    void busquedaNormalizaEspaciosYMayusculas() {
        var resultado = service.buscar("  RYZEN ", "");
        assertEquals(1, resultado.size());
        assertEquals(4L, resultado.getFirst().id());
    }

    @Test
    void filtraPorCategoria() {
        assertEquals(14, service.buscar("", "gaming").size());
        assertEquals(13, service.buscar("", "Accesorios").size());
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
       assertEquals(List.of("accesorios", "celulares", "gaming", "laptops"),
        service.categorias().stream().map(CategoriaDto::nombre).toList());
    }
}
