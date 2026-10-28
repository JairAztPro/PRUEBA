package pe.edu.utp.techlab.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import pe.edu.utp.techlab.exception.InvalidProductoDataException;

/** Pruebas unitarias (sin Spring ni base de datos) de la conversion formulario -> entrada. */
class ProductoFormDtoTests {

    @Test
    void convierteTextosEnNumerosYListas() {
        ProductoFormDto form = new ProductoFormDto();
        form.setNombre("Mouse");
        form.setPrecio(" 89 ");
        form.setDescuento("");
        form.setImagenes("https://a.test/1.jpg\r\n\r\n /img/2.png \n");
        form.setEtiquetas("RGB, ,Inalambrico ,");
        form.setEspecificaciones("Resolucion: 1920:1080\nConexion: USB");

        ProductoInputDto input = form.toInput();

        assertEquals(89, input.getPrecio());
        assertNull(input.getDescuento());
        assertEquals(List.of("https://a.test/1.jpg", "/img/2.png"), input.getImagenes());
        assertEquals(List.of("RGB", "Inalambrico"), input.getEtiquetas());
        assertEquals(new ProductoDto.Especificacion("Resolucion", "1920:1080"),
                input.getEspecificaciones().get(0));
        assertEquals(2, input.getEspecificaciones().size());
    }

    @Test
    void precioVacioSeDejaParaQueElServicioLoRechace() {
        assertNull(new ProductoFormDto().toInput().getPrecio());
    }

    @Test
    void rechazaNumerosYEspecificacionesMalFormadas() {
        ProductoFormDto precioMalo = new ProductoFormDto();
        precioMalo.setPrecio("abc");
        assertThrows(InvalidProductoDataException.class, precioMalo::toInput);

        ProductoFormDto especMala = new ProductoFormDto();
        especMala.setEspecificaciones("sin separador");
        assertThrows(InvalidProductoDataException.class, especMala::toInput);
    }

    @Test
    void desdeProductoEsInversoDeToInput() {
        ProductoDto p = new ProductoDto(1L, "ROG", "ROG completo", "asus", "gaming", 3L, 899, 20, true,
                "Resumen", null, List.of("https://a.test/1.jpg", "/img/2.png"), List.of("AM4", "DDR4"),
                List.of(new ProductoDto.Especificacion("Socket", "AMD AM4")));

        ProductoInputDto input = ProductoFormDto.desde(p).toInput();

        assertEquals("ROG", input.getNombre());
        assertEquals(899, input.getPrecio());
        assertEquals(20, input.getDescuento());
        assertTrue(input.getStock());
        assertEquals(p.imagenes(), input.getImagenes());
        assertEquals(p.etiquetas(), input.getEtiquetas());
        assertEquals(p.especificaciones(), input.getEspecificaciones());
    }
}
