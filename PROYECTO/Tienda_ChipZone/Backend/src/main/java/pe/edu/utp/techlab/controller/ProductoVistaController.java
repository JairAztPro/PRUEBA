package pe.edu.utp.techlab.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.utp.techlab.dto.ProductoDto;
import pe.edu.utp.techlab.service.ProductoService;

/**
 * Panel de productos generado en el servidor con Thymeleaf (solo lectura).
 * La tienda para el cliente esta en FRONT-END y usa la API de ProductoController.
 * Ambos controladores comparten ProductoService.
 */
@Controller
@RequestMapping("/admin/productos")
public class ProductoVistaController {

    private static final int MAX_CONSULTA = 60;

    private final ProductoService service;

    public ProductoVistaController(ProductoService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(
            @RequestParam(name = "q", defaultValue = "") String q,
            @RequestParam(name = "categoria", defaultValue = "") String categoria,
            Model model) {
        String consulta = validar(q);
        String cat = validar(categoria);
        model.addAttribute("productos", service.buscar(consulta, cat));
        model.addAttribute("categorias", service.categorias());
        model.addAttribute("q", consulta);
        model.addAttribute("categoria", cat);
        return "productos/lista";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable long id, Model model) {
        ProductoDto producto = service.buscarPorId(id).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Producto no encontrado"));
        model.addAttribute("producto", producto);
        return "productos/detalle";
    }

    private String validar(String texto) {
        String limpio = texto.strip();
        if (limpio.length() > MAX_CONSULTA) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La busqueda admite hasta 60 caracteres");
        }
        return limpio;
    }
}
