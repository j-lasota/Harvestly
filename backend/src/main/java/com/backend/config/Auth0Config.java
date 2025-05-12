package com.backend.config;

import com.auth0.client.mgmt.ManagementAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class Auth0Config {

    @Value("${auth0.domain}")
    private String domain;

    @Value("${auth0.management.api.token}")
    private String managementApiToken;

    @Bean
    public ManagementAPI managementAPI() {
        return ManagementAPI.newBuilder(domain, managementApiToken)
                .build();
    }
}
