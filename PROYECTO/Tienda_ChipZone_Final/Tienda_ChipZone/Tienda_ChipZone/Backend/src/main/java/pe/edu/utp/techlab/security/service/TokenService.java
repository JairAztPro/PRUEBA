package pe.edu.utp.techlab.security.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import pe.edu.utp.techlab.security.dto.TokenResponse;

/**
 * Comprueba usuario y clave con el DaoAuthenticationProvider de la semana 14 (BD + BCrypt) y,
 * si son validos y la cuenta esta habilitada, emite un JWT de 600 segundos. El claim "roles" es
 * una fotografia del momento de emision: un token ya emitido sigue valido hasta vencer aunque
 * despues se deshabilite la cuenta. No registrar nunca clave ni token.
 */
@Service
public class TokenService {

    private static final long TTL_SECONDS = 600;

    private final AuthenticationProvider provider;
    private final JwtEncoder encoder;
    private final String issuer;
    private final String audience;

    public TokenService(AuthenticationProvider provider, JwtEncoder encoder,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.audience}") String audience) {
        this.provider = provider;
        this.encoder = encoder;
        this.issuer = issuer;
        this.audience = audience;
    }

    public TokenResponse issue(String username, String password) {
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        Authentication request = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        Authentication auth = provider.authenticate(request);
        if (auth == null || !auth.isAuthenticated()) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(r -> r.equals("ROLE_USER") || r.equals("ROLE_ADMIN"))
                .toList();
        if (roles.isEmpty()) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(auth.getName())
                .audience(List.of(audience))
                .issuedAt(now)
                .expiresAt(now.plusSeconds(TTL_SECONDS))
                .id(UUID.randomUUID().toString())
                .claim("roles", roles)
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new TokenResponse(token, "Bearer", TTL_SECONDS);
    }
}
