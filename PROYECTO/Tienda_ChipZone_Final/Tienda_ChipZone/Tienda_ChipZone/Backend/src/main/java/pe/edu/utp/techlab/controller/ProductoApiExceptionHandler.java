package pe.edu.utp.techlab.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.edu.utp.techlab.exception.CategoriaNotFoundException;
import pe.edu.utp.techlab.exception.DuplicateProductoNombreException;
import pe.edu.utp.techlab.exception.InvalidProductoDataException;
import pe.edu.utp.techlab.exception.ProductoNotFoundException;

/**
 * Limitado a ProductoController: devuelve application/problem+json en la API
 * sin afectar a las vistas HTML (que usan la plantilla error/4xx).
 *
 * Semana 11: los errores de Bean Validation salen como 400 con el atributo
 * {@code errors}, un objeto cuyas claves son los campos y cuyos valores son listas de mensajes.
 */
@RestControllerAdvice(assignableTypes = ProductoController.class)
public class ProductoApiExceptionHandler {

    private static final String DETALLE_VALIDACION = "La solicitud tiene campos invalidos";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> validacionDelCuerpo(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errores = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            agregar(errores, error.getField(), error.getDefaultMessage());
        }
        return problemaDeValidacion(errores);
    }

    /** Red de seguridad: validacion de metodo del servicio (por ejemplo, entrada sin cuerpo). */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> validacionDelServicio(ConstraintViolationException ex) {
        Map<String, List<String>> errores = new LinkedHashMap<>();
        for (ConstraintViolation<?> violacion : ex.getConstraintViolations()) {
            // La ruta es metodo.parametro.campo (por ejemplo crear.input.nombre): se queda el campo.
            String campo = violacion.getPropertyPath().toString().replaceFirst("^[^.]+\\.[^.]+\\.?", "");
            agregar(errores, campo.isEmpty() ? "cuerpo" : campo, violacion.getMessage());
        }
        return problemaDeValidacion(errores);
    }

    @ExceptionHandler(InvalidProductoDataException.class)
    public ResponseEntity<ProblemDetail> datosInvalidos(InvalidProductoDataException ex) {
        return problema(HttpStatus.BAD_REQUEST, "Datos invalidos", ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> cuerpoIlegible(HttpMessageNotReadableException ex) {
        return problema(HttpStatus.BAD_REQUEST, "Datos invalidos",
                "El cuerpo de la solicitud no es un JSON valido de producto");
    }

    @ExceptionHandler(ProductoNotFoundException.class)
    public ResponseEntity<ProblemDetail> noEncontrado(ProductoNotFoundException ex) {
        return problema(HttpStatus.NOT_FOUND, "Producto no encontrado", ex.getMessage());
    }

    @ExceptionHandler({DuplicateProductoNombreException.class, DataIntegrityViolationException.class})
    public ResponseEntity<ProblemDetail> conflicto(RuntimeException ex) {
        String detalle = ex instanceof DuplicateProductoNombreException
                ? ex.getMessage()
                : "La operacion incumple una restriccion de la base de datos";
        return problema(HttpStatus.CONFLICT, "Conflicto de datos", detalle);
    }

    @ExceptionHandler(CategoriaNotFoundException.class)
    public ResponseEntity<ProblemDetail> categoriaInexistente(CategoriaNotFoundException ex) {
        return problema(HttpStatus.BAD_REQUEST, "Datos invalidos", ex.getMessage());
    }

    private void agregar(Map<String, List<String>> errores, String campo, String mensaje) {
        List<String> mensajes = errores.computeIfAbsent(campo, clave -> new ArrayList<>());
        if (!mensajes.contains(mensaje)) {
            mensajes.add(mensaje);
        }
    }

    private ResponseEntity<ProblemDetail> problemaDeValidacion(Map<String, List<String>> errores) {
        ProblemDetail cuerpo = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, DETALLE_VALIDACION);
        cuerpo.setTitle("Datos invalidos");
        cuerpo.setProperty("errors", errores);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(cuerpo);
    }

    private ResponseEntity<ProblemDetail> problema(HttpStatus estado, String titulo, String detalle) {
        ProblemDetail cuerpo = ProblemDetail.forStatusAndDetail(estado, detalle);
        cuerpo.setTitle(titulo);
        return ResponseEntity.status(estado)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(cuerpo);
    }
}