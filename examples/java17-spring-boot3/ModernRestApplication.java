// Application Configuration for Java 17/Spring Boot 3.x
package com.example.legacysoap;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * Main application class for modern REST API
 * Replaces the SOAP WebServiceConfig with REST configuration
 */
@SpringBootApplication
public class ModernRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModernRestApplication.class, args);
    }

    /**
     * OpenAPI configuration for automatic API documentation
     * Replaces WSDL generation in SOAP approach
     */
    @Bean
    public OpenAPI shoppingCartOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Shopping Cart REST API")
                .description("Modern REST API for shopping cart operations. " +
                           "Migrated from SOAP to REST using Java 17 and Spring Boot 3.x")
                .version("v1.0")
                .contact(new Contact()
                    .name("API Support")
                    .email("support@example.com")))
            .servers(List.of(
                new Server().url("http://localhost:8080").description("Development server"),
                new Server().url("https://api.example.com").description("Production server")
            ));
    }

    /**
     * CORS configuration for REST API
     * More flexible than SOAP cross-origin restrictions
     */
    @Bean
    public CorsFilter corsFilter() {
        var config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        
        return new CorsFilter(source);
    }
}