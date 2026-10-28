package pe.edu.utp.techlab.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pe.edu.utp.techlab.dto.CourseInputDto;

class CourseInputDtoValidationTests {
    private static ValidatorFactory factory;
    private static Validator validator;
    @BeforeAll static void iniciarValidador() { factory = Validation.buildDefaultValidatorFactory(); validator = factory.getValidator(); }
    @AfterAll static void cerrarValidador() { factory.close(); }
    @Test void entradaValidaNoProduceViolaciones() { assertTrue(validator.validate(new CourseInputDto("Spring Data JPA", 24, 2L)).isEmpty()); }
    @Test void entradaInvalidaIdentificaCadaCampo() {
        Set<String> fields = validator.validate(new CourseInputDto("   ", 0, null)).stream()
            .map(v -> v.getPropertyPath().toString()).collect(Collectors.toSet());
        assertEquals(Set.of("titulo", "horas", "categoriaId"), fields);
    }
}
