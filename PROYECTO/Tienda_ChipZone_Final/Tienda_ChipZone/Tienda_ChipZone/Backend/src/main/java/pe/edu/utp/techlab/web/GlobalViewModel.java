package pe.edu.utp.techlab.web;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Convierte Authentication en el modelo "security" que usan las plantillas. */
@ControllerAdvice
public class GlobalViewModel {

    @ModelAttribute("security")
    public SecurityView security(Authentication authentication) {
        boolean authenticated = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
        if (!authenticated) {
            return SecurityView.anonymous();
        }
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return new SecurityView(true, authentication.getName(), admin);
    }
}
