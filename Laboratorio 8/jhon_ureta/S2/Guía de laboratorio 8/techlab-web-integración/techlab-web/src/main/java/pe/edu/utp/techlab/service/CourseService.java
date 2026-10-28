package pe.edu.utp.techlab.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Service;
import pe.edu.utp.techlab.dto.CourseDto;

@Service
public class CourseService {
    private final List<CourseDto> cursos = List.of(
        new CourseDto(1L, "HTML y CSS", 12),
        new CourseDto(2L, "Bootstrap", 16),
        new CourseDto(3L, "Spring Boot", 20)
    );

    /** Busca por título sin filtrar por carga horaria. */
    public List<CourseDto> buscar(String texto) {
        return buscar(texto, null);
    }

    /**
     * Busca por título y, opcionalmente, por horas mínimas.
     * Recibe criterios ya validados: no conoce HTTP ni Thymeleaf.
     *
     * @param minHoras horas mínimas (inclusive) o null si no se filtra
     */
    public List<CourseDto> buscar(String texto, Integer minHoras) {
        String criterio = Optional.ofNullable(texto)
            .orElse("")
            .strip()
            .toLowerCase(Locale.ROOT);
        int horasMinimas = minHoras == null ? 0 : minHoras;
        return cursos.stream()
            .filter(c -> criterio.isEmpty()
                || c.titulo().toLowerCase(Locale.ROOT).contains(criterio))
            .filter(c -> c.horas() >= horasMinimas)
            .toList();
    }

    public Optional<CourseDto> buscarPorId(long id) {
        return cursos.stream()
            .filter(c -> c.id() == id)
            .findFirst();
    }

    public int cantidad() {
        return cursos.size();
    }

    public int totalHoras() {
        return cursos.stream()
            .mapToInt(CourseDto::horas)
            .sum();
    }
}
