package pe.edu.utp.techlab.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pe.edu.utp.techlab.dto.ProductoDto;
import pe.edu.utp.techlab.dto.ProductoInputDto;

/** Pruebas unitarias (sin Spring ni base de datos) de las restricciones de ProductoInputDto. */
class ProductoInputDtoValidationTests {

    private static ValidatorFactory fabrica;
    private static Validator validator;

    @BeforeAll
    static void crearValidador() {
        fabrica = Validation.buildDefaultValidatorFactory();
        validator = fabrica.getValidator();
    }

    @AfterAll
    static void cerrarFabrica() {
        fabrica.close();
    }

    private static ProductoInputDto valido() {
        ProductoInputDto in = new ProductoInputDto();
        in.setNombre("Mouse Gamer");
        in.setMarca("msi");
        in.setCategoriaId(1L);
        in.setPrecio(89);
        in.setDescuento(10);
        in.setStock(true);
        in.setImagenes(List.of("https://ejemplo.test/a.jpg", "/images/b.png"));
        in.setEtiquetas(List.of("RGB"));
        in.setEspecificaciones(List.of(new ProductoDto.Especificacion("Conexion", "USB")));
        return in;
    }

    private static Set<String> camposInvalidos(ProductoInputDto in) {
        return validator.validate(in).stream()
                .map(v -> v.getPropertyPath().iterator().next().getName())
                .collect(Collectors.toSet());
    }

    private static Set<String> mensajes(ProductoInputDto in) {
        return validator.validate(in).stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }

    @Test
    void entradaCorrectaNoTieneViolaciones() {
        assertTrue(validator.validate(valido()).isEmpty());
    }

    @Test
    void entradaMinimaConOpcionalesOmitidosEsValida() {
        ProductoInputDto in = new ProductoInputDto();
        in.setNombre("Mouse");
        in.setMarca("msi");
        in.setCategoriaId(1L);
        in.setPrecio(1);
        in.setStock(false);
        assertTrue(validator.validate(in).isEmpty());
    }

    @Test
    void entradaVaciaIdentificaLosCamposObligatorios() {
        assertEquals(Set.of("nombre", "marca", "categoriaId", "precio", "stock"),
                camposInvalidos(new ProductoInputDto()));
    }

    @Test
    void textosEnBlancoCuentanComoVacios() {
        ProductoInputDto in = valido();
        in.setNombre("   ");
        in.setMarca("\t");
        assertEquals(Set.of("nombre", "marca"), camposInvalidos(in));
    }

    @Test
    void mensajesVienenDeValidationMessages() {
        Set<String> textos = mensajes(new ProductoInputDto());
        assertTrue(textos.contains("El nombre es obligatorio"));
        assertTrue(textos.contains("La marca es obligatoria"));
        assertTrue(textos.contains("La categoría es obligatoria"));
        assertTrue(textos.contains("El precio es obligatorio"));
        assertTrue(textos.contains("El stock es obligatorio"));
    }

    @Test
    void precioYDescuentoRespetanSusRangos() {
        ProductoInputDto cero = valido();
        cero.setPrecio(0);
        assertEquals(Set.of("precio"), camposInvalidos(cero));

        ProductoInputDto enorme = valido();
        enorme.setPrecio(1_000_001);
        assertEquals(Set.of("precio"), camposInvalidos(enorme));

        ProductoInputDto limite = valido();
        limite.setPrecio(1_000_000);
        limite.setDescuento(0);
        assertTrue(validator.validate(limite).isEmpty());

        ProductoInputDto descuentoAlto = valido();
        descuentoAlto.setDescuento(101);
        assertEquals(Set.of("descuento"), camposInvalidos(descuentoAlto));

        ProductoInputDto descuentoNegativo = valido();
        descuentoNegativo.setDescuento(-1);
        assertEquals(Set.of("descuento"), camposInvalidos(descuentoNegativo));
    }

    @Test
    void longitudesMaximasCoincidenConLasColumnas() {
        ProductoInputDto en120 = valido();
        en120.setNombre("n".repeat(120));
        assertTrue(validator.validate(en120).isEmpty());

        ProductoInputDto largo = valido();
        largo.setNombre("n".repeat(121));
        largo.setNombreCompleto("c".repeat(201));
        largo.setMarca("m".repeat(41));
        largo.setResumen("r".repeat(256));
        largo.setDescripcion("d".repeat(2001));
        assertEquals(Set.of("nombre", "nombreCompleto", "marca", "resumen", "descripcion"),
                camposInvalidos(largo));
    }

    @Test
    void rechazaImagenesQueNoSonUrlHttpNiRuta() {
        ProductoInputDto in = valido();
        in.setImagenes(List.of("javascript:alert(1)", "ftp://x.test/a.png", "foto.jpg"));
        assertEquals(Set.of("imagenes"), camposInvalidos(in));
        assertEquals(3, validator.validate(in).size());
    }

    @Test
    void aceptaImagenesVaciasQueElServicioDescarta() {
        ProductoInputDto in = valido();
        in.setImagenes(List.of("", "   ", "https://ejemplo.test/a.jpg"));
        assertTrue(validator.validate(in).isEmpty());
    }

    @Test
    void limitaLaCantidadDeElementosDeLasListas() {
        ProductoInputDto in = valido();
        in.setImagenes(java.util.Collections.nCopies(11, "/img/a.png"));
        in.setEtiquetas(java.util.Collections.nCopies(21, "RGB"));
        in.setEspecificaciones(java.util.Collections.nCopies(31, new ProductoDto.Especificacion("A", "B")));
        assertEquals(Set.of("imagenes", "etiquetas", "especificaciones"), camposInvalidos(in));
    }

    @Test
    void validaLosElementosDeEtiquetasYEspecificaciones() {
        ProductoInputDto in = valido();
        in.setEtiquetas(List.of("e".repeat(61)));
        in.setEspecificaciones(List.of(new ProductoDto.Especificacion("c".repeat(256), "valor")));
        assertEquals(Set.of("etiquetas", "especificaciones"), camposInvalidos(in));
    }
}
