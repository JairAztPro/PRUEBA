package pe.edu.utp.techlab.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import pe.edu.utp.techlab.exception.InvalidProductoDataException;
import jakarta.validation.constraints.NotNull;

/**
 * Objeto de respaldo del formulario HTML. Todo llega como texto (un navegador no envia
 * listas ni numeros tipados), asi que aqui se convierte a {@link ProductoInputDto}.
 * Imagenes: una URL por linea. Etiquetas: separadas por coma.
 * Especificaciones: una por linea con el formato "Clave: valor".
 *
 * Semana 11: valida la forma de los textos con Bean Validation (obligatorio, longitud y
 * numero entero). Los rangos y las listas se validan en {@link ProductoInputDto} una vez
 * convertido el formulario (ver ProductoVistaController), asi hay una sola fuente de reglas.
 */
public class ProductoFormDto {

    /** Entero opcional (vacio permitido): el rango lo valida ProductoInputDto. */
    private static final String ENTERO = "^\\s*(-?\\d{1,9})?\\s*$";

    @NotBlank(message = "{producto.nombre.obligatorio}")
    @Size(max = 120, message = "{producto.nombre.longitud}")
    private String nombre = "";

    @Size(max = 200, message = "{producto.nombreCompleto.longitud}")
    private String nombreCompleto = "";

    @NotBlank(message = "{producto.marca.obligatoria}")
    @Size(max = 40, message = "{producto.marca.longitud}")
    private String marca = "";

    @NotNull(message = "{producto.categoria.obligatoria}")
    private Long categoriaId;
    @NotBlank(message = "{producto.precio.obligatorio}")
    @Pattern(regexp = ENTERO, message = "{producto.precio.formato}")
    private String precio = "";

    @Pattern(regexp = ENTERO, message = "{producto.descuento.formato}")
    private String descuento = "";

    private boolean stock = true;

    @Size(max = 255, message = "{producto.resumen.longitud}")
    private String resumen = "";

    @Size(max = 2000, message = "{producto.descripcion.longitud}")
    private String descripcion = "";

    private String imagenes = "";
    private String etiquetas = "";
    private String especificaciones = "";

    public ProductoFormDto() {
    }

    public static ProductoFormDto desde(ProductoDto p) {
        ProductoFormDto form = new ProductoFormDto();
        form.nombre = p.nombre();
        form.nombreCompleto = p.nombreCompleto();
        form.marca = p.marca();
        form.categoriaId = p.categoriaId();
        form.precio = String.valueOf(p.precio());
        form.descuento = p.descuento() == null ? "" : String.valueOf(p.descuento());
        form.stock = p.stock();
        form.resumen = p.resumen() == null ? "" : p.resumen();
        form.descripcion = p.descripcion() == null ? "" : p.descripcion();
        form.imagenes = String.join("\n", p.imagenes());
        form.etiquetas = String.join(", ", p.etiquetas());
        form.especificaciones = p.especificaciones().stream()
                .map(e -> e.clave() + ": " + e.valor())
                .collect(Collectors.joining("\n"));
        return form;
    }

    public ProductoInputDto toInput() {
        ProductoInputDto input = new ProductoInputDto();
        input.setNombre(nombre);
        input.setNombreCompleto(nombreCompleto);
        input.setMarca(marca);
        input.setCategoriaId(categoriaId);
        input.setPrecio(entero(precio, "El precio"));
        input.setDescuento(entero(descuento, "El descuento"));
        input.setStock(stock);
        input.setResumen(resumen);
        input.setDescripcion(descripcion);
        input.setImagenes(lineas(imagenes));
        input.setEtiquetas(etiquetasDesdeTexto(etiquetas));
        input.setEspecificaciones(especificacionesDesdeTexto(especificaciones));
        return input;
    }

    private static Integer entero(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(texto.strip());
        } catch (NumberFormatException ex) {
            throw new InvalidProductoDataException(campo + " debe ser un numero entero");
        }
    }

    private static List<String> lineas(String texto) {
        if (texto == null) {
            return List.of();
        }
        return Arrays.stream(texto.split("\\R"))
                .map(String::strip)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private static List<String> etiquetasDesdeTexto(String texto) {
        if (texto == null) {
            return List.of();
        }
        return Arrays.stream(texto.split(","))
                .map(String::strip)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private static List<ProductoDto.Especificacion> especificacionesDesdeTexto(String texto) {
        List<ProductoDto.Especificacion> resultado = new ArrayList<>();
        for (String linea : lineas(texto)) {
            int separador = linea.indexOf(':');
            if (separador <= 0) {
                throw new InvalidProductoDataException(
                        "Cada especificacion debe tener el formato Clave: valor");
            }
            resultado.add(new ProductoDto.Especificacion(
                    linea.substring(0, separador).strip(),
                    linea.substring(separador + 1).strip()));
        }
        return resultado;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public String getPrecio() { return precio; }
    public void setPrecio(String precio) { this.precio = precio; }

    public String getDescuento() { return descuento; }
    public void setDescuento(String descuento) { this.descuento = descuento; }

    public boolean isStock() { return stock; }
    public void setStock(boolean stock) { this.stock = stock; }

    public String getResumen() { return resumen; }
    public void setResumen(String resumen) { this.resumen = resumen; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getImagenes() { return imagenes; }
    public void setImagenes(String imagenes) { this.imagenes = imagenes; }

    public String getEtiquetas() { return etiquetas; }
    public void setEtiquetas(String etiquetas) { this.etiquetas = etiquetas; }

    public String getEspecificaciones() { return especificaciones; }
    public void setEspecificaciones(String especificaciones) { this.especificaciones = especificaciones; }
}
