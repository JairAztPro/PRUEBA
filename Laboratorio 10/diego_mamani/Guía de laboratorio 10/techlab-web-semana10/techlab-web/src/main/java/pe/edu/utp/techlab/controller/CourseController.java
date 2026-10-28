package pe.edu.utp.techlab.controller;

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
import pe.edu.utp.techlab.dto.CourseDto;
import pe.edu.utp.techlab.dto.CourseInputDto;
import pe.edu.utp.techlab.exception.CourseNotFoundException;
import pe.edu.utp.techlab.service.CourseService;

@RestController
@RequestMapping(value = "/api/v1/cursos", produces = "application/json")
public class CourseController {
    private final CourseService service;
    public CourseController(CourseService service) { this.service = service; }
    @GetMapping public List<CourseDto> listar(@RequestParam(name="q", defaultValue="") String q) { return service.buscar(q); }
    @GetMapping("/{id}") public CourseDto detalle(@PathVariable long id) { return service.buscarPorId(id).orElseThrow(() -> new CourseNotFoundException(id)); }
    @PostMapping(consumes="application/json")
    public ResponseEntity<CourseDto> crear(@RequestBody CourseInputDto input, UriComponentsBuilder uriBuilder) {
        CourseDto creado = service.crear(input);
        URI location = uriBuilder.path("/api/v1/cursos/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(creado);
    }
    @PutMapping(value="/{id}", consumes="application/json")
    public CourseDto actualizar(@PathVariable long id, @RequestBody CourseInputDto input) { return service.actualizar(id, input); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable long id) { service.eliminar(id); return ResponseEntity.noContent().build(); }
}
