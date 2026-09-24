package pe.edu.utp.techlab; 
import org.springframework.boot.SpringApplication; 
import org.springframework.boot.autoconfigure.SpringBootApplication; 
@SpringBootApplication 
public class TechLabWebApplication { 
public static void main(String[] args) { 
SpringApplication.run(TechLabWebApplication.class, args); 
} 
} 
src/main/resources/application.properties completo 
spring.application.name=techlab-web 
server.port=8080 
management.endpoints.web.exposure.include=health 
management.endpoint.health.show-details=never 
