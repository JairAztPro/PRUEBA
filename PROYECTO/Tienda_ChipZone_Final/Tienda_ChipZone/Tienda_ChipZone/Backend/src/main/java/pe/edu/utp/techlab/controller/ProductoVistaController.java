package pe.edu.utp.techlab.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.techlab.dto.ProductoDto;
import pe.edu.utp.techlab.dto.ProductoFormDto;
import pe.edu.utp.techlab.dto.ProductoInputDto;
import pe.edu.utp.techlab.exception.DuplicateProductoNombreException;
import pe.edu.utp.techlab.exception.InvalidProductoDataException;
import pe.edu.utp.techlab.exception.ProductoNotFoundException;
import pe.edu.utp.techlab.service.ProductoService;
import pe.edu.utp.techlab.exception.CategoriaNotFoundException;

/**
 * Panel de productos generado en el servidor con Thymeleaf: listado, detalle y CRUD
 * (Semana 10) con el patron Post-Redirect-Get y mensajes flash.
 * Semana 11: el formulario se valida con Bean Validation ({@code @Valid} + {@link BindingResult})
 * y cada error se muestra junto a su campo sin perder lo escrito.
 * La tienda para el cliente esta en Frontend y usa la API de ProductoController.
 * Ambos controladores comparten ProductoService.
 */
@Controller
@RequestMapping("/admin/productos")
public class ProductoVistaController {

    private static final int MAX_CONSULTA = 60;
    private static final String VISTA_FORMULARIO = "productos/formulario";
    private static final String MENSAJE_GENERAL = "Revise los datos ingresados";

    private final ProductoService service;
    private final Validator validator;

    public ProductoVistaController(ProductoService service, Validator validator) {
        this.service = service;
        this.validator = validator;
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

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        return mostrarFormulario(new ProductoFormDto(), null, null, model);
    }

       /** BindingResult va justo despues del parametro validado; si no, Spring lanza la excepcion. */
    @PostMapping
    public String crear(
            @Valid @ModelAttribute("form") ProductoFormDto form,
            BindingResult binding,
            Model model,
            RedirectAttributes redirect) {
        ProductoInputDto input = convertirYValidar(form, binding);
        if (binding.hasErrors()) {
            return mostrarFormulario(form, null, null, model);
        }
        try {
            ProductoDto creado = service.crear(input);
            redirect.addFlashAttribute("mensaje", "Producto registrado correctamente");
            return "redirect:/admin/productos/" + creado.id();
        } catch (DuplicateProductoNombreException ex) {
            binding.rejectValue("nombre", "producto.nombre.duplicado", ex.getMessage());
            return mostrarFormulario(form, null, null, model);
        } catch (CategoriaNotFoundException ex) {
            binding.rejectValue("categoriaId", "producto.categoria.inexistente", ex.getMessage());
            return mostrarFormulario(form, null, null, model);
        } catch (InvalidProductoDataException ex) {
            return mostrarFormulario(form, null, ex.getMessage(), model);
        } catch (ConstraintViolationException ex) {
            return mostrarFormulario(form, null, MENSAJE_GENERAL, model);
        }
    }
    @GetMapping("/{id}")
    public String detalle(@PathVariable long id, Model model) {
        ProductoDto producto = service.buscarPorId(id).orElseThrow(() -> noEncontrado(id));
        model.addAttribute("producto", producto);
        return "productos/detalle";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable long id, Model model) {
        ProductoDto producto = service.buscarPorId(id).orElseThrow(() -> noEncontrado(id));
        return mostrarFormulario(ProductoFormDto.desde(producto), id, null, model);
    }

        @PostMapping("/{id}")
    public String actualizar(
            @PathVariable long id,
            @Valid @ModelAttribute("form") ProductoFormDto form,
            BindingResult binding,
            Model model,
            RedirectAttributes redirect) {
        ProductoInputDto input = convertirYValidar(form, binding);
        if (binding.hasErrors()) {
            return mostrarFormulario(form, id, null, model);
        }
        try {
            service.actualizar(id, input);
            redirect.addFlashAttribute("mensaje", "Producto actualizado correctamente");
            return "redirect:/admin/productos/" + id;
        } catch (DuplicateProductoNombreException ex) {
            binding.rejectValue("nombre", "producto.nombre.duplicado", ex.getMessage());
            return mostrarFormulario(form, id, null, model);
        } catch (CategoriaNotFoundException ex) {
            binding.rejectValue("categoriaId", "producto.categoria.inexistente", ex.getMessage());
            return mostrarFormulario(form, id, null, model);
        } catch (InvalidProductoDataException ex) {
            return mostrarFormulario(form, id, ex.getMessage(), model);
        } catch (ConstraintViolationException ex) {
            return mostrarFormulario(form, id, MENSAJE_GENERAL, model);
        } catch (ProductoNotFoundException ex) {
            throw noEncontrado(id);
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable long id, RedirectAttributes redirect) {
        try {
            service.eliminar(id);
            redirect.addFlashAttribute("mensaje", "Producto eliminado correctamente");
            return "redirect:/admin/productos";
        } catch (ProductoNotFoundException ex) {
            throw noEncontrado(id);
        }
    }

    /**
     * Convierte el formulario (todo texto) a la entrada del servicio y le aplica las reglas de
     * {@link ProductoInputDto} (rangos, listas, URL). Cada violacion se agrega al BindingResult
     * en su campo del formulario, salvo que ese campo ya tenga un error de {@code @Valid}.
     * Devuelve null si el formulario no se pudo convertir.
     */
    private ProductoInputDto convertirYValidar(ProductoFormDto form, BindingResult binding) {
        ProductoInputDto input;
        try {
            input = form.toInput();
        } catch (InvalidProductoDataException ex) {
            // Un precio o descuento mal escrito ya tiene su error de campo (@Pattern del formulario);
            // si no lo tiene, lo que fallo es el formato "Clave: valor" de las especificaciones.
            if (!binding.hasFieldErrors("precio") && !binding.hasFieldErrors("descuento")) {
                binding.rejectValue("especificaciones", "producto.especificaciones.formato", ex.getMessage());
            }
            return null;
        }
        Map<String, Set<String>> porCampo = new LinkedHashMap<>();
        for (ConstraintViolation<ProductoInputDto> violacion : validator.validate(input)) {
            // El primer nodo de la ruta es el campo, tambien en elementos de lista (imagenes[0]).
            String campo = violacion.getPropertyPath().iterator().next().getName();
            porCampo.computeIfAbsent(campo, c -> new LinkedHashSet<>()).add(violacion.getMessage());
        }
        porCampo.forEach((campo, mensajes) -> {
            if (!binding.hasFieldErrors(campo)) {
                mensajes.forEach(mensaje -> binding.rejectValue(campo, "producto.invalido", mensaje));
            }
        });
        return input;
    }

    private String mostrarFormulario(ProductoFormDto form, Long productoId, String error, Model model) {
    model.addAttribute("form", form);
    model.addAttribute("productoId", productoId);
    model.addAttribute("categorias", service.categorias());  
    if (error != null) {
        model.addAttribute("error", error);
    }
    return VISTA_FORMULARIO;
}

    private String validar(String texto) {
        String limpio = texto.strip();
        if (limpio.length() > MAX_CONSULTA) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La busqueda admite hasta 60 caracteres");
        }
        return limpio;
    }

    private ResponseStatusException noEncontrado(long id) {
        return new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Producto no encontrado");
    }
}
