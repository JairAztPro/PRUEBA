package pe.edu.utp.techlab.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.techlab.dto.CourseDto;
import pe.edu.utp.techlab.entity.Course;
import pe.edu.utp.techlab.mapper.CourseMapper;
import pe.edu.utp.techlab.repository.CourseRepository;

@Service
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository repository;
    private final CourseMapper mapper;

    public CourseService(
            CourseRepository repository,
            CourseMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /** Busca por título sin filtrar por carga horaria. */
    public List<CourseDto> buscar(String texto) {
        return buscar(texto, null);
    }

    /**
     * Busca por título y, opcionalmente, por horas mínimas (inclusive).
     * Recibe criterios ya validados: no conoce HTTP ni Thymeleaf.
     * El filtrado lo ejecuta la base de datos, no una lista en memoria.
     *
     * @param minHoras horas mínimas o null si no se filtra
     */
    public List<CourseDto> buscar(String texto, Integer minHoras) {
        String criterio = Optional.ofNullable(texto)
            .orElse("")
            .strip();

        List<Course> entidades;
        if (minHoras == null) {
            entidades = criterio.isBlank()
                ? repository.findAllByOrderByIdAsc()
                : repository
                    .findByTituloContainingIgnoreCaseOrderByIdAsc(criterio);
        } else {
            entidades = criterio.isBlank()
                ? repository.findByHorasGreaterThanEqualOrderByIdAsc(minHoras)
                : repository
                    .findByTituloContainingIgnoreCaseAndHorasGreaterThanEqualOrderByIdAsc(
                        criterio, minHoras);
        }
        return aDtos(entidades);
    }

    public List<CourseDto> buscarPorCategoria(String categoria) {
        String criterio = Optional.ofNullable(categoria)
            .orElse("")
            .strip();
        return aDtos(repository.findByCategoriaIgnoreCaseOrderByIdAsc(criterio));
    }

    public Optional<CourseDto> buscarPorId(long id) {
        return repository.findById(id)
            .map(mapper::toDto);
    }

    public long cantidad() {
        return repository.count();
    }

    public int totalHoras() {
        Long total = repository.sumarHoras();
        return Math.toIntExact(total == null ? 0L : total);
    }

    private List<CourseDto> aDtos(List<Course> entidades) {
        return entidades.stream()
            .map(mapper::toDto)
            .toList();
    }
}
