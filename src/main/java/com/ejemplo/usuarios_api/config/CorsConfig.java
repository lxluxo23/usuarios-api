package com.ejemplo.usuarios_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements  WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Permitir todas las rutas
                .allowedOrigins("*") // Permitir todos los orígenes
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Permitir todos los métodos HTTP
                .allowedHeaders("*") // Permitir todos los encabezados
                .allowCredentials(false); // No se permite el uso de cookies (si lo necesitas, ponlo en true)
    }
}

