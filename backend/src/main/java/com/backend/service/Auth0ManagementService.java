package com.backend.service;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.backend.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class Auth0ManagementService {

    private final ManagementAPI managementAPI;

    public void updateUserInAuth0(User localUser) {
        if (localUser.getId() == null) {
            log.error("Cannot update user in Auth0 without an ID.");
            return;
        }

        try {
            com.auth0.json.mgmt.users.User auth0UpdateRequest = new com.auth0.json.mgmt.users.User();

            auth0UpdateRequest.setGivenName(localUser.getFirstName());
            auth0UpdateRequest.setFamilyName(localUser.getLastName());
            auth0UpdateRequest.setName(localUser.getName());
            auth0UpdateRequest.setNickname(localUser.getName());
            auth0UpdateRequest.setPicture(localUser.getImg());

            managementAPI.users().update(localUser.getId(), auth0UpdateRequest).execute();
            log.info("Successfully pushed profile update to Auth0 for user {}", localUser.getId());

        } catch (Auth0Exception e) {
            log.error("Failed to push profile update to Auth0 for user {}: {}", localUser.getId(), e.getMessage());
        }
    }
}
