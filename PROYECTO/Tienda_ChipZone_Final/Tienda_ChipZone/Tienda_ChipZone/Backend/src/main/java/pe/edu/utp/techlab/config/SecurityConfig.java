package pe.edu.utp.techlab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Semanas 13 a 15: frontera de seguridad de ChipZone. La politica de acceso se declara aqui y los
 * controladores no repiten comprobaciones de login ni de rol.
 *
 * <p>Dos clientes con necesidades distintas, por eso dos cadenas:
 * <ul>
 *   <li><b>API REST</b> ({@code /api/**}): sin sesion, sin cookies y sin CSRF. Se autentica solo con
 *       {@code Authorization: Bearer <JWT>}. La lectura del catalogo sigue publica porque la tienda
 *       (carpeta Frontend) la consume con fetch sin credenciales. Crear, actualizar y eliminar
 *       exigen un JWT valido con rol ADMIN. {@code POST /api/v1/auth/token} emite el JWT.</li>
 *   <li><b>Panel Thymeleaf</b> ({@code /admin/**}): formulario de login, sesion y CSRF activo.
 *       La lectura es publica; gestionar productos exige rol ADMIN.</li>
 * </ul>
 * La proteccion CSRF de la API solo esta desactivada porque no usa cookies ni Basic. Si algun dia
 * el token pasa a una cookie, hay que volver a evaluar CSRF.
 */
@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationProvider authenticationProvider(UserDetailsService users, PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(users);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    /** Debe declararse primero (Order 1): es la cadena mas especifica. */
    @Bean
    @Order(1)
    SecurityFilterChain apiSecurity(HttpSecurity http, JwtDecoder decoder,
            JwtAuthenticationConverter converter) throws Exception {
        http
                .securityMatcher("/api/**")
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/token").permitAll()
                        // Lectura publica: la consume el Frontend sin credenciales.
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos", "/api/v1/productos/*").permitAll()
                        .requestMatchers(HttpMethod.HEAD, "/api/v1/productos", "/api/v1/productos/*").permitAll()
                        // Escrituras: JWT valido con rol ADMIN. Se incluye la ruta base y la anidada para que
                        // un DELETE sin id llegue al MVC (405) en lugar de ser denegado aqui.
                        .requestMatchers(HttpMethod.POST, "/api/v1/productos", "/api/v1/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/productos", "/api/v1/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/productos", "/api/v1/productos/**").hasRole("ADMIN")
                        // Cualquier otra operacion (PATCH, OPTIONS, rutas desconocidas) se deniega.
                        .anyRequest().denyAll()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.decoder(decoder).jwtAuthenticationConverter(converter))
                )
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain webSecurity(HttpSecurity http, AuthenticationProvider provider) throws Exception {
        http
                .authenticationProvider(provider)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/",
                                "/portal",
                                "/login",
                                "/error",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/favicon.ico",
                                "/actuator/health"
                        ).permitAll()
                        // Formularios de alta y edicion: primero, porque "/admin/productos/*" tambien los cubre.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/admin/productos/nuevo",
                                "/admin/productos/*/editar"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/admin/productos",
                                "/admin/productos/",
                                "/admin/productos/*"
                        ).permitAll()
                        // Todo el resto del panel (POST de crear, actualizar y eliminar): solo ADMIN.
                        .requestMatchers("/admin/productos", "/admin/productos/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/admin/productos", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionFixation(fixation -> fixation.changeSessionId())
                )
                .csrf(Customizer.withDefaults());
        return http.build();
    }
}
