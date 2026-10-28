package pe.edu.utp.techlab.controller;

import jakarta.validation.Valid;
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
import pe.edu.utp.techlab.dto.CourseDto;
import pe.edu.utp.techlab.dto.CourseInputDto;
import pe.edu.utp.techlab.exception.CategoryNotFoundException;
import pe.edu.utp.techlab.exception.CourseNotFoundException;
import pe.edu.utp.techlab.exception.DuplicateCourseTitleException;
import pe.edu.utp.techlab.service.CourseService;

@Controller
@RequestMapping("/cursos")
public class CourseViewController {
    private static final int MAX_QUERY_LENGTH = 60;
    private final CourseService service;
    public CourseViewController(CourseService service) { this.service = service; }

    @GetMapping
    public String listar(@RequestParam(defaultValue="") String q, @RequestParam(required=false) String minHoras,
            @RequestParam(required=false) Long categoriaId, Model model) {
        String consulta = normalizar(q); String horas = minHoras == null ? "" : minHoras.strip();
        Integer min = validarMinHoras(horas);
        try {
            model.addAttribute("cursos", service.buscar(consulta, min, categoriaId));
        } catch (CategoryNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        model.addAttribute("q", consulta); model.addAttribute("minHoras", horas);
        model.addAttribute("categoriaId", categoriaId); model.addAttribute("categorias", service.listarCategorias());
        return "cursos/lista";
    }
    @GetMapping("/resumen") public String resumen(Model model) {
        model.addAttribute("cantidadCursos", service.cantidad()); model.addAttribute("totalHoras", service.totalHoras()); return "cursos/resumen";
    }
    @GetMapping("/{id}") public String detalle(@PathVariable long id, Model model) {
        CourseDto c = service.buscarPorId(id).orElseThrow(() -> notFound(id)); model.addAttribute("curso", c); return "cursos/detalle";
    }
    @GetMapping("/nuevo") public String nuevo(Model model) { return mostrarFormulario(new CourseInputDto(), null, "Nuevo curso", "Registrar curso", model); }

    @PostMapping
    public String crear(@Valid @ModelAttribute("form") CourseInputDto form, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) return mostrarFormulario(form, null, "Nuevo curso", "Registrar curso", model);
        try {
            CourseDto creado = service.crear(form); redirect.addFlashAttribute("mensaje", "Curso registrado correctamente");
            return "redirect:/cursos/" + creado.id();
        } catch (DuplicateCourseTitleException e) { result.rejectValue("titulo", "course.titulo.duplicado", e.getMessage()); }
          catch (CategoryNotFoundException e) { result.rejectValue("categoriaId", "course.categoria.inexistente", e.getMessage()); }
        return mostrarFormulario(form, null, "Nuevo curso", "Registrar curso", model);
    }

    @GetMapping("/{id}/editar") public String editar(@PathVariable long id, Model model) {
        CourseDto c = service.buscarPorId(id).orElseThrow(() -> notFound(id));
        return mostrarFormulario(new CourseInputDto(c.titulo(), c.horas(), c.categoriaId()), id, "Editar curso", "Guardar cambios", model);
    }
    @PostMapping("/{id}")
    public String actualizar(@PathVariable long id, @Valid @ModelAttribute("form") CourseInputDto form, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) return mostrarFormulario(form, id, "Editar curso", "Guardar cambios", model);
        try {
            service.actualizar(id, form); redirect.addFlashAttribute("mensaje", "Curso actualizado correctamente"); return "redirect:/cursos/" + id;
        } catch (DuplicateCourseTitleException e) { result.rejectValue("titulo", "course.titulo.duplicado", e.getMessage()); }
          catch (CategoryNotFoundException e) { result.rejectValue("categoriaId", "course.categoria.inexistente", e.getMessage()); }
          catch (CourseNotFoundException e) { throw notFound(id); }
        return mostrarFormulario(form, id, "Editar curso", "Guardar cambios", model);
    }
    @PostMapping("/{id}/eliminar") public String eliminar(@PathVariable long id, RedirectAttributes redirect) {
        try { service.eliminar(id); redirect.addFlashAttribute("mensaje", "Curso eliminado correctamente"); return "redirect:/cursos"; }
        catch (CourseNotFoundException e) { throw notFound(id); }
    }
    private String mostrarFormulario(CourseInputDto form, Long id, String titulo, String boton, Model model) {
        model.addAttribute("form", form); model.addAttribute("cursoId", id); model.addAttribute("tituloPagina", titulo);
        model.addAttribute("textoBoton", boton); model.addAttribute("categorias", service.listarCategorias()); return "cursos/formulario";
    }
    private String normalizar(String q) { String c = q.strip(); if (c.length() > MAX_QUERY_LENGTH) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La búsqueda admite hasta 60 caracteres"); return c; }
    private Integer validarMinHoras(String texto) {
        if (texto.isEmpty()) return null; int n;
        try { n = Integer.parseInt(texto); } catch (NumberFormatException e) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minHoras debe ser un entero"); }
        if (n < 0 || n > 200) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minHoras debe estar entre 0 y 200"); return n;
    }
    private ResponseStatusException notFound(long id) { return new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe un curso con el identificador " + id); }
}
