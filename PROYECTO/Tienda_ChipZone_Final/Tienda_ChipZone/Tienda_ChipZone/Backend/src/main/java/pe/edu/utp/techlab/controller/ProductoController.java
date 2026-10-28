package pe.edu.utp.techlab.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import pe.edu.utp.techlab.dto.ProductoDto;
import pe.edu.utp.techlab.dto.ProductoInputDto;
import pe.edu.utp.techlab.exception.InvalidProductoDataException;
import pe.edu.utp.techlab.exception.ProductoNotFoundException;
import pe.edu.utp.techlab.service.ProductoService;

/**
 * API REST de productos. Los errores se traducen a application/problem+json
 * en {@link ProductoApiExceptionHandler}. Semana 11: {@code @Valid} aplica Bean Validation
 * al cuerpo de POST y PUT antes de llegar al servicio (400 con errores por campo).
 */
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
            throw new InvalidProductoDataException("Consulta demasiado larga");
        }
        return service.buscar(q, categoria);
    }

    @GetMapping("/{id}")
    public ProductoDto detalle(@PathVariable("id") long id) {
        return service.buscarPorId(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
    }

    @PostMapping
    public ResponseEntity<ProductoDto> crear(
            @Valid @RequestBody ProductoInputDto input,
            UriComponentsBuilder uriBuilder) {
        ProductoDto creado = service.crear(input);
        URI location = uriBuilder.path("/api/v1/productos/{id}")
                .buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public ProductoDto actualizar(
            @PathVariable("id") long id,
            @Valid @RequestBody ProductoInputDto input) {
        return service.actualizar(id, input);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
