package pe.edu.utp.techlab.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pe.edu.utp.techlab.dto.ProductoDto;
import pe.edu.utp.techlab.dto.ProductoInputDto;
import pe.edu.utp.techlab.entity.Especificacion;
import pe.edu.utp.techlab.entity.Producto;
import pe.edu.utp.techlab.entity.ProductoDatos;
import pe.edu.utp.techlab.exception.DuplicateProductoNombreException;
import pe.edu.utp.techlab.exception.InvalidProductoDataException;
import pe.edu.utp.techlab.exception.ProductoNotFoundException;
import pe.edu.utp.techlab.mapper.ProductoMapper;
import pe.edu.utp.techlab.repository.ProductoRepository;
import pe.edu.utp.techlab.dto.CategoriaDto;
import pe.edu.utp.techlab.entity.Categoria;
import pe.edu.utp.techlab.exception.CategoriaNotFoundException;
import pe.edu.utp.techlab.repository.CategoriaRepository;

/**
 * Lecturas con transaccion de solo lectura (nivel de clase) y escrituras con
 * {@code @Transactional} propio.
 *
 * Semana 11: las reglas de forma (obligatorio, longitudes, rangos, URL de imagen) ya no se
 * comprueban a mano: las declara {@link ProductoInputDto} con Jakarta Bean Validation y
 * {@code @Validated}/{@code @Valid} las aplica tambien a quien llame al servicio sin pasar por
 * un controlador. Aqui quedan la normalizacion de los datos y las reglas que dependen de
 * datos existentes (nombre unico).
 */
@Service
@Validated
@Transactional(readOnly = true)
public class ProductoService {

    private static final int MAX_CONSULTA = 60;

    private final ProductoRepository repository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper mapper;

    public ProductoService(ProductoRepository repository,
                           CategoriaRepository categoriaRepository,
                           ProductoMapper mapper) {
        this.repository = repository;
        this.categoriaRepository = categoriaRepository;
        this.mapper = mapper;
    }

    public List<ProductoDto> buscar(String consulta, String categoria) {
        String q = normalizarConsulta(consulta);
        String cat = normalizarConsulta(categoria);
        if (q.length() > MAX_CONSULTA || cat.length() > MAX_CONSULTA) {
            throw new InvalidProductoDataException("La busqueda admite hasta 60 caracteres");
        }
        return repository.buscar(q, cat).stream()
                .map(mapper::toDto)
                .toList();
    }

       public List<CategoriaDto> categorias() {
       return categoriaRepository.findAllByOrderByNombreAsc().stream()
               .map(c -> new CategoriaDto(c.getId(), c.getNombre()))
               .toList();
   }

    public Optional<ProductoDto> buscarPorId(long id) {
       return repository.buscarConCategoria(id)
               .map(mapper::toDto);
   }

    public long cantidad() {
        return repository.count();
    }

    @Transactional
    public ProductoDto crear(@NotNull @Valid ProductoInputDto input) {
        ProductoDatos datos = normalizar(input);
        if (repository.existsByNombreIgnoreCase(datos.nombre())) {
            throw new DuplicateProductoNombreException(datos.nombre());
        }
        try {
            Producto guardado = repository.saveAndFlush(Producto.crear(datos));
            return mapper.toDto(guardado);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateProductoNombreException(datos.nombre());
        }
    }

    /** Dirty checking: se modifica la entidad gestionada y flush() envia el UPDATE. */
    @Transactional
    public ProductoDto actualizar(long id, @NotNull @Valid ProductoInputDto input) {
        Producto entidad = repository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
        ProductoDatos datos = normalizar(input);
        if (repository.existsByNombreIgnoreCaseAndIdNot(datos.nombre(), id)) {
            throw new DuplicateProductoNombreException(datos.nombre());
        }
        entidad.actualizar(datos);
        try {
            repository.flush();
            return mapper.toDto(entidad);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateProductoNombreException(datos.nombre());
        }
    }

    @Transactional
    public void eliminar(long id) {
        Producto entidad = repository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
        repository.delete(entidad);
        repository.flush();
    }

    /**
     * Convierte la entrada ya validada en datos normalizados: recorta y colapsa espacios,
     * pasa marca y categoria a minusculas, usa el nombre si falta el nombre completo y
     * descarta elementos vacios de las listas.
     */
    private ProductoDatos normalizar(ProductoInputDto input) {
    Categoria categoria = categoriaRepository.findById(input.getCategoriaId())
            .orElseThrow(() -> new CategoriaNotFoundException(input.getCategoriaId()));

    String nombre = linea(input.getNombre());
    String nombreCompleto = linea(input.getNombreCompleto());
    if (nombreCompleto.isEmpty()) {
        nombreCompleto = nombre;
    }
    return new ProductoDatos(
            nombre,
            nombreCompleto,
            linea(input.getMarca()).toLowerCase(Locale.ROOT),
            categoria,
            input.getPrecio(),
            input.getDescuento(),
            input.getStock(),
            textoOpcional(input.getResumen()),
            textoOpcional(input.getDescripcion()),
            limpiarImagenes(input.getImagenes()),
            limpiarEtiquetas(input.getEtiquetas()),
            limpiarEspecificaciones(input.getEspecificaciones()));
}

    private List<String> limpiarImagenes(List<String> imagenes) {
        return Optional.ofNullable(imagenes).orElse(List.of()).stream()
                .filter(Objects::nonNull)
                .map(String::strip)
                .filter(url -> !url.isEmpty())
                .toList();
    }

    private List<String> limpiarEtiquetas(List<String> etiquetas) {
        return Optional.ofNullable(etiquetas).orElse(List.of()).stream()
                .filter(Objects::nonNull)
                .map(ProductoService::linea)
                .filter(e -> !e.isEmpty())
                .toList();
    }

    /**
     * Regla que Bean Validation no expresa bien: una especificacion con solo clave o solo
     * valor es incompleta (las totalmente vacias se descartan).
     */
    private List<Especificacion> limpiarEspecificaciones(List<ProductoDto.Especificacion> especificaciones) {
        List<Especificacion> resultado = Optional.ofNullable(especificaciones).orElse(List.of()).stream()
                .filter(Objects::nonNull)
                .map(e -> new Especificacion(linea(e.clave()), linea(e.valor())))
                .filter(e -> !(e.clave().isEmpty() && e.valor().isEmpty()))
                .toList();
        for (Especificacion e : resultado) {
            if (e.clave().isEmpty() || e.valor().isEmpty()) {
                throw new InvalidProductoDataException("Cada especificacion necesita clave y valor");
            }
        }
        return resultado;
    }

    /** Texto opcional: se recorta; vacio pasa a null. Conserva los saltos de linea internos. */
    private static String textoOpcional(String texto) {
        String limpio = Optional.ofNullable(texto).orElse("").strip();
        return limpio.isEmpty() ? null : limpio;
    }

    /** Texto de una sola linea: recorta y colapsa espacios repetidos. */
    private static String linea(String texto) {
        return Optional.ofNullable(texto).orElse("").strip().replaceAll("\\s+", " ");
    }

    private String normalizarConsulta(String texto) {
        return Optional.ofNullable(texto)
                .orElse("")
                .strip()
                .toLowerCase(Locale.ROOT);
    }
}