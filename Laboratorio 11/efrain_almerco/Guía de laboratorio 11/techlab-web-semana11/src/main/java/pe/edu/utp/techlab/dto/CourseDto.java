package pe.edu.utp.techlab.dto;

public record CourseDto(long id, String titulo, int horas, long categoriaId, String categoriaNombre) {
    /** Compatibilidad de construcción para fixtures históricos; el contrato actual incluye categoriaId. */
    public CourseDto(long id, String titulo, int horas, String categoriaNombre) {
        this(id, titulo, horas, 0L, categoriaNombre);
    }
    /** Alias de lectura para el código antiguo de la semana 9. */
    public String categoria() { return categoriaNombre; }
}
