package pe.edu.utp.techlab.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.edu.utp.techlab.exception.CourseNotFoundException;
import pe.edu.utp.techlab.exception.DuplicateCourseTitleException;
import pe.edu.utp.techlab.exception.InvalidCourseDataException;

@RestControllerAdvice(assignableTypes = CourseController.class)
public class CourseApiExceptionHandler {
    @ExceptionHandler(InvalidCourseDataException.class)
    public ResponseEntity<ProblemDetail> invalidData(InvalidCourseDataException e) { return problem(HttpStatus.BAD_REQUEST, "Datos inválidos", e.getMessage()); }
    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<ProblemDetail> notFound(CourseNotFoundException e) { return problem(HttpStatus.NOT_FOUND, "Curso no encontrado", e.getMessage()); }
    @ExceptionHandler({DuplicateCourseTitleException.class, DataIntegrityViolationException.class})
    public ResponseEntity<ProblemDetail> conflict(RuntimeException e) {
        String detail = e instanceof DuplicateCourseTitleException ? e.getMessage() : "La operación incumple una restricción de la base de datos";
        return problem(HttpStatus.CONFLICT, "Conflicto de datos", detail);
    }
    private ResponseEntity<ProblemDetail> problem(HttpStatus status, String title, String detail) {
        ProblemDetail body = ProblemDetail.forStatusAndDetail(status, detail); body.setTitle(title);
        return ResponseEntity.status(status).body(body);
    }
}
