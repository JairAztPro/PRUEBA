package pe.edu.utp.techlab.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.utp.techlab.dto.ProductoDto;
import pe.edu.utp.techlab.service.ProductoService;

@RestController
@RequestMapping(value = "/api/v1/productos", produces = "application/json")
public class ProductoController {

    private static final int MAX_CONSULTA = 60;

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductoDto> listar(
            @RequestParam(name = "q", defaultValue = "") String q,
            @RequestParam(name = "categoria", defaultValue = "") String categoria) {
        if (q.length() > MAX_CONSULTA || categoria.length() > MAX_CONSULTA) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Consulta demasiado larga");
        }
        return service.buscar(q, categoria);
    }

    @GetMapping("/{id}")
    public ProductoDto detalle(@PathVariable("id") long id) {
        return service.buscarPorId(id).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }
}
