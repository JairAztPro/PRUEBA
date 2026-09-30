package pe.edu.utp.techlab.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ProductoVistaTests {

    @Autowired
    private MockMvc mvc;

    @Test
    void listadoRenderizaModeloYHtml() throws Exception {
        mvc.perform(get("/admin/productos"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/lista"))
                .andExpect(model().attribute("q", ""))
                .andExpect(model().attribute("productos", hasSize(9)))
                .andExpect(model().attribute("categorias", hasSize(2)))
                .andExpect(content().string(containsString("RTX 4060 Gaming")))
                .andExpect(content().string(containsString("9 producto(s) encontrado(s)")))
                .andExpect(content().string(not(containsString("th:each"))));
    }

    @Test
    void filtroConservaConsultaYReduceResultados() throws Exception {
        mvc.perform(get("/admin/productos").param("q", "ryzen"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("q", "ryzen"))
                .andExpect(model().attribute("productos", hasSize(1)))
                .andExpect(content().string(containsString("Ryzen 7 5800X3D")))
                .andExpect(content().string(not(containsString("RTX 4060 Gaming"))));

        mvc.perform(get("/admin/productos").param("categoria", "accesorios"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("productos", hasSize(4)));
    }

    @Test
    void sinResultadosMuestraEstadoVacio() throws Exception {
        mvc.perform(get("/admin/productos").param("q", "zzz"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("productos", hasSize(0)))
                .andExpect(content().string(containsString("No se encontraron productos")));
    }

    @Test
    void detalleRenderizaElProductoSolicitado() throws Exception {
        mvc.perform(get("/admin/productos/2"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/detalle"))
                .andExpect(model().attributeExists("producto"))
                .andExpect(content().string(containsString("RTX 4060 Gaming")))
                .andExpect(content().string(containsString("S/ 749")));
    }

    @Test
    void detalleConDescuentoMuestraPrecioFinalYAnterior() throws Exception {
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
                .andExpect(content().string(containsString("8 en stock")))
                .andExpect(content().string(containsString("1 sin stock")))
                .andExpect(content().string(containsString("3 con descuento")));
    }

    @Test
    void erroresConservanEstadosHttp() throws Exception {
        mvc.perform(get("/admin/productos/999"))
                .andExpect(status().isNotFound());
        mvc.perform(get("/admin/productos/abc"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/admin/productos").param("q", "x".repeat(61)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void portalRedirigeAlPanel() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/admin/productos"));
    }
}