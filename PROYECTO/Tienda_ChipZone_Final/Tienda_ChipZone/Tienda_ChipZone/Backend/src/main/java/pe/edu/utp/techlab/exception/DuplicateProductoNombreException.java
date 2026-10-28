package pe.edu.utp.techlab.exception;

/** Ya existe otro producto con el mismo nombre (HTTP 409). */
public class DuplicateProductoNombreException extends RuntimeException {

    public DuplicateProductoNombreException(String nombre) {
        super("Ya existe un producto con el nombre " + nombre);
    }
}
