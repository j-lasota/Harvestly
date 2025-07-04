package com.backend.APITests;

import com.backend.config.GraphQLScalarConfig;
import com.backend.controller.VerificationController;
import com.backend.model.Store;
import com.backend.model.User;
import com.backend.model.Verification;
import com.backend.service.StoreService;
import com.backend.service.UserService;
import com.backend.service.VerificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.backend.config.SecurityTestConfig;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
class VerificationAPITests {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private VerificationService verificationService;

    @MockitoBean
    private StoreService storeService;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed75")

    void verifications_ReturnsAllVerifications() {
        Store store1 = new Store();
        store1.setId(1L);
        store1.setName("Farm Fresh");

        Store store2 = new Store();
        store2.setId(2L);
        store2.setName("Organic Market");

        User user1 = new User();
        user1.setId("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3cfd62 ");
        user1.setFirstName("John");
        user1.setLastName("Doe");

        User user2 = new User();
        user2.setId("2a6e8658-d6db-45d8-9131-e8f87b62ed75");
        user2.setFirstName("Jane");
        user2.setLastName("Smith");

        List<Verification> mockVerifications = Arrays.asList(
                new Verification(store1, user1),
                new Verification(store2, user2)
        );
        mockVerifications.get(0).setId(1L);
        mockVerifications.get(1).setId(2L);

        when(verificationService.getAllVerifications()).thenReturn(mockVerifications);

        String query = """
                query {
                  verifications {
                    id
                    store {
                      id
                      name
                    }
                    user {
                      id
                      firstName
                      lastName
                    }
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("verifications")
                .entityList(Verification.class)
                .hasSize(2);
    }

    @Test
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed75")
    void verificationById_ReturnsVerification_WhenVerificationExists() {
        Long verificationId = 1L;
        Store store = new Store();
        store.setId(1L);
        store.setName("Farm Fresh");

        User user = new User();
        user.setId("2a6e8658-d6db-45d8-9131-e8f87b62ed75");
        user.setFirstName("John");
        user.setLastName("Doe");

        Verification mockVerification = new Verification(store, user);
        mockVerification.setId(verificationId);

        when(verificationService.getVerificationById(verificationId)).thenReturn(Optional.of(mockVerification));

        String query = """
                query {
                  verificationById(id: 1) {
                    id
                    store {
                      id
                      name
                    }
                    user {
                      id
                      firstName
                      lastName
                    }
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("verificationById")
                .entity(Verification.class)
                .satisfies(verification -> {
                    assert verification.getId().equals(verificationId);
                    assert verification.getStore().getName().equals("Farm Fresh");
                    assert verification.getUser().getFirstName().equals("John");
                });
    }

    @Test
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed75")

    void verificationById_ReturnsNull_WhenVerificationDoesNotExist() {
        Long verificationId = 999L;
        when(verificationService.getVerificationById(verificationId)).thenReturn(Optional.empty());

        String query = """
                query {
                  verificationById(id: 999) {
                    id
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("verificationById")
                .valueIsNull();
    }

    @Test
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed75")
    void createVerification_ReturnsCreatedVerification() {
        Long storeId = 1L;
        String userId = "2a6e8658-d6db-45d8-9131-e8f87b62ed75";

        Store store = new Store();
        store.setId(storeId);
        store.setName("Farm Fresh");
        store.setVerified(false);

        User user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setTier(0);

        Verification createdVerification = new Verification(store, user);
        createdVerification.setId(1L);

        when(storeService.getStoreById(storeId)).thenReturn(Optional.of(store));
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(verificationService.saveVerification(any(Verification.class))).thenReturn(createdVerification);

        String mutation = """
                mutation {
                  createVerification(
                    storeId: 1
                    userId: "2a6e8658-d6db-45d8-9131-e8f87b62ed75"
                  ) {
                    id
                    store {
                      id
                      name
                    }
                    user {
                      id
                      firstName
                      lastName
                    }
                  }
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .path("createVerification")
                .entity(Verification.class)
                .satisfies(verification -> {
                    assert verification.getId() != null;
                    assert verification.getStore().getId().equals(storeId);
                    assert verification.getUser().getId().equals(userId);
                });
    }

    @Test
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed75")

    void createVerification_ThrowsException_WhenStoreNotFound() {
        Long storeId = 999L;
        String userId = "2a6e8658-d6db-45d8-9131-e8f87b62ed75";

        User user = new User();
        user.setId(userId);

        when(storeService.getStoreById(storeId)).thenReturn(Optional.empty());
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        String mutation = """
                mutation {
                  createVerification(
                    storeId: 999
                    userId: "2a6e8658-d6db-45d8-9131-e8f87b62ed75"
                  ) {
                    id
                  }
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });
    }

    @Test
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed75")
    void createVerification_ThrowsException_WhenUserNotFound() {
        Long storeId = 1L;
        String userId = "2a6e8658-d6db-45d8-9131-e8f87b62ed75";

        Store store = new Store();
        store.setId(storeId);

        when(storeService.getStoreById(storeId)).thenReturn(Optional.of(store));
        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        String mutation = """
                mutation {
                  createVerification(
                    storeId: 1
                    userId: "2a6e8658-d6db-45d8-9131-e8f87b62ed75"
                  ) {
                    id
                  }
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });
    }

    @Test
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed75")
    void createVerification_ThrowsException_WhenVerificationAlreadyExists() {
        Long storeId = 1L;
        String userId = "2a6e8658-d6db-45d8-9131-e8f87b62ed75";

        Store store = new Store();
        store.setId(storeId);
        store.setName("Farm Fresh");

        User user = new User();
        user.setId(userId);
        user.setFirstName("John");

        when(storeService.getStoreById(storeId)).thenReturn(Optional.of(store));
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(verificationService.saveVerification(any(Verification.class)))
                .thenThrow(new IllegalArgumentException("Verification already exists for the given shop and user."));

        String mutation = """
                mutation {
                  createVerification(
                    storeId: 1
                    userId: 2
                  ) {
                    id
                  }
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });
    }

    @Test
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed75")
    void deleteVerification_ReturnsTrue_WhenVerificationDeleted() {
        Long verificationId = 1L;
        when(verificationService.deleteVerificationById(verificationId)).thenReturn(true);

        String mutation = """
                mutation {
                  deleteVerification(id: 1)
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .path("deleteVerification")
                .entity(Boolean.class)
                .isEqualTo(true);
    }

    @Test
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed75")
    void deleteVerification_ReturnsFalse_WhenVerificationNotFound() {
        Long verificationId = 999L;
        when(verificationService.deleteVerificationById(verificationId)).thenReturn(false);

        String mutation = """
                mutation {
                  deleteVerification(id: 999)
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .path("deleteVerification")
                .entity(Boolean.class)
                .isEqualTo(false);
    }

    @Test
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed75")

    void createVerification_VerifiesStore_WhenFiveVerificationsExist() {
        Long storeId = 1L;
        String userId = "2a6e8658-d6db-45d8-9131-e8f87b62ed75";

        Store store = new Store();
        store.setId(storeId);
        store.setName("Farm Fresh");
        store.setVerified(false);

        User storeOwner = new User();
        storeOwner.setId("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3cfd62");
        storeOwner.setTier(0);

        store.setUser(storeOwner);

        User verifier = new User();
        verifier.setId(userId);

        Verification verificationThatVerifiesStore = new Verification(store, verifier);
        verificationThatVerifiesStore.setId(1L);

        Store verifiedStore = new Store();
        verifiedStore.setId(storeId);
        verifiedStore.setName("Farm Fresh");
        verifiedStore.setVerified(true);

        User upgradedStoreOwner = new User();
        upgradedStoreOwner.setId("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3cfd62");
        upgradedStoreOwner.setTier(1);

        verifiedStore.setUser(upgradedStoreOwner);
        verificationThatVerifiesStore.setStore(verifiedStore);

        when(storeService.getStoreById(storeId)).thenReturn(Optional.of(store));
        when(userService.getUserById(userId)).thenReturn(Optional.of(verifier));
        when(verificationService.saveVerification(any(Verification.class))).thenReturn(verificationThatVerifiesStore);

        String mutation = """
                mutation {
                  createVerification(
                    storeId: 1
                    userId: "2a6e8658-d6db-45d8-9131-e8f87b62ed75"
                  ) {
                    id
                    store {
                      id
                      name
                      verified
                      user {
                        tier
                      }
                    }
                  }
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .path("createVerification")
                .entity(Verification.class)
                .satisfies(verification -> {
                    assert verification.getId() != null;
                    assert verification.getStore().isVerified();
                    assert verification.getStore().getUser().getTier() == 1;
                });
    }
    @Test
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed76")
    void createVerification_ReturnsForbidden_WhenUserIdDoesNotMatchAuthenticatedUser() {
        Long storeId = 1L;
        String userId = "2a6e8658-d6db-45d8-9131-e8f87b62ed75";

        Store store = new Store();
        store.setId(storeId);
        store.setName("Farm Fresh");

        User user = new User();
        user.setId(userId);

        when(storeService.getStoreById(storeId)).thenReturn(Optional.of(store));
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        String mutation = """
            mutation {
              createVerification(
                storeId: 1
                userId: "2a6e8658-d6db-45d8-9131-e8f87b62ed75"
              ) {
                id
                store {
                  id
                  name
                }
              }
            }
            """;

        graphQlTester.document(mutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertFalse(errors.isEmpty());
                });
    }
}