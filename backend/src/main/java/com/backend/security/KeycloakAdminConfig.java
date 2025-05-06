//package com.backend.security;
//
//import org.keycloak.admin.client.Keycloak;
//import org.keycloak.admin.client.KeycloakBuilder;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Bean;
//
//@Configuration
//public class KeycloakAdminConfig {
//
//    @Bean
//    public Keycloak keycloak() {
//        return KeycloakBuilder.builder()
//                .serverUrl("http://localhost:8081")
//                .realm("App")               // używamy master, bo klient admin-cli jest tam skonfigurowany
//                .username("admin")
//                .password("admin")
//                .clientId("admin-cli")
//                .build();
//    }
//}
//
