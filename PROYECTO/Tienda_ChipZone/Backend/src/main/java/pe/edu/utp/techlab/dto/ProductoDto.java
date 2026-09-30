package pe.edu.utp.techlab.dto;

import java.util.List;

public record ProductoDto(
        long id,
        String nombre,
        String nombreCompleto,
        String marca,
        String categoria,
        int precio,
        Integer descuento,
        boolean stock,
        String resumen,
        String descripcion,
        List<String> imagenes,
        List<String> etiquetas,
        List<Especificacion> especificaciones) {

    /** Indica si el producto tiene un descuento mayor que cero. */
    public boolean tieneDescuento() {
        return descuento != null && descuento > 0;
    }

    /** Precio de lista menos el porcentaje de descuento, en soles enteros. */
    public int precioFinal() {
        return tieneDescuento()
                ? (int) Math.round(precio * (100 - descuento) / 100.0)
                : precio;
    }

    /** Cuanto se ahorra el cliente con el descuento. */
    public int ahorro() {
        return precio - precioFinal();
    }

    public record Especificacion(String clave, String valor) {
    }
}