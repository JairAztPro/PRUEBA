package pe.edu.utp.techlab.security.dto;

/** Nombres en snake_case a proposito: es el formato habitual de una respuesta de token. */
public record TokenResponse(
        String access_token,
        String token_type,
        long expires_in
) {
}
