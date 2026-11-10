package pe.edu.utp.techlab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Reto semana 13: pagina privada "Mi sesion". Sin reglas propias: queda protegida por
 * anyRequest().authenticated() y el nombre llega por el modelo global "security".
 */
@Controller
public class PerfilController {

    @GetMapping("/perfil")
    public String perfil() {
        return "auth/perfil";
    }
}
