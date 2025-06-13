package com.backend.service;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import com.backend.model.Auth0UserDto;
import com.backend.model.User;
import com.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    private static final int PAGE_SIZE = 50;
    private static final long SYNC_DELAY = 60 * 60 * 1000;
    private static final long SYNC_RATE = 60 * 60 * 1000;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;


    @Transactional
    public void processIncomingUsers(List<Auth0UserDto> auth0Users) {
        log.info("Processing {} Auth0 users", auth0Users.size());
        for (Auth0UserDto dto : auth0Users) {
            userRepository.findById(extractCleanId(dto.getUserId()))
                    .ifPresentOrElse(
                            existing -> updateFromDto(existing, dto),
                            ()        -> createFromDto(dto)
                    );
        }
        log.info("Finished processing Auth0 users");
    }

    private void updateFromDto(User existing, Auth0UserDto dto) {
        boolean changed = false;

        // givenName
        if (dto.getGivenName() != null
                && !dto.getGivenName().equals(existing.getFirstName())) {
            existing.setFirstName(dto.getGivenName());
            changed = true;
        }

        // familyName
        if (dto.getFamilyName() != null
                && !dto.getFamilyName().equals(existing.getLastName())) {
            existing.setLastName(dto.getFamilyName());
            changed = true;
        }

        // nickname → name
        if (dto.getName() != null
                && !dto.getName().equals(existing.getName())) {
            existing.setName(dto.getName());
            changed = true;
        }

        // img
        if (dto.getImg() != null
                && !dto.getImg().equals(existing.getImg())) {
            existing.setImg(dto.getImg());
            changed = true;
        }

        // facebookNickname
        if (dto.getFacebook_nickname() != null
                && !dto.getFacebook_nickname().equals(existing.getFacebook_nickname())) {
            existing.setFacebook_nickname(dto.getFacebook_nickname());
            changed = true;
        }

        // createdAt
        if (dto.getCreatedAt() != null) {
            LocalDateTime ts = LocalDateTime.parse(dto.getCreatedAt(), DateTimeFormatter.ISO_DATE_TIME);
            if (existing.getCreatedAt() == null || !ts.equals(existing.getCreatedAt())) {
                existing.setCreatedAt(ts);
                changed = true;
            }
        }

        if (changed) {
            userRepository.save(existing);
            log.debug("Updated user {}", existing.getEmail());
        }
    }

    private String extractCleanId(String rawId) {
        // Auth0 daje np. "google-oauth2|123456", my chcemy tylko po "│"
        if (rawId == null) return null;
        int idx = rawId.indexOf('|');
        return idx >= 0 ? rawId.substring(idx + 1) : rawId;
    }

    private void createFromDto(Auth0UserDto dto) {
        User user = new User();
        user.setId(extractCleanId(dto.getUserId()));

        // Email (zakładamy, że nie-null, bo walidujesz wcześniej)
        user.setEmail(dto.getEmail());

        // Imię i nazwisko
        user.setFirstName(dto.getGivenName() != null ? dto.getGivenName() : "");
        user.setLastName(dto.getFamilyName() != null ? dto.getFamilyName() : "");

        // Nickname → name
        user.setName(dto.getName() != null ? dto.getName() : "");

        // Tier i kolekcje
        user.setTier(0);
        user.setStores(new ArrayList<>());
        user.setFavoriteStores(new HashSet<>());

        // createdAt
        if (dto.getCreatedAt() != null) {
            try {
                LocalDateTime ts = LocalDateTime.parse(dto.getCreatedAt(), DateTimeFormatter.ISO_DATE_TIME);
                user.setCreatedAt(ts);
            } catch (DateTimeParseException e) {
                log.warn("Cannot parse createdAt='{}', skipping", dto.getCreatedAt());
            }
        }

        // Obrazek
        if (dto.getImg() != null) {
            user.setImg(dto.getImg());
        }

        // Facebook nickname
        if (dto.getFacebook_nickname() != null) {
            user.setFacebook_nickname(dto.getFacebook_nickname());
        }

        userRepository.save(user);
        log.debug("Created new user {}", user.getId());
    }

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
                        Thread.sleep(1000); // 1 sekunda przerwy między stronami
                    }
                } catch (Auth0Exception e) {
                    if (e instanceof com.auth0.exception.RateLimitException) {
                        log.warn("Rate limit reached, waiting before next attempt...");
                        Thread.sleep(60000); // minutka jakbym znowu rozjebał wszystkie tokeny w sekundę
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
        String rawId = auth0User.getId();
        String cleanId = rawId.contains("|") ? rawId.substring(rawId.indexOf("|") + 1) : rawId;
        user.setId(cleanId);
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
        user.setStores(new ArrayList<>());
        user.setFavoriteStores(new HashSet<>());
        return userRepository.save(user);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationStart() {
        synchronizeUsers();
    }
}