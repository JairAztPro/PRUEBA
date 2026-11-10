package pe.edu.utp.techlab.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Semana 13: frontera de seguridad de ChipZone. La politica de acceso se declara aqui y los
 * controladores no repiten comprobaciones de login.
 *
 * Hay dos clientes con necesidades distintas y por eso dos cadenas:
 * - API REST ({@code /api/**}): Basic en cada solicitud, sin sesion y sin CSRF. La lectura del
 *   catalogo es publica porque la tienda (carpeta Frontend) la consume con fetch sin credenciales;
 *   crear, actualizar y eliminar exigen autenticacion (401 si falta).
 * - Panel Thymeleaf ({@code /admin/**}): formulario de login, sesion y CSRF activo.
 */
@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Identidad temporal en memoria; la clave llega por variable de entorno y se guarda como hash. */
    @Bean
    UserDetailsService userDetailsService(
            @Value("${app.security.demo-username}") String username,
            @Value("${app.security.demo-password}") String rawPassword,
            PasswordEncoder encoder) {
        UserDetails user = User.builder()
                .username(username)
                .password(encoder.encode(rawPassword))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    /** Debe declararse primero (Order 1): es la cadena mas especifica. */
    @Bean
    @Order(1)
    SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos", "/api/v1/productos/*").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Solo en esta cadena: es sin estado, no usa cookies de sesion y el cliente envia Basic cada vez.
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    SecurityFilterChain webSecurity(HttpSecurity http) throws Exception {
        http
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
                        ).authenticated()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/admin/productos",
                                "/admin/productos/*"
                        ).permitAll()
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
