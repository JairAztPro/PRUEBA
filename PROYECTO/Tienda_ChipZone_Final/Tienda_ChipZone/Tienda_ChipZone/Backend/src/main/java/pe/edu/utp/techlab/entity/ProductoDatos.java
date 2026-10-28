package pe.edu.utp.techlab.entity;

import java.util.List;

/**
 * Datos ya validados y normalizados que el servicio entrega a la entidad
 * para crear o actualizar un producto. Evita metodos de dominio con 12 parametros.
 */
public record ProductoDatos(
        String nombre,
        String nombreCompleto,
        String marca,
        Categoria categoria,
        int precio,
        Integer descuento,
        boolean stock,
        String resumen,
        String descripcion,
        List<String> imagenes,
        List<String> etiquetas,
        List<Especificacion> especificaciones) {
}
