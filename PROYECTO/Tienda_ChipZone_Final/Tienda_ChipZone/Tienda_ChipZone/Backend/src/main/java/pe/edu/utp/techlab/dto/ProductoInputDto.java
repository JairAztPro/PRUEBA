package pe.edu.utp.techlab.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO exclusivo de entrada (cuerpo JSON de POST/PUT). No incluye el id:
 * lo asigna la base de datos o viene en la ruta.
 *
 * Semana 11: las reglas de forma (obligatorio, longitud, rango, formato de URL) son
 * restricciones declarativas de Jakarta Bean Validation; los mensajes viven en
 * ValidationMessages.properties. Las reglas que dependen de datos existentes
 * (nombre unico) siguen en el servicio. Se usan tipos envoltorio (Integer, Boolean)
 * para distinguir un campo omitido de un valor valido.
 */
public class ProductoInputDto {

    /** Una URL http(s) o una ruta que empiece con "/". Vacia se permite: el servicio la descarta. */
    private static final String URL_IMAGEN = "^\\s*((https?://|/)\\S+)?\\s*$";

    @NotBlank(message = "{producto.nombre.obligatorio}")
    @Size(max = 120, message = "{producto.nombre.longitud}")
    private String nombre;

    @Size(max = 200, message = "{producto.nombreCompleto.longitud}")
    private String nombreCompleto;

    @NotBlank(message = "{producto.marca.obligatoria}")
    @Size(max = 40, message = "{producto.marca.longitud}")
    private String marca;

    @NotNull(message = "{producto.categoria.obligatoria}")
  private Long categoriaId;

    @NotNull(message = "{producto.precio.obligatorio}")
    @Min(value = 1, message = "{producto.precio.rango}")
    @Max(value = 1_000_000, message = "{producto.precio.rango}")
    private Integer precio;

    @Min(value = 0, message = "{producto.descuento.rango}")
    @Max(value = 100, message = "{producto.descuento.rango}")
    private Integer descuento;

    @NotNull(message = "{producto.stock.obligatorio}")
    private Boolean stock;

    @Size(max = 255, message = "{producto.resumen.longitud}")
    private String resumen;

    @Size(max = 2000, message = "{producto.descripcion.longitud}")
    private String descripcion;

    @Size(max = 10, message = "{producto.imagenes.maximo}")
    private List<
            @Size(max = 1000, message = "{producto.imagen.longitud}")
            @Pattern(regexp = URL_IMAGEN, message = "{producto.imagen.url}") String> imagenes;

    @Size(max = 20, message = "{producto.etiquetas.maximo}")
    private List<@Size(max = 60, message = "{producto.etiqueta.longitud}") String> etiquetas;

    @Size(max = 30, message = "{producto.especificaciones.maximo}")
    private List<ProductoDto.@Valid Especificacion> especificaciones;

    public ProductoInputDto() {
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public Integer getPrecio() { return precio; }
    public void setPrecio(Integer precio) { this.precio = precio; }

    public Integer getDescuento() { return descuento; }
    public void setDescuento(Integer descuento) { this.descuento = descuento; }

    public Boolean getStock() { return stock; }
    public void setStock(Boolean stock) { this.stock = stock; }

    public String getResumen() { return resumen; }
    public void setResumen(String resumen) { this.resumen = resumen; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public List<String> getImagenes() { return imagenes; }
    public void setImagenes(List<String> imagenes) { this.imagenes = imagenes; }

    public List<String> getEtiquetas() { return etiquetas; }
    public void setEtiquetas(List<String> etiquetas) { this.etiquetas = etiquetas; }

    public List<ProductoDto.Especificacion> getEspecificaciones() { return especificaciones; }
    public void setEspecificaciones(List<ProductoDto.Especificacion> especificaciones) {
        this.especificaciones = especificaciones;
    }
}
