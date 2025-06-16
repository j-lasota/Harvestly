package com.backend.EndToEndTests;

import com.backend.model.Store;
import com.backend.model.User;
import com.backend.model.Verification;
import com.backend.repository.StoreRepository;
import com.backend.repository.UserRepository;
import com.backend.repository.VerificationRepository;
import com.backend.service.StoreService;
import com.backend.service.UserService;
import com.backend.service.VerificationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
public class VerificationModuleE2ETests {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private StoreService storeService;


    private Store testStore;
    private User storeOwnerUser;
    private User[] verifierUsers;

    @BeforeEach
    public void setUp() {
        verificationRepository.deleteAll();
        storeRepository.deleteAll();
        userRepository.deleteAll();

        storeOwnerUser = new User(
                UUID.randomUUID().toString(),
                "Store",
                "Owner",
                "storeowner@example.com",
                "123456789",
                0,
                "owner-img.jpg"
        );
        storeOwnerUser = userRepository.save(storeOwnerUser);

        testStore = new Store(
                storeOwnerUser,
                "Test Store",
                "A store for testing",
                45.0,
                45.0,
                "Test City",
                "123 Test Street",
                "store-img.jpg",
                "test-store"
        );
        testStore = storeRepository.save(testStore);

        verifierUsers = new User[5];
        for (int i = 0; i < 5; i++) {
            verifierUsers[i] = new User(
                    UUID.randomUUID().toString(),
                    "Verifier",
                    "User " + i,
                    "verifier" + i + "@example.com",
                    "9876543" + i,
                    0,
                    "verifier-img" + i + ".jpg"
            );
            verifierUsers[0].setId("2a6e8658-d6db-45d8-9131-e8f87b62ed75"); // for checking if authentication works
            verifierUsers[i] = userRepository.save(verifierUsers[i]);
        }
    }

