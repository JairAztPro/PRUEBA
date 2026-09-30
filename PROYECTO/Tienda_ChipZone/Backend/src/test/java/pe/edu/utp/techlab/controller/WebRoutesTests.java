package pe.edu.utp.techlab.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class WebRoutesTests {

    @Autowired
    private MockMvc mvc;

    @Test
    void listadoYFiltrosFuncionan() throws Exception {
        mvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(9));

        mvc.perform(get("/api/v1/productos").param("q", "RYZEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(4));

        mvc.perform(get("/api/v1/productos").param("categoria", "gaming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void busquedaSinCoincidenciasEsVaciaYNoError() throws Exception {
        mvc.perform(get("/api/v1/productos").param("q", "ZZZ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void detalleYErroresUsanEstadosHttp() throws Exception {
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
        mvc.perform(post("/api/v1/productos"))
                .andExpect(status().isMethodNotAllowed());

        mvc.perform(get("/portal"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/admin/productos"));
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
}