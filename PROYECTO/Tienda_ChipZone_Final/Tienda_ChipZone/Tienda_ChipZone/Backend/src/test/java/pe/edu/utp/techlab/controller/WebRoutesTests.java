package pe.edu.utp.techlab.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.utp.techlab.dto.ProductoDto;
import pe.edu.utp.techlab.dto.ProductoInputDto;
import pe.edu.utp.techlab.exception.DuplicateProductoNombreException;
import pe.edu.utp.techlab.exception.InvalidProductoDataException;
import pe.edu.utp.techlab.exception.ProductoNotFoundException;
import pe.edu.utp.techlab.service.ProductoService;
import pe.edu.utp.techlab.exception.CategoriaNotFoundException;

@WebMvcTest({ProductoController.class, PortalController.class})
class WebRoutesTests {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ProductoService service;

    private static final String JSON_PRODUCTO = """
            {"nombre":"Mouse Nuevo","marca":"msi","categoriaId":1,"precio":89,"stock":true}
            """;

    private ProductoDto rtx;
    private ProductoDto ryzen;

    @BeforeEach
    void prepararDatos() {
        rtx = new ProductoDto(2L, "RTX 4060 Gaming", "Tarjeta Grafica MSI RTX 4060 Gaming",
                "msi", "gaming", 3L, 749, null, true, "Tarjeta grafica.", "Ray tracing.",
                List.of(), List.of(), List.of());
        ryzen = new ProductoDto(4L, "Ryzen 7 5800X3D", "Procesador AMD Ryzen 7 5800X3D",
                "amd", "gaming", 3L, 1299, null, true, "Procesador.", "Cache 3D.",
                List.of(), List.of(), List.of());

        given(service.buscar("", "")).willReturn(List.of(rtx, ryzen));
    }

