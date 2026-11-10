package pe.edu.utp.techlab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Solo muestra la vista. El POST /login lo procesa UsernamePasswordAuthenticationFilter,
 * por eso los campos del formulario se llaman username y password.
 */
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
}
