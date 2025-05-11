package com.backend.service;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.PageFilter;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import com.backend.model.User;
import com.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class Auth0UserService {
    private final UserRepository userRepository;
    private final ManagementAPI managementAPI;

    private static final int PAGE_SIZE = 50; // Zmniejszona liczba użytkowników na stronę
    private static final long SYNC_DELAY = 60 * 60 * 1000; // 1 godzina opóźnienia początkowego
    private static final long SYNC_RATE = 24 * 60 * 60 * 1000; // synchronizacja co 24h

    @Scheduled(initialDelay = SYNC_DELAY, fixedRate = SYNC_RATE)
    @Transactional
    public void synchronizeUsers() {
        try {
            log.info("Starting user synchronization");
            int pageIndex = 0;
            boolean hasMore = true;

            while (hasMore) {
                try {
                    UserFilter filter = new UserFilter();
                    filter.withPage(pageIndex, PAGE_SIZE);

                    com.auth0.json.mgmt.users.UsersPage usersPage = managementAPI.users()
                            .list(filter)
                            .execute()
                            .getBody();

                    List<com.auth0.json.mgmt.users.User> users = usersPage.getItems();
                    if (users == null || users.isEmpty()) {
                        hasMore = false;
                    } else {
                        synchronizeUsersPage(users);
                        pageIndex++;
                        // Dodaj opóźnienie między stronami aby uniknąć przekroczenia limitu
                        Thread.sleep(1000); // 1 sekunda przerwy między stronami
                    }
                } catch (Auth0Exception e) {
                    if (e instanceof com.auth0.exception.RateLimitException) {
                        log.warn("Rate limit reached, waiting before next attempt...");
                        Thread.sleep(5000); // 5 sekund przerwy przy przekroczeniu limitu
                        continue;
                    }
                    throw e;
                }
            }
            log.info("User synchronization completed successfully");
        } catch (Exception e) {
            log.error("Error during user synchronization", e);
        }
    }

    private void synchronizeUsersPage(List<com.auth0.json.mgmt.users.User> auth0Users) {
        for (com.auth0.json.mgmt.users.User auth0User : auth0Users) {
            try {
                String email = auth0User.getEmail();
                if (email == null) {
                    log.warn("Skipping user without email");
                    continue;
                }

                userRepository.findByEmail(email)
                        .ifPresentOrElse(
                                existingUser -> updateUserFromAuth0(existingUser, auth0User),
                                () -> createUserFromAuth0(auth0User)
                        );
            } catch (Exception e) {
                log.error("Error synchronizing user: " + auth0User.getEmail(), e);
            }
        }
    }


    private User mapAuth0UserToEntity(com.auth0.json.mgmt.users.User auth0User) {
        String email = auth0User.getEmail();
        if (email == null) {
            log.warn("Skipping user without email");
            return null;
        }

        return userRepository.findByEmail(email)
                .map(existingUser -> updateUserFromAuth0(existingUser, auth0User))
                .orElseGet(() -> createUserFromAuth0(auth0User));
    }

    private User updateUserFromAuth0(User existingUser, com.auth0.json.mgmt.users.User auth0User) {
        existingUser.setEmail(auth0User.getEmail());
        Map<String, Object> userMetadata = auth0User.getUserMetadata();
        if (userMetadata != null) {
            Object givenName = userMetadata.get("given_name");
            Object familyName = userMetadata.get("family_name");
            existingUser.setFirstName(givenName != null ? givenName.toString() : "");
            existingUser.setLastName(familyName != null ? familyName.toString() : "");
        }
        return userRepository.save(existingUser);
    }

    private User createUserFromAuth0(com.auth0.json.mgmt.users.User auth0User) {
        User user = new User();
        user.setEmail(auth0User.getEmail());

        Map<String, Object> userMetadata = auth0User.getUserMetadata();
        if (userMetadata != null) {
            Object givenName = userMetadata.get("given_name");
            Object familyName = userMetadata.get("family_name");
            user.setFirstName(givenName != null ? givenName.toString() : "");
            user.setLastName(familyName != null ? familyName.toString() : "");
        } else {
            user.setFirstName("");
            user.setLastName("");
        }

        user.setTier(0);
        user.setShops(new ArrayList<>());
        user.setFavoriteShops(new HashSet<>());
        return userRepository.save(user);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationStart() {
        synchronizeUsers();
    }
}