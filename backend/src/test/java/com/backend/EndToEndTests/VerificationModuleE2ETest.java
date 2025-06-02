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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
public class VerificationModuleE2ETest {

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

    @Autowired
    private UserService userService;

    private Store testStore;
    private User storeOwnerUser;
    private User[] verifierUsers;

    @BeforeEach
    public void setUp() {
        // Clean up existing data
        verificationRepository.deleteAll();
        storeRepository.deleteAll();
        userRepository.deleteAll();

        // Create test store owner
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

        // Create test store
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

        // Create verifier users for testing
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
            verifierUsers[i] = userRepository.save(verifierUsers[i]);
        }
    }

    @AfterEach
    public void tearDown() {
        // Clean up all test data
        verificationRepository.deleteAll();
        storeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    public void testCompleteVerificationLifecycle() {
        // 1. Create a new verification through GraphQL
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
            """.formatted(testStore.getId(), verifierUsers[0].getId());

        // Execute the mutation and verify the response
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
                .path("createVerification.user.id").entity(String.class).isEqualTo(verifierUsers[0].getId());

        // 2. Verify verification exists in database
        Optional<Verification> savedVerification = verificationService.getVerificationById(verificationId);
        assertTrue(savedVerification.isPresent());
        assertEquals(testStore.getId(), savedVerification.get().getStore().getId());
        assertEquals(verifierUsers[0].getId(), savedVerification.get().getUser().getId());

        // 3. Get the verification by ID using GraphQL
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
                .path("verificationById.user.id").entity(String.class).isEqualTo(verifierUsers[0].getId());

        // 4. Get all verifications
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

        // 5. Delete the verification
        String deleteVerificationMutation = """
            mutation {
                deleteVerification(id: %d)
            }
            """.formatted(verificationId);

        graphQlTester
                .document(deleteVerificationMutation)
                .execute()
                .path("deleteVerification").entity(Boolean.class).isEqualTo(true);

        // 6. Verify verification was deleted
        Optional<Verification> deletedVerification = verificationService.getVerificationById(verificationId);
        assertTrue(deletedVerification.isEmpty());
    }

    @Test
    @Transactional
    public void testStoreVerificationLogic() {
        // Create 4 verifications (one less than needed to verify the store)
        for (int i = 0; i < 4; i++) {
            String createVerificationMutation = """
                mutation {
                    createVerification(
                        storeId: %d,
                        userId: "%s"
                    ) {
                        id
                        store {
                            id
                            verified
                            user {
                                tier
                            }
                        }
                    }
                }
                """.formatted(testStore.getId(), verifierUsers[i].getId());

            graphQlTester
                    .document(createVerificationMutation)
                    .execute()
                    .path("createVerification.store.verified").entity(Boolean.class).isEqualTo(false)
                    .path("createVerification.store.user.tier").entity(Integer.class).isEqualTo(0);
        }

        // Verify store is still not verified and owner tier is still 0
        Store storeBeforeFifthVerification = storeService.getStoreById(testStore.getId()).orElseThrow();
        assertFalse(storeBeforeFifthVerification.isVerified());
        assertEquals(0, storeBeforeFifthVerification.getUser().getTier());

        // Add the 5th verification which should trigger store verification
        String finalVerificationMutation = """
            mutation {
                createVerification(
                    storeId: %d,
                    userId: "%s"
                ) {
                    id
                    store {
                        id
                        verified
                        user {
                            id
                            tier
                        }
                    }
                }
            }
            """.formatted(testStore.getId(), verifierUsers[4].getId());

        graphQlTester
                .document(finalVerificationMutation)
                .execute()
                .path("createVerification.store.verified").entity(Boolean.class).isEqualTo(true)
                .path("createVerification.store.user.tier").entity(Integer.class).isEqualTo(1);

        // Verify store and owner are updated in the database
        Store verifiedStore = storeService.getStoreById(testStore.getId()).orElseThrow();
        assertTrue(verifiedStore.isVerified());
        assertEquals(1, verifiedStore.getUser().getTier());
    }

    @Test
    public void testDuplicateVerification() {
        // Create initial verification
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

        // Try to create a duplicate verification (same store and user)
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
    public void testInvalidVerificationCreation() {
        // Try to create verification with non-existent store
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

        // Try to create verification with non-existent user
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
    public void testInvalidVerificationOperations() {
        // Try to get non-existent verification by ID
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

        // Try to delete non-existent verification
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
}