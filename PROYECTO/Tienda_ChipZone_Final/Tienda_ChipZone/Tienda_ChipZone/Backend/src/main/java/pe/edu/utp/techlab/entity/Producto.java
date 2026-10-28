package pe.edu.utp.techlab.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.ManyToOne;
/**
 * Entidad sin Lombok ni setters publicos (Semana 9). Desde la Semana 10 se modifica
 * solo con los metodos de dominio {@link #crear} y {@link #actualizar}.
 * El esquema lo crea Flyway (V1) y los datos iniciales vienen de V2.
 */
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "nombre_completo", nullable = false, length = 200)
    private String nombreCompleto;

    @Column(name = "marca", nullable = false, length = 40)
    private String marca;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
          name = "categoria_id",
          nullable = false,
          foreignKey = @ForeignKey(name = "fk_productos_categoria"))
  private Categoria categoria;

    @Column(name = "precio", nullable = false)
    private int precio;

    @Column(name = "descuento")
    private Integer descuento;

    @Column(name = "stock", nullable = false)
    private boolean stock;

    @Column(name = "resumen", length = 255)
    private String resumen;

    @Column(name = "descripcion", length = 2000)
    private String descripcion;

    @ElementCollection
    @CollectionTable(name = "producto_imagenes", joinColumns = @JoinColumn(name = "producto_id"))
    @OrderColumn(name = "orden")
    @Column(name = "url", length = 1000)
    private List<String> imagenes = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "producto_etiquetas", joinColumns = @JoinColumn(name = "producto_id"))
    @OrderColumn(name = "orden")
    @Column(name = "etiqueta", length = 60)
    private List<String> etiquetas = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "producto_especificaciones", joinColumns = @JoinColumn(name = "producto_id"))
    @OrderColumn(name = "orden")
    private List<Especificacion> especificaciones = new ArrayList<>();

    protected Producto() {
    }

    private Producto(ProductoDatos datos) {
        aplicar(datos);
    }

    public static Producto crear(ProductoDatos datos) {
        return new Producto(datos);
    }

    /** Reemplaza todos los datos editables; las colecciones se vacian y se vuelven a llenar. */
    public void actualizar(ProductoDatos datos) {
        aplicar(datos);
    }

    private void aplicar(ProductoDatos datos) {
        this.nombre = datos.nombre();
        this.nombreCompleto = datos.nombreCompleto();
        this.marca = datos.marca();
        this.categoria = datos.categoria();
        this.precio = datos.precio();
        this.descuento = datos.descuento();
        this.stock = datos.stock();
        this.resumen = datos.resumen();
        this.descripcion = datos.descripcion();
        this.imagenes.clear();
        this.imagenes.addAll(datos.imagenes());
        this.etiquetas.clear();
        this.etiquetas.addAll(datos.etiquetas());
        this.especificaciones.clear();
        this.especificaciones.addAll(datos.especificaciones());
    }

    public Long getId() { return id; }

    public String getNombre() { return nombre; }

    public String getNombreCompleto() { return nombreCompleto; }

    public String getMarca() { return marca; }

    public Categoria getCategoria() { return categoria; }

    public int getPrecio() { return precio; }

    public Integer getDescuento() { return descuento; }

    public boolean isStock() { return stock; }

    public String getResumen() { return resumen; }

    public String getDescripcion() { return descripcion; }

    public List<String> getImagenes() { return imagenes; }

    public List<String> getEtiquetas() { return etiquetas; }

    public List<Especificacion> getEspecificaciones() { return especificaciones; }
}
