package pe.edu.utp.techlab.service;

import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.techlab.dto.CourseDto;
import pe.edu.utp.techlab.dto.CourseInputDto;
import pe.edu.utp.techlab.entity.Course;
import pe.edu.utp.techlab.exception.CourseNotFoundException;
import pe.edu.utp.techlab.exception.DuplicateCourseTitleException;
import pe.edu.utp.techlab.exception.InvalidCourseDataException;
import pe.edu.utp.techlab.mapper.CourseMapper;
import pe.edu.utp.techlab.repository.CourseRepository;

@Service
@Transactional(readOnly = true)
public class CourseService {
    private static final int MAX_QUERY_LENGTH = 60;
    private final CourseRepository repository;
    private final CourseMapper mapper;
    public CourseService(CourseRepository repository, CourseMapper mapper) { this.repository = repository; this.mapper = mapper; }

    public List<CourseDto> buscar(String texto) { return buscar(texto, null); }
    public List<CourseDto> buscar(String texto, Integer minHoras) {
        String criterio = Optional.ofNullable(texto).orElse("").strip();
        if (criterio.length() > MAX_QUERY_LENGTH) throw new InvalidCourseDataException("La búsqueda admite hasta 60 caracteres");
        List<Course> entidades;
        if (minHoras == null) entidades = criterio.isBlank() ? repository.findAllByOrderByIdAsc() : repository.findByTituloContainingIgnoreCaseOrderByIdAsc(criterio);
        else entidades = criterio.isBlank() ? repository.findByHorasGreaterThanEqualOrderByIdAsc(minHoras) : repository.findByTituloContainingIgnoreCaseAndHorasGreaterThanEqualOrderByIdAsc(criterio, minHoras);
        return aDtos(entidades);
    }
    public List<CourseDto> buscarPorCategoria(String categoria) { return aDtos(repository.findByCategoriaIgnoreCaseOrderByIdAsc(Optional.ofNullable(categoria).orElse("").strip())); }
    public Optional<CourseDto> buscarPorId(long id) { return repository.findById(id).map(mapper::toDto); }
    public long cantidad() { return repository.count(); }
    public int totalHoras() { Long total = repository.sumarHoras(); return Math.toIntExact(total == null ? 0L : total); }

    @Transactional
    public CourseDto crear(CourseInputDto input) {
        Datos d = validar(input);
        if (repository.existsByTituloIgnoreCase(d.titulo)) throw new DuplicateCourseTitleException(d.titulo);
        try { return mapper.toDto(repository.saveAndFlush(Course.crear(d.titulo, d.horas, d.categoria))); }
        catch (DataIntegrityViolationException e) { throw new DuplicateCourseTitleException(d.titulo); }
    }
    @Transactional
    public CourseDto actualizar(long id, CourseInputDto input) {
        Course entity = repository.findById(id).orElseThrow(() -> new CourseNotFoundException(id));
        Datos d = validar(input);
        if (repository.existsByTituloIgnoreCaseAndIdNot(d.titulo, id)) throw new DuplicateCourseTitleException(d.titulo);
        entity.actualizar(d.titulo, d.horas, d.categoria);
        try { repository.flush(); return mapper.toDto(entity); }
        catch (DataIntegrityViolationException e) { throw new DuplicateCourseTitleException(d.titulo); }
    }
    @Transactional
    public void eliminar(long id) { Course entity = repository.findById(id).orElseThrow(() -> new CourseNotFoundException(id)); repository.delete(entity); repository.flush(); }
    private Datos validar(CourseInputDto input) {
        if (input == null) throw new InvalidCourseDataException("Debe enviar los datos del curso");
        String titulo = Optional.ofNullable(input.getTitulo()).orElse("").strip().replaceAll("\\s+", " ");
        if (titulo.isBlank()) throw new InvalidCourseDataException("El título es obligatorio");
        if (titulo.length() > 120) throw new InvalidCourseDataException("El título admite hasta 120 caracteres");
        Integer horas = input.getHoras();
        if (horas == null || horas < 1 || horas > 500) throw new InvalidCourseDataException("Las horas deben estar entre 1 y 500");
        String categoria = Optional.ofNullable(input.getCategoria()).orElse("General").strip().replaceAll("\\s+", " ");
        if (categoria.isBlank()) categoria = "General";
        if (categoria.length() > 40) throw new InvalidCourseDataException("La categoría admite hasta 40 caracteres");
        return new Datos(titulo, horas, categoria);
    }
    private List<CourseDto> aDtos(List<Course> entidades) { return entidades.stream().map(mapper::toDto).toList(); }
    private record Datos(String titulo, int horas, String categoria) {}
}
