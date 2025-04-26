package com.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${graphql.url:/graphql}")
    private String graphqlUrl;

    @Value("${app.cors.allowed-origins:*}")
    private String allowedOriginsRaw;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] allowedOrigins = allowedOriginsRaw.split(",");

        registry.addMapping(graphqlUrl)
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
    
}