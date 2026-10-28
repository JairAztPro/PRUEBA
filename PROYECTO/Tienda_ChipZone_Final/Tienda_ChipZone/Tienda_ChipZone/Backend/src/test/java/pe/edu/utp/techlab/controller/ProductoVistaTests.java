package pe.edu.utp.techlab.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.utp.techlab.dto.ProductoDto;
import pe.edu.utp.techlab.dto.ProductoInputDto;
import pe.edu.utp.techlab.exception.DuplicateProductoNombreException;
import pe.edu.utp.techlab.exception.ProductoNotFoundException;
import pe.edu.utp.techlab.service.ProductoService;
import pe.edu.utp.techlab.dto.CategoriaDto;
import pe.edu.utp.techlab.exception.CategoriaNotFoundException;

@WebMvcTest({ProductoVistaController.class, PortalController.class})
class ProductoVistaTests {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ProductoService service;

    private ProductoDto rog;
    private ProductoDto rtx;
    private ProductoDto ram;

    @BeforeEach
    void prepararDatos() {
        rog = new ProductoDto(1L, "ROG Strix B550-F", "Placa Madre ASUS ROG Strix B550-F Gaming",
                "asus", "gaming", 3L, 899, 20, true, "Placa madre gaming.", "Placa de alto rendimiento.",
                List.of("https://ejemplo.test/rog.jpg"), List.of("AM4"),
                List.of(new ProductoDto.Especificacion("Socket", "AMD AM4")));
        rtx = new ProductoDto(2L, "RTX 4060 Gaming", "Tarjeta Grafica MSI RTX 4060 Gaming",
                "msi", "gaming", 3L, 749, null, true, "Tarjeta grafica.", "Ray tracing.",
                List.of("https://ejemplo.test/rtx.jpg"), List.of("DLSS 3"), List.of());
        ram = new ProductoDto(3L, "RAM DDR4 16GB", "RAM DDR4 16GB",
                "msi", "accesorios", 1L, 459, null, false, "Memoria.", "",
                List.of(), List.of(), List.of());

        given(service.buscar("", "")).willReturn(List.of(rog, rtx, ram));
        given(service.categorias()).willReturn(
        List.of(new CategoriaDto(1L, "accesorios"), new CategoriaDto(3L, "gaming")));
    }

