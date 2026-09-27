package com.example.PROYECTO.SPRING;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    // Redirige la ruta raíz ("/") al inicio
    @GetMapping("/")
    public String index() {
        return "inicio"; 
    }

    @GetMapping("/inicio")
    public String inicio() {
        return "inicio"; // Busca inicio.html
    }

    @GetMapping("/catalogo")
    public String catalogo() {
        return "catalogo"; // Busca catalogo.html
    }

    @GetMapping("/carrito")
    public String carrito() {
        return "carrito"; // Busca carrito.html
    }

    @GetMapping("/mi-cuenta")
    public String miCuenta() {
        return "mi_cuenta"; // Busca mi_cuenta.html
    }
}