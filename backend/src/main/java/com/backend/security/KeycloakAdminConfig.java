package com.backend.security;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

@Configuration
public class KeycloakAdminConfig {

    @Value("${keycloak.server-url}")
    private String serverUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.admin.username}")
    private String username;
    @Value("${keycloak.admin.password}")
    private String password;
    @Value("${keycloak.admin.client-id}")
    private String clientId;
    @Value("${keycloak.admin.client-secret:}")
    private String clientSecret;

    @Bean
    public Keycloak keycloak() {
        KeycloakBuilder builder = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .username(username)
                .password(password)
                .clientId(clientId);
        if (!clientSecret.isBlank()) {
            builder.clientSecret(clientSecret);
        }
        return builder.build();
    }
}


