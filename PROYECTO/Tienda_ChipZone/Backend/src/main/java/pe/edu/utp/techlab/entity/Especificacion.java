package pe.edu.utp.techlab.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public record Especificacion(String clave, String valor) {
}