    @Test
    void listadoRenderizaModeloYHtml() throws Exception {
        mvc.perform(get("/admin/productos"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/lista"))
                .andExpect(model().attribute("q", ""))
                .andExpect(model().attribute("productos", hasSize(3)))
                .andExpect(model().attribute("categorias", hasSize(2)))
                .andExpect(content().string(containsString("RTX 4060 Gaming")))
                .andExpect(content().string(containsString("3 producto(s) encontrado(s)")))
                .andExpect(content().string(not(containsString("th:each"))));
    }

    @Test
    void filtroConservaConsultaYReduceResultados() throws Exception {
        given(service.buscar("ryzen", "")).willReturn(List.of(rog));
        given(service.buscar("", "accesorios")).willReturn(List.of(ram));

        mvc.perform(get("/admin/productos").param("q", "ryzen"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("q", "ryzen"))
                .andExpect(model().attribute("productos", hasSize(1)))
                .andExpect(content().string(containsString("ROG Strix B550-F")))
                .andExpect(content().string(not(containsString("RTX 4060 Gaming"))));

        mvc.perform(get("/admin/productos").param("categoria", "accesorios"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("productos", hasSize(1)));
    }

    @Test
    void sinResultadosMuestraEstadoVacio() throws Exception {
        given(service.buscar("zzz", "")).willReturn(List.of());

        mvc.perform(get("/admin/productos").param("q", "zzz"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("productos", hasSize(0)))
                .andExpect(content().string(containsString("No se encontraron productos")));
    }

    @Test
    void detalleRenderizaElProductoSolicitado() throws Exception {
        given(service.buscarPorId(2L)).willReturn(Optional.of(rtx));

        mvc.perform(get("/admin/productos/2"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/detalle"))
                .andExpect(model().attribute("producto", rtx))
                .andExpect(content().string(containsString("RTX 4060 Gaming")))
                .andExpect(content().string(containsString("S/ 749")));
    }

    @Test
    void detalleConDescuentoMuestraPrecioFinalYAnterior() throws Exception {
        given(service.buscarPorId(1L)).willReturn(Optional.of(rog));

        mvc.perform(get("/admin/productos/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("S/ 719")))
                .andExpect(content().string(containsString("S/ 899")))
                .andExpect(content().string(containsString("-20%")));
    }

    @Test
    void listadoMuestraResumenDeStockYDescuentos() throws Exception {
        mvc.perform(get("/admin/productos"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("2 en stock")))
                .andExpect(content().string(containsString("1 sin stock")))
                .andExpect(content().string(containsString("1 con descuento")));
    }

    @Test
    void erroresConservanEstadosHttp() throws Exception {
        given(service.buscarPorId(999L)).willReturn(Optional.empty());

        mvc.perform(get("/admin/productos/999"))
                .andExpect(status().isNotFound());
        mvc.perform(get("/admin/productos/abc"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/admin/productos").param("q", "x".repeat(61)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listadoOfreceLasAccionesDelCrud() throws Exception {
        mvc.perform(get("/admin/productos"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/admin/productos/nuevo")))
                .andExpect(content().string(containsString("/admin/productos/1/editar")))
                .andExpect(content().string(containsString("/admin/productos/1/eliminar")))
                .andExpect(content().string(containsString("data-confirm-delete")));
    }

    @Test
    void formularioNuevoRenderizaCamposVacios() throws Exception {
        mvc.perform(get("/admin/productos/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/formulario"))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attribute("productoId", (Object) null))
                .andExpect(content().string(containsString("Registrar producto")))
                .andExpect(content().string(containsString("action=\"/admin/productos\"")));
    }

    @Test
    void formularioEditarCargaLosDatosDelProducto() throws Exception {
        given(service.buscarPorId(1L)).willReturn(Optional.of(rog));

        mvc.perform(get("/admin/productos/1/editar"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/formulario"))
                .andExpect(model().attribute("productoId", 1L))
                .andExpect(content().string(containsString("ROG Strix B550-F")))
                .andExpect(content().string(containsString("Socket: AMD AM4")))
                .andExpect(content().string(containsString("Guardar cambios")))
                .andExpect(content().string(containsString("action=\"/admin/productos/1\"")));
    }

    @Test
    void crearValidoRedirigeAlDetalleConMensajeFlash() throws Exception {
        ProductoDto creado = new ProductoDto(48L, "Mouse Nuevo", "Mouse Nuevo",
                "msi", "accesorios", 1L, 89, null, true, null, null,
                List.of(), List.of(), List.of());
        given(service.crear(any(ProductoInputDto.class))).willReturn(creado);

        mvc.perform(post("/admin/productos")
                        .param("nombre", "Mouse Nuevo")
                        .param("marca", "msi")
                        .param("categoriaId", "1")
                        .param("precio", "89"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/productos/48"))
                .andExpect(flash().attribute("mensaje", "Producto registrado correctamente"));
    }

    @Test
    void crearDuplicadoVuelveAlFormularioConErrorEnElCampoNombre() throws Exception {
        given(service.crear(any(ProductoInputDto.class)))
                .willThrow(new DuplicateProductoNombreException("ROG Strix B550-F"));

        mvc.perform(post("/admin/productos")
                        .param("nombre", "ROG Strix B550-F")
                        .param("marca", "asus")
                        .param("categoriaId", "3")
                        .param("precio", "899"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/formulario"))
                .andExpect(model().attributeHasFieldErrors("form", "nombre"))
                .andExpect(content().string(containsString("Ya existe un producto")))
                .andExpect(content().string(containsString("ROG Strix B550-F")));
    }

    @Test
    void crearConFormularioVacioMuestraErroresPorCampoSinLlamarAlServicio() throws Exception {
        mvc.perform(post("/admin/productos"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/formulario"))
                .andExpect(model().attributeHasFieldErrors("form", "nombre", "marca", "categoriaId", "precio"))
                .andExpect(content().string(containsString("El nombre es obligatorio")))
                .andExpect(content().string(containsString("La marca es obligatoria")))
                .andExpect(content().string(containsString("El precio es obligatorio")))
                .andExpect(content().string(containsString("is-invalid")))
                .andExpect(content().string(containsString("aria-invalid=\"true\"")));

        verify(service, never()).crear(any(ProductoInputDto.class));
    }

    @Test
    void crearConPrecioNoNumericoMuestraErrorEnElCampoSinLlamarAlServicio() throws Exception {
        mvc.perform(post("/admin/productos")
                        .param("nombre", "Mouse Nuevo")
                        .param("marca", "msi")
                        .param("categoriaId", "1")
                        .param("precio", "abc"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/formulario"))
                .andExpect(model().attributeHasFieldErrors("form", "precio"))
                .andExpect(content().string(containsString("El precio debe ser un número entero")));

        verify(service, never()).crear(any(ProductoInputDto.class));
    }

    @Test
    void crearConPrecioCeroYDescuentoFueraDeRangoUsaLasReglasDelDto() throws Exception {
        mvc.perform(post("/admin/productos")
                        .param("nombre", "Mouse Nuevo")
                        .param("marca", "msi")
                        .param("categoriaId", "1")
                        .param("precio", "0")
                        .param("descuento", "101"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("form", "precio", "descuento"))
                .andExpect(content().string(containsString("El precio debe estar entre 1 y 1000000")))
                .andExpect(content().string(containsString("El descuento debe estar entre 0 y 100")));

        verify(service, never()).crear(any(ProductoInputDto.class));
    }

    @Test
    void crearConImagenInvalidaYEspecificacionMalFormadaMarcaEsosCampos() throws Exception {
        mvc.perform(post("/admin/productos")
                        .param("nombre", "Mouse Nuevo")
                        .param("marca", "msi")
                        .param("categoriaId", "1")
                        .param("precio", "89")
                        .param("imagenes", "javascript:alert(1)"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("form", "imagenes"));

        mvc.perform(post("/admin/productos")
                        .param("nombre", "Mouse Nuevo")
                        .param("marca", "msi")
                        .param("categoriaId", "1")
                        .param("precio", "89")
                        .param("especificaciones", "sin separador"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("form", "especificaciones"))
                .andExpect(content().string(containsString("Clave: valor")));

        verify(service, never()).crear(any(ProductoInputDto.class));
    }

    @Test
    void erroresDeValidacionConservanLoEscritoEnElFormulario() throws Exception {
        mvc.perform(post("/admin/productos")
                        .param("nombre", "Mouse Gamer")
                        .param("marca", "msi")
                        .param("categoriaId", "1")
                        .param("precio", "0")
                        .param("resumen", "Texto que no debe perderse"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("form", "precio"))
                .andExpect(content().string(containsString("Mouse Gamer")))
                .andExpect(content().string(containsString("Texto que no debe perderse")));
    }

    @Test
    void actualizarConDatosInvalidosVuelveAlFormularioDeEdicion() throws Exception {
        mvc.perform(post("/admin/productos/2")
                        .param("nombre", "")
                        .param("marca", "msi")
                        .param("categoriaId", "3")
                        .param("precio", "749"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/formulario"))
                .andExpect(model().attribute("productoId", 2L))
                .andExpect(model().attributeHasFieldErrors("form", "nombre"))
                .andExpect(content().string(containsString("action=\"/admin/productos/2\"")));

        verify(service, never()).actualizar(eq(2L), any(ProductoInputDto.class));
    }

    @Test
    void actualizarValidoRedirigeAlDetalleConMensajeFlash() throws Exception {
        given(service.actualizar(eq(2L), any(ProductoInputDto.class))).willReturn(rtx);

        mvc.perform(post("/admin/productos/2")
                        .param("nombre", "RTX 4060 Gaming")
                        .param("marca", "msi")
                        .param("categoriaId", "3")
                        .param("precio", "749"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/productos/2"))
                .andExpect(flash().attribute("mensaje", "Producto actualizado correctamente"));
    }

    @Test
    void eliminarRedirigeAlListadoConMensajeFlash() throws Exception {
        mvc.perform(post("/admin/productos/2/eliminar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/productos"))
                .andExpect(flash().attribute("mensaje", "Producto eliminado correctamente"));

        verify(service).eliminar(2L);
    }

    @Test
    void operacionesSobreProductoInexistenteDevuelven404() throws Exception {
        given(service.buscarPorId(999L)).willReturn(Optional.empty());
        willThrow(new ProductoNotFoundException(999L)).given(service).eliminar(999L);
        willThrow(new ProductoNotFoundException(999L)).given(service)
                .actualizar(eq(999L), any(ProductoInputDto.class));

        mvc.perform(get("/admin/productos/999/editar"))
                .andExpect(status().isNotFound());
        mvc.perform(post("/admin/productos/999/eliminar"))
                .andExpect(status().isNotFound());
        mvc.perform(post("/admin/productos/999")
                        .param("nombre", "X")
                        .param("marca", "x")
                        .param("categoriaId", "1")
                        .param("precio", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void portalRedirigeAlPanel() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/admin/productos"));
    }

     @Test
    void crearConCategoriaInexistenteMarcaElCampoCategoria() throws Exception {
        given(service.crear(any(ProductoInputDto.class)))
                .willThrow(new CategoriaNotFoundException(99L));

        mvc.perform(post("/admin/productos")
                        .param("nombre", "Mouse")
                        .param("marca", "msi")
                        .param("categoriaId", "99")
                        .param("precio", "89"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/formulario"))
                .andExpect(model().attributeHasFieldErrors("form", "categoriaId"));
    }
}
