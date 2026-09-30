package pe.edu.utp.techlab.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.techlab.dto.ProductoDto;
import pe.edu.utp.techlab.entity.Producto;
import pe.edu.utp.techlab.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository repo;

    public ProductoService(ProductoRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<ProductoDto> buscar(String consulta, String categoria) {
        return repo.buscar(normalizar(consulta), normalizar(categoria))
                .stream()
                .map(this::aDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> categorias() {
        return repo.categorias();
    }

    @Transactional(readOnly = true)
    public Optional<ProductoDto> buscarPorId(long id) {
        return repo.findById(id).map(this::aDto);
    }

    private ProductoDto aDto(Producto p) {
        return new ProductoDto(
                p.getId(),
                p.getNombre(),
                p.getNombreCompleto(),
                p.getMarca(),
                p.getCategoria(),
                p.getPrecio(),
                p.getDescuento(),
                p.isStock(),
                p.getResumen(),
                p.getDescripcion(),
                List.copyOf(p.getImagenes()),
                List.copyOf(p.getEtiquetas()),
                p.getEspecificaciones().stream()
                        .map(e -> new ProductoDto.Especificacion(e.clave(), e.valor()))
                        .toList());
    }

    private String normalizar(String texto) {
        return Optional.ofNullable(texto)
                .orElse("")
                .strip()
                .toLowerCase(Locale.ROOT);
    }
}