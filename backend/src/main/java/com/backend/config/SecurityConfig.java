package com.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${OAUTH2_ISSUER}")
    private String issuer;


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // Public: fetching data
                        .requestMatchers("/graphql").permitAll()
                        .requestMatchers("/api/auth0/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/stores/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/opinions/**").permitAll()
                        // All mutations or non-GET REST: require auth
                        //.requestMatchers(HttpMethod.POST, "/graphql").authenticated()
                        .requestMatchers("/api/**").authenticated()
                        //.anyRequest().denyAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder()))
                )

                 .addFilterBefore(logAuthHeader(), BearerTokenAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return JwtDecoders.fromOidcIssuerLocation(issuer);
    }

    @Bean
    public OncePerRequestFilter logAuthHeader() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain)
                    throws ServletException, IOException {
                System.out.println("==> Incoming Authorization header: "
                        + request.getHeader("Authorization"));
                filterChain.doFilter(request, response);
            }
        };
    }

}