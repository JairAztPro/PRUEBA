package pe.edu.utp.techlab.controller;

import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.utp.techlab.security.dto.LoginRequest;
import pe.edu.utp.techlab.security.dto.TokenResponse;
import pe.edu.utp.techlab.security.service.TokenService;

/**
 * Semana 15: emision de JWT. Credenciales erroneas responden 401 sin revelar si el usuario existe;
 * datos que incumplen Bean Validation responden 400. La respuesta nunca se guarda en cache.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class TokenController {

    private final TokenService tokens;

    public TokenController(TokenService tokens) {
        this.tokens = tokens;
    }

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> token(@Valid @RequestBody LoginRequest request) {
        try {
            TokenResponse response = tokens.issue(request.username(), request.password());
            return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                    .header("Pragma", "no-cache").body(response);
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .cacheControl(CacheControl.noStore())
                    .header("Pragma", "no-cache").build();
        }
    }
}
