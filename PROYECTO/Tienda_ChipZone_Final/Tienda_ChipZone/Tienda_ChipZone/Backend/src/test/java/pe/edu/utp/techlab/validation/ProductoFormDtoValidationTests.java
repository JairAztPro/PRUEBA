package pe.edu.utp.techlab.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pe.edu.utp.techlab.dto.ProductoFormDto;

/** Pruebas unitarias de las restricciones del formulario (todo texto) de ProductoFormDto. */
class ProductoFormDtoValidationTests {

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

    private static ProductoFormDto valido() {
        ProductoFormDto form = new ProductoFormDto();
        form.setNombre("Mouse Gamer");
        form.setMarca("msi");
        form.setCategoriaId(1L);
        form.setPrecio("89");
        form.setDescuento("10");
        return form;
    }

    private static Set<String> camposInvalidos(ProductoFormDto form) {
        return validator.validate(form).stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    @Test
    void formularioCorrectoNoTieneViolaciones() {
        assertTrue(validator.validate(valido()).isEmpty());
    }

    @Test
    void formularioNuevoSinCompletarMarcaLosObligatorios() {
        assertEquals(Set.of("nombre", "marca", "categoriaId", "precio"),
                camposInvalidos(new ProductoFormDto()));
    }

    @Test
    void descuentoVacioEsValidoPorqueEsOpcional() {
        ProductoFormDto form = valido();
        form.setDescuento("");
        assertTrue(validator.validate(form).isEmpty());
    }

    @Test
    void precioYDescuentoDebenSerEnteros() {
        ProductoFormDto form = valido();
        form.setPrecio("abc");
        form.setDescuento("10.5");
        assertEquals(Set.of("precio", "descuento"), camposInvalidos(form));

        Set<String> textos = validator.validate(form).stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        assertTrue(textos.contains("El precio debe ser un número entero"));
        assertTrue(textos.contains("El descuento debe ser un número entero"));
    }

    @Test
    void aceptaEspaciosAlrededorDelNumeroYDejaElRangoParaElDto() {
        ProductoFormDto form = valido();
        form.setPrecio(" 0 ");
        form.setDescuento(" 101 ");
        assertTrue(validator.validate(form).isEmpty());
    }

    @Test
    void numerosQueDesbordanIntegerSeRechazanComoFormato() {
        ProductoFormDto form = valido();
        form.setPrecio("99999999999");
        assertEquals(Set.of("precio"), camposInvalidos(form));
    }

    @Test
    void respetaLasLongitudesDeLosTextos() {
        ProductoFormDto form = valido();
        form.setNombre("n".repeat(121));
        form.setResumen("r".repeat(256));
        form.setDescripcion("d".repeat(2001));
        assertEquals(Set.of("nombre", "resumen", "descripcion"), camposInvalidos(form));
    }
}
