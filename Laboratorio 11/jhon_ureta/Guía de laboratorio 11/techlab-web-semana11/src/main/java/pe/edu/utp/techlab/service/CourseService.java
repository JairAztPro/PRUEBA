package pe.edu.utp.techlab.service;

import java.util.List;
import java.util.Optional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pe.edu.utp.techlab.dto.CategoryDto;
import pe.edu.utp.techlab.dto.CourseDto;
import pe.edu.utp.techlab.dto.CourseInputDto;
import pe.edu.utp.techlab.entity.Category;
import pe.edu.utp.techlab.entity.Course;
import pe.edu.utp.techlab.exception.CategoryNotFoundException;
import pe.edu.utp.techlab.exception.CourseNotFoundException;
import pe.edu.utp.techlab.exception.DuplicateCourseTitleException;
import pe.edu.utp.techlab.exception.InvalidCourseDataException;
import pe.edu.utp.techlab.mapper.CourseMapper;
import pe.edu.utp.techlab.repository.CategoryRepository;
import pe.edu.utp.techlab.repository.CourseRepository;

@Service
@Validated
@Transactional(readOnly = true)
public class CourseService {
    private static final int MAX_QUERY_LENGTH = 60;
    private final CourseRepository repository;
    private final CategoryRepository categoryRepository;
    private final CourseMapper mapper;

    public CourseService(CourseRepository repository, CategoryRepository categoryRepository, CourseMapper mapper) {
        this.repository = repository; this.categoryRepository = categoryRepository; this.mapper = mapper;
    }

    public List<CourseDto> buscar(String texto) { return buscar(texto, null, null); }
    public List<CourseDto> buscar(String texto, Integer minHoras) { return buscar(texto, minHoras, null); }

    public List<CourseDto> buscar(String texto, Integer minHoras, Long categoriaId) {
        String criterio = Optional.ofNullable(texto).orElse("").strip();
        if (criterio.length() > MAX_QUERY_LENGTH) throw new InvalidCourseDataException("La búsqueda admite hasta 60 caracteres");
        if (categoriaId != null) buscarCategoria(categoriaId);
        List<Course> cursos;
        if (categoriaId != null) {
            if (minHoras != null) cursos = criterio.isBlank()
                ? repository.findByCategoria_IdAndHorasGreaterThanEqualOrderByIdAsc(categoriaId, minHoras)
                : repository.findByCategoria_IdAndTituloContainingIgnoreCaseAndHorasGreaterThanEqualOrderByIdAsc(categoriaId, criterio, minHoras);
            else cursos = criterio.isBlank()
                ? repository.findByCategoria_IdOrderByIdAsc(categoriaId)
                : repository.findByCategoria_IdAndTituloContainingIgnoreCaseOrderByIdAsc(categoriaId, criterio);
        } else if (minHoras != null) {
            cursos = criterio.isBlank() ? repository.findByHorasGreaterThanEqualOrderByIdAsc(minHoras)
                : repository.findByTituloContainingIgnoreCaseAndHorasGreaterThanEqualOrderByIdAsc(criterio, minHoras);
        } else {
            cursos = criterio.isBlank() ? repository.findAllByOrderByIdAsc()
                : repository.findByTituloContainingIgnoreCaseOrderByIdAsc(criterio);
        }
        return aDtos(cursos);
    }

    public List<CategoryDto> listarCategorias() {
        return categoryRepository.findAllByOrderByNombreAsc().stream()
            .map(c -> new CategoryDto(c.getId(), c.getNombre())).toList();
    }
    public Optional<CourseDto> buscarPorId(long id) { return repository.findDetailById(id).map(mapper::toDto); }
    public long cantidad() { return repository.count(); }
    public int totalHoras() { Long total = repository.sumarHoras(); return Math.toIntExact(total == null ? 0L : total); }

    @Transactional
    public CourseDto crear(@NotNull(message = "Debe enviar los datos del curso") @Valid CourseInputDto input) {
        Datos datos = normalizar(input);
        validarTituloDisponible(datos.titulo(), null);
        Category categoria = buscarCategoria(datos.categoriaId());
        try {
            return mapper.toDto(repository.saveAndFlush(Course.crear(datos.titulo(), datos.horas(), categoria)));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateCourseTitleException(datos.titulo());
        }
    }

    @Transactional
    public CourseDto actualizar(long id, @NotNull(message = "Debe enviar los datos del curso") @Valid CourseInputDto input) {
        Course entity = repository.findById(id).orElseThrow(() -> new CourseNotFoundException(id));
        Datos datos = normalizar(input);
        validarTituloDisponible(datos.titulo(), id);
        Category categoria = buscarCategoria(datos.categoriaId());
        entity.actualizar(datos.titulo(), datos.horas(), categoria);
        try { repository.flush(); return mapper.toDto(entity); }
        catch (DataIntegrityViolationException e) { throw new DuplicateCourseTitleException(datos.titulo()); }
    }

    @Transactional
    public void eliminar(long id) {
        Course entity = repository.findById(id).orElseThrow(() -> new CourseNotFoundException(id));
        repository.delete(entity); repository.flush();
    }

    private Datos normalizar(CourseInputDto input) {
        if (input == null) throw new InvalidCourseDataException("Debe enviar los datos del curso");
        String titulo = Optional.ofNullable(input.getTitulo()).orElse("").strip().replaceAll("\\s+", " ");
        if (titulo.isBlank()) throw new InvalidCourseDataException("El título es obligatorio");
        if (titulo.length() > 120) throw new InvalidCourseDataException("El título admite hasta 120 caracteres");
        Integer horas = input.getHoras();
        if (horas == null || horas < 1 || horas > 500) throw new InvalidCourseDataException("Las horas deben estar entre 1 y 500");
        Long categoriaId = input.getCategoriaId();
        if (categoriaId == null) throw new InvalidCourseDataException("Seleccione una categoría");
        return new Datos(titulo, horas, categoriaId);
    }

    private Category buscarCategoria(long id) { return categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id)); }
    private void validarTituloDisponible(String titulo, Long id) {
        boolean repetido = id == null ? repository.existsByTituloIgnoreCase(titulo) : repository.existsByTituloIgnoreCaseAndIdNot(titulo, id);
        if (repetido) throw new DuplicateCourseTitleException(titulo);
    }
    private List<CourseDto> aDtos(List<Course> cursos) { return cursos.stream().map(mapper::toDto).toList(); }
    private record Datos(String titulo, int horas, long categoriaId) {}
}
