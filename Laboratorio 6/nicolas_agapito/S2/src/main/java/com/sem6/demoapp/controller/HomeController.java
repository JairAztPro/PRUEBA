package com.sem6.demoapp.controller;

import com.sem6.demoapp.model.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("usuarioNombre", "Carlos");
        return "home"; // Renderiza home.html
    }

    @GetMapping("/formulario")
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "formulario";
    }

    @PostMapping("/procesar")
    public String procesarFormulario(@ModelAttribute Usuario usuario, Model model) {
        model.addAttribute("usuario", usuario);
        return "resultado";
    }
}
