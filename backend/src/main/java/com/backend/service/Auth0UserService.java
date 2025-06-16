package com.backend.service;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.exception.RateLimitException;
import com.auth0.json.mgmt.Page; // Import dla paginacji
import com.backend.model.Auth0UserDto;
import com.backend.model.Opinion;
import com.backend.model.Store;
import com.backend.model.User; // Twoja encja User
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class Auth0UserService { // Zmieniam nazwę na bardziej standardową
    private final UserService userService;
    private final ManagementAPI managementAPI;
    private final OpinionService opinionService;
    private final StoreService storeService;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Value("${app.protected-user.ids}")
    private Set<String> protectedUserIds;

    // Metoda dla webhooka
    @Transactional
    public void processIncomingUser(Auth0UserDto dto) {
        if (dto.getUserId() == null || dto.getEmail() == null) {
            log.warn("Skipping incoming user due to missing userId or email: {}", dto);
            return;
        }

        // Używamy pełnego ID, bez obcinania
        Optional<User> userOpt = userService.getUserById(dto.getUserId());
        if (userOpt.isEmpty()) {

            userOpt = userService.findByEmail(dto.getEmail());
        }

        userOpt.ifPresentOrElse(
                existingUser -> updateUserFromDto(existingUser, dto),
                () -> createUserFromDto(dto)
        );
    }

    // Główna metoda synchronizacji - bez zmian, jest OK
    @Scheduled(initialDelayString = "${app.sync.initial-delay:3600000}", fixedRateString = "${app.sync.fixed-rate:3600000}")
    @Transactional
    public void synchronizeAllUsers() {
        log.info("Starting full user synchronization with Auth0.");
        try {
            List<com.auth0.json.mgmt.users.User> auth0Users = fetchAllAuth0Users();
            Map<String, User> localUsersMapById = userService.getAllUsers().stream()
                    .collect(Collectors.toMap(User::getId, Function.identity()));

            // Przekazujemy pełne ID do obsługi deaktywacji
            handleDeactivatedUsers(localUsersMapById, auth0Users.stream().map(com.auth0.json.mgmt.users.User::getId).collect(Collectors.toSet()));

            for (com.auth0.json.mgmt.users.User auth0User : auth0Users) {
                // Kluczem jest PEŁNY, nieobcięty ID
                String fullId = auth0User.getId();

                // KROK 1: Inteligentne wyszukiwanie
                Optional<User> localUserOpt = Optional.ofNullable(localUsersMapById.get(fullId));
                if (localUserOpt.isEmpty()) {
                    localUserOpt = userService.findByEmail(auth0User.getEmail());
                }

                // KROK 2: Decyzja
                if (localUserOpt.isPresent()) {
                    // Użytkownik znaleziony -> aktualizujemy Auth0, jeśli trzeba
                    User localUser = localUserOpt.get();
                    if (dataHasChanged(localUser, auth0User)) {
                        updateUserInAuth0(localUser, auth0User.getId());
                    }
                } else {
                    // Użytkownika na pewno nie ma -> tworzymy go
                    log.info("User with email {} not found in local DB. Creating.", auth0User.getEmail());
                    createUserFromAuth0User(auth0User);
                }
            }
        } catch (Exception e) {
            log.error("A critical error occurred during user synchronization.", e);
        }
        log.info("Full user synchronization finished.");
    }

    // --- POPRAWIONE I DODANE METODY POMOCNICZE ---

    /**
     * [NOWA i KLUCZOWA] Pobiera WSZYSTKIE pełne obiekty użytkowników z Auth0, strona po stronie.
     * @return Lista obiektów użytkowników z biblioteki Auth0.
     */
    private List<com.auth0.json.mgmt.users.User> fetchAllAuth0Users() throws Auth0Exception, InterruptedException {
        List<com.auth0.json.mgmt.users.User> allUsers = new ArrayList<>();
        int page = 0;
        final int pageSize = 50; // Użyjemy stałej dla czytelności

        while (true) {
            UserFilter filter = new UserFilter().withPage(page, pageSize);
            try {
                // POPRAWKA: API zwraca generyczny obiekt Page<User>, a nie UserPage
                Page<com.auth0.json.mgmt.users.User> pageResult = managementAPI.users().list(filter).execute().getBody();

                if (pageResult == null || pageResult.getItems() == null || pageResult.getItems().isEmpty()) {
                    break; // Koniec stron, przerywamy pętlę
                }

                allUsers.addAll(pageResult.getItems());

                // Jeśli dostaliśmy mniej wyników niż rozmiar strony, to jest to ostatnia strona
                if (pageResult.getItems().size() < pageSize) {
                    break;
                }
                    page++;
                    Thread.sleep(1000);
                } catch (RateLimitException e) {
                    log.warn("Auth0 rate limit hit. Waiting for 60 seconds before retrying.");
                    Thread.sleep(60000);
                }
            }
            return allUsers;
        }

    /**
     * [POPRAWIONA] Aktualizuje profil użytkownika w Auth0 na podstawie danych z naszej bazy.
     * Używa poprawnych nazw metod z biblioteki Auth0.
     * @param localUser Użytkownik z naszej bazy danych (com.backend.model.User).
     * @param auth0Id Pełny identyfikator Auth0 (np. "google-oauth2|123456").
     */
    private void updateUserInAuth0(User localUser, String auth0Id) {
        try {
            // Tworzymy obiekt żądania aktualizacji z biblioteki Auth0
            com.auth0.json.mgmt.users.User auth0UpdateRequest = new com.auth0.json.mgmt.users.User();

            // Używamy poprawnych setterów z klasy com.auth0.json.mgmt.users.User
            auth0UpdateRequest.setGivenName(localUser.getFirstName());
            auth0UpdateRequest.setFamilyName(localUser.getLastName());
            auth0UpdateRequest.setName(localUser.getName());
            auth0UpdateRequest.setNickname(localUser.getName()); // Często name i nickname są tym samym
            auth0UpdateRequest.setPicture(localUser.getImg());

            managementAPI.users().update(auth0Id, auth0UpdateRequest).execute();
            log.info("Successfully updated user {} in Auth0.", auth0Id);

        } catch (Auth0Exception e) {
            log.error("Failed to update user {} in Auth0: {}", auth0Id, e.getMessage());
        }
    }

    // Metody handleDeactivatedUsers i dataHasChanged - bez zmian, są OK
    private void handleDeactivatedUsers(Map<String, User> localUsersMap, Set<String> auth0UserIds) {
        Set<String> localUserIds = localUsersMap.keySet();

        // 1. Znajdź kandydatów do usunięcia
        Set<String> idsToDelete = new HashSet<>(localUserIds);
        idsToDelete.removeAll(auth0UserIds);

        // 2. KROK KRYTYCZNY: Usuń WSZYSTKIE chronione ID z listy kandydatów do skasowania.
        // Używamy `removeAll`, które jest wydajne dla zbiorów.
        boolean wereAnyProtected = idsToDelete.removeAll(protectedUserIds);
        if (wereAnyProtected) {
            log.info("Protected users were correctly excluded from the deletion list.");
        }

        // 3. Kontynuuj tylko, jeśli nadal są jacyś użytkownicy do usunięcia
        if (!idsToDelete.isEmpty()) {
            log.warn("Found {} users to delete. Reassigning their content first.", idsToDelete.size());

            // Pobierz ID "Użytkownika Usuniętego" z naszej listy chronionych.
            // Zakładamy, że jest tam zawsze, ale dodajemy solidne sprawdzenie.
            String deletedUserPlaceholderId = "11111111-1111-1111-1111-111111111111"; // Można też wziąć pierwszy element z `protectedUserIds`, ale to jest bezpieczniejsze

            User deletedUserPlaceholder = userService.getUserById(deletedUserPlaceholderId)
                    .orElseThrow(() -> new IllegalStateException("Critical Error: Deleted User placeholder with ID " + deletedUserPlaceholderId + " not found in the database!"));

            // Pobierz pełne obiekty użytkowników, których chcemy usunąć
            List<User> usersToDelete = userService.findAllUsersByIdIn(idsToDelete);

            if (!usersToDelete.isEmpty()) {
                // Znajdź i przepisz wszystkie powiązane opinie
                List<Opinion> opinionsToReassign = opinionService.getOpinionsByUserIn(usersToDelete);
                if (!opinionsToReassign.isEmpty()) {
                    log.info("Reassigning {} opinions to the deleted-user placeholder.", opinionsToReassign.size());
                    opinionsToReassign.forEach(opinion -> opinion.setUser(deletedUserPlaceholder));
                    opinionService.saveAllOpinions(opinionsToReassign);
                }

                // Znajdź i przepisz wszystkie powiązane sklepy
                // (Zakładam, że masz `StoreRepository` z podobną metodą)
                 List<Store> storesToReassign = storeService.getStoresByUserIn(usersToDelete);
                 if (!storesToReassign.isEmpty()) {
                     log.info("Reassigning {} stores to the deleted-user placeholder.", storesToReassign.size());
                     storesToReassign.forEach(store -> store.setUser(deletedUserPlaceholder));
                     storeService.saveAllStores(storesToReassign);
                 }

                // Teraz możemy bezpiecznie usunąć użytkowników
                log.info("Proceeding with deletion of {} users.", usersToDelete.size());
                userService.deleteAllUsersInBatch(usersToDelete);
            }
        }
    }

    private boolean dataHasChanged(User localUser, com.auth0.json.mgmt.users.User auth0User) {
        if (!Objects.equals(localUser.getFirstName(), auth0User.getGivenName())) return true;
        if (!Objects.equals(localUser.getLastName(), auth0User.getFamilyName())) return true;
        if (!Objects.equals(localUser.getName(), auth0User.getNickname())) return true;
        if (!Objects.equals(localUser.getImg(), auth0User.getPicture())) return true;
        return false;
    }

    private void createUserFromDto(Auth0UserDto dto) {
        User user = new User();
        // Ustawiamy PEŁNY ID, bez obcinania
        user.setId(dto.getUserId());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getGivenName() != null ? dto.getGivenName() : "");
        user.setLastName(dto.getFamilyName() != null ? dto.getFamilyName() : "");
        user.setName(dto.getName() != null ? dto.getName() : "");
        user.setImg(dto.getImg());

        if (dto.getCreatedAt() != null) {
            try {
                user.setCreatedAt(LocalDateTime.parse(dto.getCreatedAt(), ISO_FORMATTER));
            } catch (DateTimeParseException e) {
                user.setCreatedAt(LocalDateTime.now());
                log.warn("Cannot parse createdAt='{}', setting to now.", dto.getCreatedAt());
            }
        } else {
            user.setCreatedAt(LocalDateTime.now());
        }

        user.setTier(0);
        user.setStores(new ArrayList<>());
        user.setFavoriteStores(new HashSet<>());

        try {
            userService.saveUser(user);
            log.info("Created new user from DTO with ID: {}", user.getId());
        } catch (IllegalArgumentException e) {
            // Ignorujemy błędy walidacji (np. duplikat numeru tel.), logujemy i kontynuujemy
            log.warn("Could not create user {} due to validation error: {}",
                    (user.getEmail() != null ? user.getEmail() : user.getId()), e.getMessage());
        }
    }

    private void updateUserFromDto(User existing, Auth0UserDto dto) {
        boolean changed = false;
        if (dto.getGivenName() != null && !dto.getGivenName().equals(existing.getFirstName())) {
            existing.setFirstName(dto.getGivenName());
            changed = true;
        }
        // ... reszta pól jak w poprzednim kodzie
        if (dto.getFamilyName() != null && !dto.getFamilyName().equals(existing.getLastName())) {
            existing.setLastName(dto.getFamilyName());
            changed = true;
        }
        if (dto.getName() != null && !dto.getName().equals(existing.getName())) {
            existing.setName(dto.getName());
            changed = true;
        }
        if (dto.getImg() != null && !dto.getImg().equals(existing.getImg())) {
            existing.setImg(dto.getImg());
            changed = true;
        }
        if (changed) {
            userService.saveUser(existing);
        }
    }

    private void createUserFromAuth0User(com.auth0.json.mgmt.users.User auth0User) {
        Auth0UserDto dto = new Auth0UserDto();
        dto.setUserId(auth0User.getId());
        dto.setEmail(auth0User.getEmail());
        dto.setGivenName(auth0User.getGivenName());
        dto.setFamilyName(auth0User.getFamilyName());
        dto.setName(auth0User.getNickname());
        dto.setImg(auth0User.getPicture());
        if (auth0User.getCreatedAt() != null) {
            dto.setCreatedAt(auth0User.getCreatedAt().toInstant().toString());
        }
        createUserFromDto(dto);
    }

    private String extractCleanId(String rawId) {
        if (rawId == null) return null;
        int idx = rawId.indexOf('|');
        return idx >= 0 ? rawId.substring(idx + 1) : rawId;
    }

    @EventListener(ApplicationReadyEvent.class)
    @ConditionalOnProperty(name = "app.sync.on-startup.enabled", havingValue = "true", matchIfMissing = true)
    public void onApplicationStart() {
        new Thread(this::synchronizeAllUsers).start();
    }
}