    @AfterEach
    public void tearDown() {
        verificationRepository.deleteAll();
        storeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed75")
    public void testCompleteVerificationLifecycle() {
        String createVerificationMutation = """
            mutation {
                createVerification(
                    storeId: %d,
                    userId: "%s"
                ) {
                    id
                    store {
                        id
                        name
                        verified
                    }
                    user {
                        id
                        firstName
                        lastName
                    }
                }
            }
            """.formatted(testStore.getId(), "2a6e8658-d6db-45d8-9131-e8f87b62ed75");

        GraphQlTester.Response createResponse = graphQlTester
                .document(createVerificationMutation)
                .execute();

        Long verificationId = createResponse
                .path("createVerification.id")
                .entity(Long.class)
                .get();

        createResponse
                .path("createVerification.store.id").entity(String.class).isEqualTo(testStore.getId().toString())
                .path("createVerification.store.name").entity(String.class).isEqualTo("Test Store")
                .path("createVerification.store.verified").entity(Boolean.class).isEqualTo(false)
                .path("createVerification.user.id").entity(String.class).isEqualTo("2a6e8658-d6db-45d8-9131-e8f87b62ed75");

        Optional<Verification> savedVerification = verificationService.getVerificationById(verificationId);
        assertTrue(savedVerification.isPresent());
        assertEquals(testStore.getId(), savedVerification.get().getStore().getId());
        assertEquals("2a6e8658-d6db-45d8-9131-e8f87b62ed75", savedVerification.get().getUser().getId());

        String getVerificationQuery = """
            query {
                verificationById(id: %d) {
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
            """.formatted(verificationId);

        graphQlTester
                .document(getVerificationQuery)
                .execute()
                .path("verificationById.id").entity(String.class).isEqualTo(verificationId.toString())
                .path("verificationById.store.id").entity(String.class).isEqualTo(testStore.getId().toString())
                .path("verificationById.user.id").entity(String.class).isEqualTo("2a6e8658-d6db-45d8-9131-e8f87b62ed75");

        String getAllVerificationsQuery = """
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

        List<Verification> allVerifications = verificationService.getAllVerifications();
        assertEquals(1, allVerifications.size());

        graphQlTester
                .document(getAllVerificationsQuery)
                .execute()
                .path("verifications").entityList(Verification.class).hasSize(1);

        String deleteVerificationMutation = """
            mutation {
                deleteVerification(id: %d)
            }
            """.formatted(verificationId);

        graphQlTester
                .document(deleteVerificationMutation)
                .execute()
                .path("deleteVerification").entity(Boolean.class).isEqualTo(true);

        Optional<Verification> deletedVerification = verificationService.getVerificationById(verificationId);
        assertTrue(deletedVerification.isEmpty());
    }

    @Test
    @Transactional
    public void testStoreVerificationLogic() {
        // bypass security for this test
        for (int i = 0; i < 4; i++) {
            verificationService.saveVerification(new Verification(testStore, verifierUsers[i]));
        }

        Store storeBeforeFifthVerification = storeService.getStoreById(testStore.getId()).orElseThrow();
        assertFalse(storeBeforeFifthVerification.isVerified());
        assertEquals(0, storeBeforeFifthVerification.getUser().getTier());

        verificationService.saveVerification(new Verification(testStore, verifierUsers[4]));
        Store verifiedStore = storeService.getStoreById(testStore.getId()).orElseThrow();
        assertTrue(verifiedStore.isVerified());
        assertEquals(1, verifiedStore.getUser().getTier());
    }

    @Test
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed75")
    public void testDuplicateVerification() {
        String createFirstVerificationMutation = """
            mutation {
                createVerification(
                    storeId: %d,
                    userId: "%s"
                ) {
                    id
                }
            }
            """.formatted(testStore.getId(), verifierUsers[0].getId());

        graphQlTester
                .document(createFirstVerificationMutation)
                .execute()
                .path("createVerification.id").entity(Long.class).isNotEqualTo(null);

        String createDuplicateVerificationMutation = """
            mutation {
                createVerification(
                    storeId: %d,
                    userId: "%s"
                ) {
                    id
                }
            }
            """.formatted(testStore.getId(), verifierUsers[0].getId());

        graphQlTester.document(createDuplicateVerificationMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });
    }

    @Test
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed75")
    public void testInvalidVerificationCreation() {
        String invalidStoreMutation = """
            mutation {
                createVerification(
                    storeId: 999999,
                    userId: "%s"
                ) {
                    id
                }
            }
            """.formatted(verifierUsers[0].getId());

        graphQlTester.document(invalidStoreMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });

        String invalidUserMutation = """
            mutation {
                createVerification(
                    storeId: %d,
                    userId: "non-existent-user-id"
                ) {
                    id
                }
            }
            """.formatted(testStore.getId());

        graphQlTester.document(invalidUserMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });
    }

    @Test
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed75")
    public void testInvalidVerificationOperations() {
        String getNonExistentVerificationQuery = """
            query {
                verificationById(id: 999999) {
                    id
                }
            }
            """;

        graphQlTester
                .document(getNonExistentVerificationQuery)
                .execute()
                .path("verificationById")
                .valueIsNull();

        String deleteNonExistentVerificationMutation = """
            mutation {
                deleteVerification(id: 999999)
            }
            """;

        graphQlTester
                .document(deleteNonExistentVerificationMutation)
                .execute()
                .path("deleteVerification").entity(Boolean.class).isEqualTo(false);
    }
    @Test
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed76") // Different user than verifierUsers[0]
    public void testAuthenticationFailsWithDifferentUserId() {
        String createVerificationMutation = """
        mutation {
            createVerification(
                storeId: %d,
                userId: "%s"
            ) {
                id
            }
        }
        """.formatted(testStore.getId(), verifierUsers[0].getId());

        graphQlTester.document(createVerificationMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertFalse(errors.isEmpty());
                });

        List<Verification> verifications = verificationRepository.findAll();
        assertEquals(0, verifications.size());
    }
}