package pe.edu.utp.techlab.exception;

/** El producto solicitado no existe (HTTP 404). */
public class ProductoNotFoundException extends RuntimeException {

    public ProductoNotFoundException(long id) {
        super("No existe un producto con el identificador " + id);
    }
}
