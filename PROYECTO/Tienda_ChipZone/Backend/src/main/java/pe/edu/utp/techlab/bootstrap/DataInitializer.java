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
            // --- PRODUCTOS ORIGINALES ---
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
                    List.of(new ProductoDto.Especificacion("Radiador", "Aluminio 240mm"), new ProductoDto.Especificacion("Ventiladores", "2 x 120mm ARGB"), new ProductoDto.Especificacion("Velocidad del ventilador", "500-2000 RPM"), new ProductoDto.Especificacion("Nivel de ruido", "max 34.3 dBA"), new ProductoDto.Especificacion("Sockets compatibles", "Intel / AMD"))),

            // --- NUEVOS PRODUCTOS: LAPTOPS (10 productos con ruta local) ---
            new ProductoDto(10L, "Laptop Mística Pro", "Laptop Mística Pro con acabado en aluminio", "asus", "laptops", 4500, 10, true,
                    "Laptop de alta gama inspirada en artes mágicas.", "Herramienta de poder para creadores.",
                    List.of("/images/laptops/laptop1.png"), List.of("OLED", "16GB"), List.of(new ProductoDto.Especificacion("Pantalla", "15.6"))),
            new ProductoDto(11L, "Ultrabook Estelar", "Ultrabook Estelar de diseño ligero", "msi", "laptops", 3800, null, true,
                    "Diseño ultra fino y portátil.", "Movilidad sin sacrificar rendimiento.",
                    List.of("/images/laptops/laptop2.png"), List.of("Ligera"), List.of(new ProductoDto.Especificacion("Peso", "1.2 kg"))),
            new ProductoDto(12L, "Laptop Dragon", "Laptop Gaming Dragon X", "asus", "laptops", 5999, 5, true,
                    "Potencia gráfica extrema.", "Para creadores y gamers exigentes.",
                    List.of("/images/laptops/laptop3.png"), List.of("RTX"), List.of(new ProductoDto.Especificacion("GPU", "RTX"))),
            new ProductoDto(13L, "Laptop Academic", "Laptop Academic Book UTP", "msi", "laptops", 2499, null, false,
                    "Ideal para estudiantes universitarios.", "Multitarea fluida.",
                    List.of("/images/laptops/laptop4.png"), List.of("Estudio"), List.of(new ProductoDto.Especificacion("Batería", "8h"))),
            new ProductoDto(14L, "Laptop Titan", "Laptop Titan X Workstation", "asus", "laptops", 7200, null, true,
                    "Rendimiento profesional extremo.", "Refrigeración líquida avanzada.",
                    List.of("/images/laptops/laptop5.png"), List.of("Pro"), List.of(new ProductoDto.Especificacion("RAM", "64GB"))),
            new ProductoDto(15L, "Laptop Z-Edition", "Laptop Z-Edition Edición Mística", "msi", "laptops", 4100, 10, true,
                    "Edición especial con teclado retroiluminado.", "Estilo único.",
                    List.of("/images/laptops/laptop6.png"), List.of("Edición Especial"), List.of(new ProductoDto.Especificacion("Teclado", "RGB"))),
            new ProductoDto(16L, "Laptop Lite 360", "Laptop Convertible Lite 360", "asus", "laptops", 2999, null, false,
                    "Pantalla táctil convertible 2 en 1.", "Versatilidad total.",
                    List.of("/images/laptops/laptop7.png"), List.of("Tactil"), List.of(new ProductoDto.Especificacion("Modo", "Tablet/Laptop"))),
            new ProductoDto(17L, "Laptop CyberBook", "Laptop CyberBook Chasis Metálico", "msi", "laptops", 3499, 15, true,
                    "Chasis metálico reforzado.", "Durabilidad garantizada.",
                    List.of("/images/laptops/laptop8.png"), List.of("Metal"), List.of(new ProductoDto.Especificacion("Chasis", "Aluminio"))),
            new ProductoDto(18L, "Laptop Creator", "Laptop Creator Studio 4K", "asus", "laptops", 6500, null, true,
                    "Optimizada para edición de video 4K.", "Diseño 3D fluido.",
                    List.of("/images/laptops/laptop9.png"), List.of("4K"), List.of(new ProductoDto.Especificacion("Color", "100% sRGB"))),
            new ProductoDto(19L, "Laptop Essential", "Laptop Essential V Conectividad Total", "msi", "laptops", 2199, null, false,
                    "Gran relación calidad-precio.", "Para el día a día.",
                    List.of("/images/laptops/laptop10.png"), List.of("Base"), List.of(new ProductoDto.Especificacion("Puertos", "Completos"))),

            // --- NUEVOS PRODUCTOS: CELULARES (10 productos con ruta local) ---
            new ProductoDto(20L, "Smartphone Lumina", "Smartphone Lumina 12 Pro", "asus", "celulares", 2899, 15, true,
                    "Cámara de alta resolución.", "Pantalla AMOLED de 120Hz.",
                    List.of("/images/celulares/celular1.png"), List.of("5G"), List.of(new ProductoDto.Especificacion("Cámara", "108MP"))),
            new ProductoDto(21L, "Smartphone Volt X", "Smartphone Volt X Carga Rápida", "msi", "celulares", 2499, null, true,
                    "Carga rápida de 120W.", "Procesador Snapdragon.",
                    List.of("/images/celulares/celular2.png"), List.of("Carga 120W"), List.of(new ProductoDto.Especificacion("Bateria", "4500mAh"))),
            new ProductoDto(22L, "Smartphone Nova", "Smartphone Nova Ultra Titanio", "asus", "celulares", 3599, 10, true,
                    "Diseño simétrico en cristal y titanio.", "Elegancia pura.",
                    List.of("/images/celulares/celular3.png"), List.of("Titanio"), List.of(new ProductoDto.Especificacion("Material", "Titanio"))),
            new ProductoDto(23L, "Smartphone Echo", "Smartphone Echo 5G Velocidad", "msi", "celulares", 1499, null, false,
                    "Conectividad ultra veloz.", "Autonomía extendida.",
                    List.of("/images/celulares/celular4.png"), List.of("Económico"), List.of(new ProductoDto.Especificacion("Red", "5G"))),
            new ProductoDto(24L, "Smartphone Pulse", "Smartphone Pulse Pro Creadores", "asus", "celulares", 3199, null, true,
                    "Enfocado en fotografía nocturna.", "Ideal para creadores.",
                    List.of("/images/celulares/celular5.png"), List.of("Nocturna"), List.of(new ProductoDto.Especificacion("Sensor", "Sony"))),
            new ProductoDto(25L, "Smartphone Zen", "Smartphone Zen Phone Fluido", "msi", "celulares", 1899, 20, true,
                    "Interfaz limpia y fluida.", "Sin bloatware.",
                    List.of("/images/celulares/celular6.png"), List.of("Clean"), List.of(new ProductoDto.Especificacion("OS", "Android Puro"))),
            new ProductoDto(26L, "Smartphone Gamer", "Smartphone Gamer Edge Háptico", "asus", "celulares", 2799, null, true,
                    "Gatillos hápticos integrados.", "Modo refrigeración.",
                    List.of("/images/celulares/celular7.png"), List.of("Gaming"), List.of(new ProductoDto.Especificacion("Trigger", "Háptico"))),
            new ProductoDto(27L, "Smartphone Mini", "Smartphone Mini S Compacto", "msi", "celulares", 1999, null, false,
                    "Compacto pero potente.", "Fácil de llevar.",
                    List.of("/images/celulares/celular8.png"), List.of("Mini"), List.of(new ProductoDto.Especificacion("Pantalla", "5.9 pulgadas"))),
            new ProductoDto(28L, "Smartphone Horizon", "Smartphone Horizon Curvo Inmersivo", "asus", "celulares", 3499, 5, true,
                    "Pantalla curva sin marcos.", "Inmersión total.",
                    List.of("/images/celulares/celular9.png"), List.of("Curvo"), List.of(new ProductoDto.Especificacion("Panel", "AMOLED Curvo"))),
            new ProductoDto(29L, "Smartphone Base", "Smartphone Base Go Resistente", "msi", "celulares", 899, null, false,
                    "Resistente a impactos.", "Batería de 5000 mAh.",
                    List.of("/images/celulares/celular10.png"), List.of("Resistente"), List.of(new ProductoDto.Especificacion("Batería", "5000mAh"))),

            // --- NUEVOS PRODUCTOS: ACCESORIOS (Para completar 10) ---
            new ProductoDto(30L, "Mouse Místico", "Mouse Óptico Místico RGB", "asus", "accesorios", 199, null, true,
                    "Sensor de alta precisión.", "Iluminación ajustable.",
                    List.of("/images/accesorios/acc2.png"), List.of("Mouse"), List.of(new ProductoDto.Especificacion("DPI", "16000"))),
            new ProductoDto(31L, "Audífonos Aura", "Audífonos Gamer Aura Surround", "msi", "accesorios", 280, 10, true,
                    "Sonido envolvente 7.1.", "Micrófono con cancelación de ruido.",
                    List.of("/images/accesorios/acc3.png"), List.of("Audio"), List.of(new ProductoDto.Especificacion("Sonido", "7.1"))),
            new ProductoDto(32L, "Alfombrilla XL", "Alfombrilla Extensa Mística", "asus", "accesorios", 99, null, false,
                    "Superficie deslizante de alta durabilidad.", "Base antideslizante.",
                    List.of("/images/accesorios/acc4.png"), List.of("Mousepad"), List.of(new ProductoDto.Especificacion("Medida", "900x400mm"))),
            new ProductoDto(33L, "Webcam Vision", "Webcam 4K Ultra HD Vision", "msi", "accesorios", 350, null, true,
                    "Resolución 4K para streaming.", "Autoenfoque inteligente.",
                    List.of("/images/accesorios/acc5.png"), List.of("Streaming"), List.of(new ProductoDto.Especificacion("Resolución", "4K"))),
            new ProductoDto(34L, "Microfono Studio", "Micrófono USB Studio Condenser", "asus", "accesorios", 290, 15, true,
                    "Captura de voz profesional.", "Filtro pop incluido.",
                    List.of("/images/accesorios/acc6.png"), List.of("Podcast"), List.of(new ProductoDto.Especificacion("Conexión", "USB-C"))),
            new ProductoDto(35L, "Hub USB-C Pro", "Hub Multiplicador USB-C Pro", "msi", "accesorios", 150, null, false,
                    "Puertos HDMI, USB 3.0 y lector SD.", "Compacto.",
                    List.of("/images/accesorios/acc7.png"), List.of("Hub"), List.of(new ProductoDto.Especificacion("Puertos", "7 en 1"))),
            new ProductoDto(36L, "Base Refrigerante", "Base Soporte Refrigerante RGB", "asus", "accesorios", 130, null, false,
                    "Dos ventiladores silenciosos.", "Altura ajustable.",
                    List.of("/images/accesorios/acc8.png"), List.of("Cooler"), List.of(new ProductoDto.Especificacion("Ventiladores", "2x"))),
            new ProductoDto(37L, "Controlador Game", "Gamepad Inalámbrico Pro", "msi", "accesorios", 220, null, true,
                    "Compatible con PC y consolas.", "Vibración dual.",
                    List.of("/images/accesorios/acc9.png"), List.of("Gamepad"), List.of(new ProductoDto.Especificacion("Conexión", "Bluetooth/Wireless"))),
            new ProductoDto(38L, "Kit Limpieza Tech", "Kit de Limpieza Especializado", "asus", "accesorios", 49, null, false,
                    "Líquido antiestático y brocha suave.", "Para pantallas y teclados.",
                    List.of("/images/accesorios/acc10.png"), List.of("Limpieza"), List.of(new ProductoDto.Especificacion("Contenido", "Spray + Paño"))),

            // --- NUEVOS PRODUCTOS: GAMING EXTRA (Para completar 10) ---
            new ProductoDto(39L, "Gabinete Cristal", "Gabinete Gamer ARGB Cristal Templado", "msi", "gaming", 399, 10, true,
                    "Excelente flujo de aire.", "Paneles de cristal templado.",
                    List.of("/images/gaming/game2.png"), List.of("Gabinete"), List.of(new ProductoDto.Especificacion("Formato", "ATX"))),
            new ProductoDto(40L, "Kit Teclado y Mouse", "Kit Esencial Gaming RGB", "asus", "gaming", 250, null, true,
                    "Combo perfecto para empezar.", "Iluminación sincronizada.",
                    List.of("/images/gaming/game3.png"), List.of("Kit"), List.of(new ProductoDto.Especificacion("Incluye", "Teclado + Mouse"))),
            new ProductoDto(41L, "Tarjeta WiFi Pro", "Tarjeta de Red PCIe WiFi 6E", "msi", "gaming", 199, null, false,
                    "Conexión inalámbrica ultrarrápida.", "Baja latencia.",
                    List.of("/images/gaming/game4.png"), List.of("Red"), List.of(new ProductoDto.Especificacion("Estándar", "WiFi 6E"))),
            new ProductoDto(42L, "SSD M.2 1TB", "Disco Sólido NVMe M.2 1TB", "asus", "gaming", 320, 15, true,
                    "Velocidades de lectura de 7000MB/s.", "Ideal para cargas rápidas.",
                    List.of("/images/gaming/game5.png"), List.of("Almacenamiento"), List.of(new ProductoDto.Especificacion("Lectura", "7000 MB/s"))),
            new ProductoDto(43L, "Memoria RAM 32GB", "Kit Memoria RAM 32GB DDR5", "msi", "gaming", 599, null, true,
                    "Alto rendimiento multitarea.", "Diseño disipador agresivo.",
                    List.of("/images/gaming/game6.png"), List.of("DDR5"), List.of(new ProductoDto.Especificacion("Frecuencia", "5600 MHz"))),
            new ProductoDto(44L, "Pasta Térmica Pro", "Compuesto Térmico de Alta Conductividad", "asus", "gaming", 45, null, false,
                    "Máxima transferencia de calor.", "Durabilidad prolongada.",
                    List.of("/images/gaming/game7.png"), List.of("Térmica"), List.of(new ProductoDto.Especificacion("Conductividad", "12.5 W/mK"))),
            new ProductoDto(45L, "Brazo para Monitor", "Soporte de Brazo Articulado VESA", "msi", "gaming", 210, null, false,
                    "Optimiza espacio en tu escritorio.", "Ajuste de altura fluido.",
                    List.of("/images/gaming/game8.png"), List.of("Soporte"), List.of(new ProductoDto.Especificacion("VESA", "75x75 / 100x100"))),
            new ProductoDto(46L, "Kit Cables Sleeved", "Extensiones de Cables mallados ARGB", "asus", "gaming", 129, null, false,
                    "Estética limpia para tu fuente de poder.", "Colores vibrantes.",
                    List.of("/images/gaming/game9.png"), List.of("Cables"), List.of(new ProductoDto.Especificacion("Certificado", "Flex"))),
            new ProductoDto(47L, "Disipador M.2 Heatsink", "Disipador de Aluminio para SSD M.2", "msi", "gaming", 55, null, false,
                    "Evita el Thermal Throttling.", "Fácil instalación.",
                    List.of("/images/gaming/game10.png"), List.of("Cooler M.2"), List.of(new ProductoDto.Especificacion("Material", "Aluminio")))
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