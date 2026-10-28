package pe.edu.utp.techlab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CourseServiceTests {
    private final CourseService service = new CourseService();

    @Test
    void consultaVaciaDevuelveTodos() {
        assertEquals(3, service.buscar("").size());
    }

    @Test
    void busquedaNormalizaEspaciosYMayusculas() {
        var resultado = service.buscar(" SPRING ");
        assertEquals(1, resultado.size());
        assertEquals(3L, resultado.getFirst().id());
    }

    @Test
    void idDesconocidoNoExiste() {
        assertTrue(service.buscarPorId(999L).isEmpty());
    }

    @Test
    void calculaResumenConLosDatosBase() {
        assertThat(service.cantidad()).isEqualTo(3);
        assertThat(service.totalHoras()).isEqualTo(48);
    }

    @Test
    void buscaSinDistinguirMayusculas() {
        assertThat(service.buscar("spring"))
            .extracting("titulo")
            .containsExactly("Spring Boot");
    }

    @Test
    void filtraPorHorasMinimasInclusivo() {
        assertThat(service.buscar("", 16))
            .extracting("titulo")
            .containsExactly("Bootstrap", "Spring Boot");
    }

    @Test
    void combinaTextoYHorasMinimas() {
        // "boot" aparece en "Bootstrap" (16 h) y en "Spring Boot" (20 h)
        assertThat(service.buscar("boot", 16))
            .extracting("titulo")
            .containsExactly("Bootstrap", "Spring Boot");
        assertThat(service.buscar("boot", 17))
            .extracting("titulo")
            .containsExactly("Spring Boot");
    }

    @Test
    void horasMinimasSuperioresDejanListaVacia() {
        assertThat(service.buscar("", 21)).isEmpty();
    }

    @Test
    void sinHorasMinimasConservaElComportamientoOriginal() {
        assertThat(service.buscar("", null)).hasSize(3);
    }
}
