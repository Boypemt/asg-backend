package th.camt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The web service.
 *
 * @EntityScan is needed because the entities live in the OTHER Maven module,
 * in package th.camt.domain. @SpringBootApplication only scans below its own
 * package, and Hibernate would otherwise never find them - the error is
 * "Not a managed type: class th.camt.domain.Customer".
 *
 * The repositories need no such line: they are in th.camt.repository, which is
 * already below th.camt.
 */
@SpringBootApplication
@EntityScan(basePackages = { "th.camt.domain" })
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    /** Lets a web page served from port 8081 call this service on port 8080. */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:8081")
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
            }
        };
    }
}
