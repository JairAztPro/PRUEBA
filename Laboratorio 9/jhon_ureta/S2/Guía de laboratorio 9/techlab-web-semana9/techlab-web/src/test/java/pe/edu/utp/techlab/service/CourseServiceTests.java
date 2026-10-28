package pe.edu.utp.techlab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.utp.techlab.dto.CourseDto;
import pe.edu.utp.techlab.entity.Course;
import pe.edu.utp.techlab.mapper.CourseMapper;
import pe.edu.utp.techlab.repository.CourseRepository;

/** Pruebas unitarias del servicio: sin Spring y sin MySQL. */
@ExtendWith(MockitoExtension.class)
class CourseServiceTests {

    @Mock
    private CourseRepository repository;

    private CourseService service;

    @BeforeEach
    void crearServicio() {
        service = new CourseService(repository, new CourseMapper());
    }

    private static Course curso(long id, String titulo, int horas, String categoria) {
        Course c = mock(Course.class);
        when(c.getId()).thenReturn(id);
        when(c.getTitulo()).thenReturn(titulo);
        when(c.getHoras()).thenReturn(horas);
        when(c.getCategoria()).thenReturn(categoria);
        return c;
    }

    @Test
    void consultaVaciaOEnBlancoUsaElListadoCompleto() {
        when(repository.findAllByOrderByIdAsc()).thenReturn(List.of());

        assertThat(service.buscar("   ")).isEmpty();
        assertThat(service.buscar(null)).isEmpty();
    }

    @Test
    void busquedaNormalizaEspaciosAntesDeConsultar() {
        when(repository.findByTituloContainingIgnoreCaseOrderByIdAsc("Spring"))
            .thenReturn(List.of());

        service.buscar(" Spring ");

        verify(repository).findByTituloContainingIgnoreCaseOrderByIdAsc("Spring");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void horasMinimasSeDelegaAlRepositorio() {
        when(repository.findByHorasGreaterThanEqualOrderByIdAsc(16))
            .thenReturn(List.of());

        service.buscar("", 16);

        verify(repository).findByHorasGreaterThanEqualOrderByIdAsc(16);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void combinaTextoYHorasMinimasEnLaConsulta() {
        when(repository
            .findByTituloContainingIgnoreCaseAndHorasGreaterThanEqualOrderByIdAsc("boot", 17))
            .thenReturn(List.of());

        service.buscar("boot", 17);

        verify(repository)
            .findByTituloContainingIgnoreCaseAndHorasGreaterThanEqualOrderByIdAsc("boot", 17);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void convierteEntidadesADtoSinExponerlas() {
        when(repository.findAllByOrderByIdAsc()).thenReturn(List.of(
            curso(3L, "Spring Boot", 20, "Backend")));

        assertThat(service.buscar(""))
            .containsExactly(new CourseDto(3L, "Spring Boot", 20, "Backend"));
    }

    @Test
    void idDesconocidoNoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThat(service.buscarPorId(999L)).isEmpty();
    }

    @Test
    void buscarPorIdConvierteAdto() {
        when(repository.findById(2L)).thenReturn(Optional.of(
            curso(2L, "Bootstrap", 16, "Frontend")));

        assertThat(service.buscarPorId(2L))
            .contains(new CourseDto(2L, "Bootstrap", 16, "Frontend"));
    }

    @Test
    void cantidadDelegaEnCount() {
        when(repository.count()).thenReturn(3L);

        assertThat(service.cantidad()).isEqualTo(3L);
    }

    @Test
    void totalHorasAplicaCeroCuandoNoHayFilas() {
        when(repository.sumarHoras()).thenReturn(null);

        assertThat(service.totalHoras()).isZero();
    }

    @Test
    void totalHorasDevuelveLaSuma() {
        when(repository.sumarHoras()).thenReturn(48L);

        assertThat(service.totalHoras()).isEqualTo(48);
    }

    @Test
    void buscarPorCategoriaNormalizaElCriterio() {
        when(repository.findByCategoriaIgnoreCaseOrderByIdAsc("Frontend"))
            .thenReturn(List.of());

        service.buscarPorCategoria(" Frontend ");

        verify(repository).findByCategoriaIgnoreCaseOrderByIdAsc("Frontend");
    }
}
