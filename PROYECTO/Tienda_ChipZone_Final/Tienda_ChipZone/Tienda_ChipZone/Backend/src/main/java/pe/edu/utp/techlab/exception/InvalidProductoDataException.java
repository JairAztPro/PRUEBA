package pe.edu.utp.techlab.exception;

/** Datos de entrada que incumplen una regla de negocio (HTTP 400). */
public class InvalidProductoDataException extends RuntimeException {

    public InvalidProductoDataException(String message) {
        super(message);
    }
}
