package com.backend.service;

import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeycloakUserService {

    private final Keycloak keycloak;

    public KeycloakUserService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    /**
     * Create a new user in realm "myrealm"
     */
    public String createUser(String username, String email, String password) {
        // 1. Prepare user representation
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setEnabled(true);

        // 2. Call Keycloak to create
        var response = keycloak.realm("myrealm")
                .users()
                .create(user);

        if (response.getStatus() != 201) {
            throw new RuntimeException("Keycloak create user failed: " + response.getStatusInfo());
        }

        // 3. Set initial password
        String userId = CreatedResponseUtil.getCreatedId(response);
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setTemporary(false);
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(password);
        keycloak.realm("myrealm")
                .users()
                .get(userId)
                .resetPassword(cred);

        // 4. Assign default role "user"
        RoleRepresentation roleUser = keycloak.realm("myrealm")
                .roles()
                .get("user")
                .toRepresentation();
        keycloak.realm("myrealm")
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .add(List.of(roleUser));

        return userId;
    }
}

