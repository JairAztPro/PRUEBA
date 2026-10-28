package pe.edu.utp.techlab.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import pe.edu.utp.techlab.dto.ProductoDto;
import pe.edu.utp.techlab.entity.Producto;

/**
 * Traduce la entidad JPA a DTO para no exponer JPA a controladores ni vistas.
 * Debe invocarse dentro de la transaccion del servicio (las colecciones son perezosas).
 */
@Component
public class ProductoMapper {

    public ProductoDto toDto(Producto p) {
        return new ProductoDto(
                p.getId(),
                p.getNombre(),
                p.getNombreCompleto(),
                p.getMarca(),
                p.getCategoria().getNombre(),
                p.getCategoria().getId(),
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
}
