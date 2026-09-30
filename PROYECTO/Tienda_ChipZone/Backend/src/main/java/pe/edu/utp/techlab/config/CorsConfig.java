package pe.edu.utp.techlab.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * El frontend (carpeta FRONT-END) se sirve desde otro origen, por ejemplo Live Server.
 * Solo se permiten los origenes listados en techlab.cors.origins y solo lectura (GET).
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String[] origenes;

    public CorsConfig(
            @Value("${techlab.cors.origins:http://localhost:5500,http://127.0.0.1:5500}")
            String[] origenes) {
        this.origenes = origenes;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(origenes)
                .allowedMethods("GET");
    }
}