    @Test
    void listadoYFiltrosFuncionan() throws Exception {
        given(service.buscar("RYZEN", "")).willReturn(List.of(ryzen));
        given(service.buscar("", "gaming")).willReturn(List.of(rtx, ryzen));

        mvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mvc.perform(get("/api/v1/productos").param("q", "RYZEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(4))
                .andExpect(jsonPath("$[0].categoria").value("gaming"));

        mvc.perform(get("/api/v1/productos").param("categoria", "gaming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void busquedaSinCoincidenciasEsVaciaYNoError() throws Exception {
        given(service.buscar("ZZZ", "")).willReturn(List.of());

        mvc.perform(get("/api/v1/productos").param("q", "ZZZ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void detalleYErroresUsanEstadosHttp() throws Exception {
        given(service.buscarPorId(2L)).willReturn(Optional.of(rtx));
        given(service.buscarPorId(999L)).willReturn(Optional.empty());

        mvc.perform(get("/api/v1/productos/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("RTX 4060 Gaming"));

        mvc.perform(get("/api/v1/productos/999"))
                .andExpect(status().isNotFound());

        mvc.perform(get("/api/v1/productos/abc"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/api/v1/productos").param("q", "x".repeat(61)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void metodoNoPermitidoYRedireccion() throws Exception {
        mvc.perform(delete("/api/v1/productos"))
                .andExpect(status().isMethodNotAllowed());

        mvc.perform(get("/portal"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/admin/productos"));
    }

    @Test
    void postCreaProductoConEstado201YLocation() throws Exception {
        ProductoDto creado = new ProductoDto(48L, "Mouse Nuevo", "Mouse Nuevo",
                "msi", "accesorios", 1L, 89, null, true, null, null,
                List.of(), List.of(), List.of());
        given(service.crear(any(ProductoInputDto.class))).willReturn(creado);

        mvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_PRODUCTO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/productos/48"))
                .andExpect(jsonPath("$.id").value(48))
                .andExpect(jsonPath("$.nombre").value("Mouse Nuevo"));
    }

    @Test
    void putActualizaProductoConEstado200() throws Exception {
        given(service.actualizar(eq(2L), any(ProductoInputDto.class))).willReturn(rtx);

        mvc.perform(put("/api/v1/productos/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_PRODUCTO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void deleteEliminaProductoConEstado204() throws Exception {
        mvc.perform(delete("/api/v1/productos/2"))
                .andExpect(status().isNoContent());

        verify(service).eliminar(2L);
    }

    @Test
    void datosInvalidosDevuelven400ComoProblemJson() throws Exception {
        given(service.crear(any(ProductoInputDto.class)))
                .willThrow(new InvalidProductoDataException("El nombre es obligatorio"));

        mvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_PRODUCTO))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Datos invalidos"))
                .andExpect(jsonPath("$.detail").value("El nombre es obligatorio"));
    }

    @Test
    void cuerpoSinCamposObligatoriosDevuelve400ConErroresPorCampo() throws Exception {
        mvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Datos invalidos"))
                .andExpect(jsonPath("$.errors.nombre[0]").value("El nombre es obligatorio"))
                .andExpect(jsonPath("$.errors.marca[0]").value("La marca es obligatoria"))
                .andExpect(jsonPath("$.errors.categoriaId").isArray())
                .andExpect(jsonPath("$.errors.precio[0]").value("El precio es obligatorio"))
                .andExpect(jsonPath("$.errors.stock[0]").value("El stock es obligatorio"));

        verify(service, never()).crear(any(ProductoInputDto.class));
    }

    @Test
    void rangosYListasInvalidasDevuelven400ConUnMensajePorCampo() throws Exception {
        String json = """
                {"nombre":"Mouse","marca":"msi","categoriaId":1,"precio":0,
                 "descuento":101,"stock":true,"imagenes":["javascript:alert(1)"]}
                """;

        mvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errors.precio[0]").value("El precio debe estar entre 1 y 1000000"))
                .andExpect(jsonPath("$.errors.descuento[0]").value("El descuento debe estar entre 0 y 100"))
                .andExpect(jsonPath("$.errors['imagenes[0]'][0]").exists())
                .andExpect(jsonPath("$.errors.nombre").doesNotExist());

        verify(service, never()).crear(any(ProductoInputDto.class));
    }

    @Test
    void putConDatosInvalidosDevuelve400SinLlamarAlServicio() throws Exception {
        mvc.perform(put("/api/v1/productos/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\",\"marca\":\"msi\",\"categoriaId\":3,"
                                + "\"precio\":749,\"stock\":true}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errors.nombre[0]").value("El nombre es obligatorio"));

        verify(service, never()).actualizar(eq(2L), any(ProductoInputDto.class));
    }

    @Test
    void cuerpoQueNoEsJsonDevuelve400ComoProblemJson() throws Exception {
        mvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("esto no es json"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    void nombreDuplicadoDevuelve409ComoProblemJson() throws Exception {
        given(service.crear(any(ProductoInputDto.class)))
                .willThrow(new DuplicateProductoNombreException("Mouse Nuevo"));

        mvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_PRODUCTO))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Conflicto de datos"));
    }

    @Test
    void actualizarOEliminarInexistenteDevuelve404ComoProblemJson() throws Exception {
        given(service.actualizar(eq(999L), any(ProductoInputDto.class)))
                .willThrow(new ProductoNotFoundException(999L));
        willThrow(new ProductoNotFoundException(999L)).given(service).eliminar(999L);

        mvc.perform(put("/api/v1/productos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_PRODUCTO))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));

        mvc.perform(delete("/api/v1/productos/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    void corsPermiteSoloElOrigenDelFrontend() throws Exception {
        mvc.perform(get("/api/v1/productos")
                        .header("Origin", "http://localhost:5500"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Access-Control-Allow-Origin", "http://localhost:5500"));

        mvc.perform(get("/api/v1/productos")
                        .header("Origin", "http://sitio-desconocido.example"))
                .andExpect(status().isForbidden());
    }

        @Test
    void categoriaInexistenteDevuelve400ComoProblemJson() throws Exception {
        given(service.crear(any(ProductoInputDto.class)))
                .willThrow(new CategoriaNotFoundException(99L));

        mvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Mouse\",\"marca\":\"msi\",\"categoriaId\":99,\"precio\":89,\"stock\":true}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }
}
