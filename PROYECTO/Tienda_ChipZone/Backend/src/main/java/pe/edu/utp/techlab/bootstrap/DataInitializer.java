package pe.edu.utp.techlab.bootstrap;

import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.techlab.dto.ProductoDto;
import pe.edu.utp.techlab.entity.Especificacion;
import pe.edu.utp.techlab.entity.Producto;
import pe.edu.utp.techlab.repository.ProductoRepository;

@Component
public class DataInitializer implements ApplicationRunner {

    private final ProductoRepository repo;

    public DataInitializer(ProductoRepository repo) {
        this.repo = repo;
    }

    private static final List<ProductoDto> DATOS = List.of(
            new ProductoDto(1L, "ROG Strix B550-F", "Placa Madre ASUS ROG Strix B550-F Gaming", "asus", "gaming", 899, 20, true,
                    "Placa madre para gaming de alto rendimiento.",
                    "La placa madre ROG Strix B550-F Gaming encarna la esencia pura de Republic of Gamers...",
                    List.of("https://dlcdnwebimgs.asus.com/files/media/A9730D0E-D1A6-40C1-9282-F1BA1B13BFB4/v1/img/spec/connectivity.jpg", "https://spacegamer.com.ar/img/Public/1058/46257-producto-2.jpg", "https://m.media-amazon.com/images/I/81dnPvE6hUL._AC_SX466_.jpg"),
                    List.of("AMD AM4", "PCIe 4.0", "DDR4"),
                    List.of(new ProductoDto.Especificacion("Socket", "AMD AM4"), new ProductoDto.Especificacion("Chipset", "AMD B550"), new ProductoDto.Especificacion("Ranuras RAM", "4 x DIMM DDR4 (Máx 128GB)"), new ProductoDto.Especificacion("Red", "Intel® 2.5Gb Ethernet"), new ProductoDto.Especificacion("Factor", "ATX"))),
            new ProductoDto(2L, "RTX 4060 Gaming", "Tarjeta Gráfica MSI RTX 4060 Gaming", "msi", "gaming", 749, null, true,
                    "Tarjeta gráfica optimizada para Ray Tracing.",
                    "Experimenta mundos virtuales sumamente realistas con la GPU MSI RTX 4060 Gaming...",
                    List.of("https://www.laptopperu.pe/wp-content/uploads/2023/12/tarjeta-grafica-msi-rtx-4060-8gb-gddr6-gaming-x-1.webp", "https://imagedelivery.net/wExMwKP1304LY2ReXCyTPg/0e20af9b-3220-4491-1a1a-434c8a31df00/w=9999", "https://http2.mlstatic.com/D_NQ_NP_976872-MPE73652908309_122023-O.webp"),
                    List.of("Ray Tracing", "DLSS 3", "GDDR6"),
                    List.of(new ProductoDto.Especificacion("Arquitectura", "NVIDIA Ada Lovelace"), new ProductoDto.Especificacion("Memoria", "8GB GDDR6"), new ProductoDto.Especificacion("Frecuencia OC", "2460 MHz"), new ProductoDto.Especificacion("Interfaz", "PCI Express 4.0"), new ProductoDto.Especificacion("Salidas", "3x DP / 1x HDMI"))),
            new ProductoDto(3L, "RAM DDR4 16GB", "RAM DDR4 16GB", "msi", "accesorios", 459, null, false,
                    "Memoria de alto rendimiento para gaming competitivo.",
                    "",
                    List.of("https://media.falabella.com/falabellaPE/117795430_01/w=1500,h=1500,fit=cover"),
                    List.of(),
                    List.of()),
            new ProductoDto(4L, "Ryzen 7 5800X3D", "Procesador AMD Ryzen 7 5800X3D", "amd", "gaming", 1299, null, true,
                    "Procesador diseñado para máximo rendimiento en juegos.",
                    "El procesador AMD Ryzen 7 5800X3D ofrece una tecnología de caché avanzada...",
                    List.of("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQLurUPMj2F0zS9vvrXaO3aXfhmaqduRNNGkCm-dKCgb1qADNoiM8stSps&s=10"),
                    List.of("Socket AM4", "3D V-Cache", "8 Núcleos"),
                    List.of(new ProductoDto.Especificacion("Arquitectura", "Zen 3"), new ProductoDto.Especificacion("Núcleos/Hilos", "8 / 16"), new ProductoDto.Especificacion("Caché L3", "96MB"), new ProductoDto.Especificacion("Velocidad Base", "3.4 GHz"), new ProductoDto.Especificacion("TDP", "105W"))),
            new ProductoDto(5L, "Teclado Vigor GK30", "Teclado Mecánico MSI Vigor GK30", "msi", "accesorios", 320, null, true,
                    "Teclado mecánico retroiluminado con switches táctiles.",
                    "Diseñado para la competición, el teclado MSI Vigor GK30 cuenta con iluminación RGB...",
                    List.of("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSXYCvkZKNggV-tKWA5Ac6c3J4lATVPYDRl5YCVYC8bpAPYoK8_W0ZAZKQ&s=10"),
                    List.of("RGB Mystic Light", "Antighosting", "Resistente"),
                    List.of(new ProductoDto.Especificacion("Tipo de Switch", "Mecánico Táctil"), new ProductoDto.Especificacion("Iluminación", "RGB por zonas"), new ProductoDto.Especificacion("Conexión", "USB 2.0 mallado"), new ProductoDto.Especificacion("Peso", "1042g"), new ProductoDto.Especificacion("Compatibilidad", "Windows 10/11"))),
            new ProductoDto(6L, "Monitor TUF 27\" 170Hz", "Monitor ASUS TUF Gaming 27\" 170Hz", "asus", "gaming", 1450, 15, true,
                    "Pantalla IPS con tasa de refresco ultra rápida.",
                    "Disfruta de una fluidez visual insuperable con el monitor ASUS TUF Gaming...",
                    List.of("https://www.infotec.com.pe/101189-large_default/monitor-gamer-asus-27-tuf-vg279q5r-panel-fast-ips-200hz-03ms-hdmi-dp.jpg"),
                    List.of("IPS Full HD", "1ms MPRT", "FreeSync Premium"),
                    List.of(new ProductoDto.Especificacion("Tamaño", "27 pulgadas"), new ProductoDto.Especificacion("Resolución", "1920 x 1080"), new ProductoDto.Especificacion("Frecuencia", "170Hz"), new ProductoDto.Especificacion("Tiempo de respuesta", "1ms"), new ProductoDto.Especificacion("Puertos", "DisplayPort / 2x HDMI"))),
            new ProductoDto(7L, "Fuente 750W Gold", "Fuente de Poder MSI 750W Gold", "msi", "accesorios", 450, null, true,
                    "Certificación 80 Plus Gold modular para máxima eficiencia.",
                    "Energía estable y limpia para todo tu hardware de alto rendimiento...",
                    List.of("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS8ZggilGieQahiFGG-iFUUp-HgLYnMwJdLOH7wa7qdyxpO6UoXoC0MOtWS&s=10"),
                    List.of("80 Plus Gold", "Modular", "Silenciosa"),
                    List.of(new ProductoDto.Especificacion("Potencia", "750W"), new ProductoDto.Especificacion("Certificación", "80 Plus Gold"), new ProductoDto.Especificacion("Ventilador", "120mm silencioso"), new ProductoDto.Especificacion("Protecciones", "OPP/OVP/SCP"), new ProductoDto.Especificacion("Garantía", "5 años"))),
            new ProductoDto(8L, "Silla ROG Chariot", "Silla Gamer ASUS ROG Chariot", "asus", "accesorios", 999, 10, true,
                    "Ergonomía superior para largas jornadas de juego.",
                    "Diseñada para ofrecer el máximo confort ergonómico...",
                    List.of("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTCF5A-ktfG4dyUyYQRu4WB2ip7AIZVn6_9W_faUY6NTaTzafVamDfHAKU&s=10"),
                    List.of("Ergonómica", "Espuma Viscoelástica", "Soporte Lumbar"),
                    List.of(new ProductoDto.Especificacion("Material", "Cuero sintético respirable"), new ProductoDto.Especificacion("Estructura", "Acero reforzado"), new ProductoDto.Especificacion("Pistón de gas", "Clase 4"), new ProductoDto.Especificacion("Inclinación", "Hasta 145 grados"), new ProductoDto.Especificacion("Peso máximo", "150 kg"))),
            new ProductoDto(9L, "Cooler Líquido MAG 240", "Refrigeración Líquida MSI MAG 240", "msi", "gaming", 620, null, true,
                    "Disipación líquida AIO con iluminación ARGB integrada.",
                    "Mantén las temperaturas de tu CPU bajo control...",
                    List.of("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRMNwhgAMqKC7evc7J8ByqzwCwfzUA0xuWcLlIzyDlhxT1UFz7OW55H55B8&s=10"),
                    List.of("Radiador 240mm", "ARGB", "Bomba silenciosa"),
                    List.of(new ProductoDto.Especificacion("Radiador", "Aluminio 240mm"), new ProductoDto.Especificacion("Ventiladores", "2 x 120mm ARGB"), new ProductoDto.Especificacion("Velocidad del ventilador", "500-2000 RPM"), new ProductoDto.Especificacion("Nivel de ruido", "max 34.3 dBA"), new ProductoDto.Especificacion("Sockets compatibles", "Intel / AMD")))
    );

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (repo.count() > 0) {
            return;
        }
        repo.saveAll(DATOS.stream().map(this::aEntidad).toList());
    }

    private Producto aEntidad(ProductoDto d) {
        Producto p = new Producto();
        p.setNombre(d.nombre());
        p.setNombreCompleto(d.nombreCompleto());
        p.setMarca(d.marca());
        p.setCategoria(d.categoria());
        p.setPrecio(d.precio());
        p.setDescuento(d.descuento());
        p.setStock(d.stock());
        p.setResumen(d.resumen());
        p.setDescripcion(d.descripcion());
        p.getImagenes().addAll(d.imagenes());
        p.getEtiquetas().addAll(d.etiquetas());
        d.especificaciones().forEach(e ->
                p.getEspecificaciones().add(new Especificacion(e.clave(), e.valor())));
        return p;
    }
}