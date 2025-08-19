package com.example.legacysoap.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * REST API configuration for Java 17/Spring Boot 3.x
 * Replaces legacy WebServiceConfig for SOAP
 */
@Configuration
@EnableWebMvc
public class RestApiConfig implements WebMvcConfigurer {
    
    /**
     * OpenAPI configuration for REST API documentation
     * Replaces WSDL generation from WebServiceConfig
     */
    @Bean
    public OpenAPI cartApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Shopping Cart REST API")
                .version("v1.0")
                .description("Modern REST API for shopping cart operations - migrated from SOAP")
                .contact(new Contact()
                    .name("Development Team")
                    .email("dev@example.com")));
    }
}
