package pe.edu.utp.techlab.exception;

/** La categoría indicada no existe (HTTP 400 en la API; error de campo en el formulario). */
public class CategoriaNotFoundException extends RuntimeException {

    public CategoriaNotFoundException(long id) {
        super("No existe una categoria con el identificador " + id);
    }
}