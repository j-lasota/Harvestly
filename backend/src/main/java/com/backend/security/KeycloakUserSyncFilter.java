package com.backend.security;

import com.backend.model.User;
import com.backend.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class KeycloakUserSyncFilter extends OncePerRequestFilter {

    private final Keycloak keycloak;
    private final UserRepository userRepository;
    @Value("${keycloak.realm}")
    private String realm;

    public KeycloakUserSyncFilter(Keycloak keycloak, UserRepository userRepository) {
        this.keycloak = keycloak;
        this.userRepository = userRepository;
    }

    @Bean
    public KeycloakUserSyncFilter keycloakUserSyncFilter(Keycloak keycloak,
                                                         UserRepository userRepository) {
        return new KeycloakUserSyncFilter(keycloak, userRepository);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        logger.info(">>> KeycloakUserSyncFilter uruchomiony dla: " +
                SecurityContextHolder.getContext().getAuthentication());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            String username = jwtAuth.getToken().getClaim("preferred_username");
            // Pobranie z Keycloaka
            List<UserRepresentation> reps = keycloak
                    .realm(realm)
                    .users()
                    .search(username);
            if (!reps.isEmpty()) {
                UserRepresentation rep = reps.get(0);
                User user = userRepository.findAll()
                        .stream()
                        .filter(u -> u.getEmail().equals(rep.getEmail()))
                        .findFirst()
                        .orElse(new User());
                user.setFirstName(rep.getFirstName());
                user.setLastName(rep.getLastName());
                user.setEmail(rep.getEmail());
                // telefon, tier czy img możesz wrzucić do attributes w Keycloak i pobierać tu rep.getAttributes()
                userRepository.save(user);
            }
        }
        filterChain.doFilter(request, response);
    }
}
