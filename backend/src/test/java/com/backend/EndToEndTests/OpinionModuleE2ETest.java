package com.backend.EndToEndTests;

import com.backend.model.Opinion;
import com.backend.model.Store;
import com.backend.model.User;
import com.backend.repository.OpinionRepository;
import com.backend.repository.StoreRepository;
import com.backend.repository.UserRepository;
import com.backend.service.OpinionService;
import com.backend.service.StoreService;
import com.backend.service.UserService;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
public class OpinionModuleE2ETest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private OpinionRepository opinionRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OpinionService opinionService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private UserService userService;

    private Store testStore;
    private User testUser;
    private User storeOwnerUser;

    @BeforeEach
    public void setUp() {
        // Clean up existing data
        opinionRepository.deleteAll();

        // Create test users
        storeOwnerUser = new User(
                "store-owner-id",
                "Store",
                "Owner",
                "storeowner@example.com",
                "123456789",
                0,
                "owner-img.jpg"
        );
        storeOwnerUser = userRepository.save(storeOwnerUser);

        testUser = new User(
                "test-user-id",
                "Test",
                "User",
                "testuser@example.com",
                "987654321",
                0,
                "user-img.jpg"
        );
        testUser = userRepository.save(testUser);

        // Create test store using proper constructor
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
    }

    @AfterEach
    public void tearDown() {
        // Clean up all test data
        opinionRepository.deleteAll();
        storeRepository.delete(testStore);
        userRepository.delete(testUser);
        userRepository.delete(storeOwnerUser);
    }

    @Test
    @Transactional
    public void testCompleteOpinionLifecycle() {
        // 1. Create a new opinion through GraphQL
        String createOpinionMutation = """
            mutation {
                createOpinion(
                    storeId: %d,
                    userId: "%s",
                    description: "Great store with excellent service!",
                    stars: 5
                ) {
                    id
                    description
                    stars
                    store {
                        id
                        name
                    }
                    user {
                        id
                        firstName
                    }
                }
            }
            """.formatted(testStore.getId(), testUser.getId());

        // Execute the mutation and verify the response
        GraphQlTester.Response createResponse = graphQlTester
                .document(createOpinionMutation)
                .execute();

        Long opinionId = createResponse
                .path("createOpinion.id")
                .entity(Long.class)
                .get();

        createResponse
                .path("createOpinion.description").entity(String.class).isEqualTo("Great store with excellent service!")
                .path("createOpinion.stars").entity(Integer.class).isEqualTo(5)
                .path("createOpinion.store.id").entity(String.class).isEqualTo(testStore.getId().toString())
                .path("createOpinion.user.id").entity(String.class).isEqualTo(testUser.getId());

        // 2. Verify opinion exists in database
        Optional<Opinion> savedOpinion = opinionService.getOpinionById(opinionId);
        assertTrue(savedOpinion.isPresent());
        assertEquals("Great store with excellent service!", savedOpinion.get().getDescription());
        assertEquals(5, savedOpinion.get().getStars());

        // 3. Get the opinion by ID using GraphQL
        String getOpinionQuery = """
            query {
                opinionById(id: %d) {
                    id
                    description
                    stars
                }
            }
            """.formatted(opinionId);

        graphQlTester
                .document(getOpinionQuery)
                .execute()
                .path("opinionById.id").entity(String.class).isEqualTo(opinionId.toString())
                .path("opinionById.description").entity(String.class).isEqualTo("Great store with excellent service!")
                .path("opinionById.stars").entity(Integer.class).isEqualTo(5);

        // 4. Get opinions by store ID
        String getOpinionsByStoreQuery = """
            query {
                opinionsByStoreId(storeId: %d) {
                    id
                    description
                    stars
                }
            }
            """.formatted(testStore.getId());

        GraphQlTester.Response storeOpinionsResponse = graphQlTester
                .document(getOpinionsByStoreQuery)
                .execute();

        storeOpinionsResponse
                .path("opinionsByStoreId[0].id").entity(String.class).isEqualTo(opinionId.toString())
                .path("opinionsByStoreId[0].description").entity(String.class).isEqualTo("Great store with excellent service!")
                .path("opinionsByStoreId[0].stars").entity(Integer.class).isEqualTo(5);

        // 5. Update the opinion
        String updateOpinionMutation = """
            mutation {
                updateOpinion(
                    id: %d,
                    description: "Updated opinion after second visit.",
                    stars: 4
                ) {
                    id
                    description
                    stars
                }
            }
            """.formatted(opinionId);

        graphQlTester
                .document(updateOpinionMutation)
                .execute()
                .path("updateOpinion.id").entity(String.class).isEqualTo(opinionId.toString())
                .path("updateOpinion.description").entity(String.class).isEqualTo("Updated opinion after second visit.")
                .path("updateOpinion.stars").entity(Integer.class).isEqualTo(4);

        // 6. Verify update in the database
        Opinion updatedOpinion = opinionService.getOpinionById(opinionId).orElseThrow();
        assertEquals("Updated opinion after second visit.", updatedOpinion.getDescription());
        assertEquals(4, updatedOpinion.getStars());

        // 7. Test getting all opinions
        String getAllOpinionsQuery = """
            query {
                opinions {
                    id
                    description
                    stars
                }
            }
            """;

        List<Opinion> allOpinions = opinionService.getAllOpinions();
        assertEquals(1, allOpinions.size());

        graphQlTester
                .document(getAllOpinionsQuery)
                .execute()
                .path("opinions").entityList(Opinion.class).hasSize(1);

        // 8. Delete the opinion
        String deleteOpinionMutation = """
            mutation {
                deleteOpinion(id: %d)
            }
            """.formatted(opinionId);

        graphQlTester
                .document(deleteOpinionMutation)
                .execute()
                .path("deleteOpinion").entity(Boolean.class).isEqualTo(true);

        // 9. Verify opinion was deleted
        Optional<Opinion> deletedOpinion = opinionService.getOpinionById(opinionId);
        assertTrue(deletedOpinion.isEmpty());
    }

    @Test
    public void testDuplicateOpinionPrevention() {
        // First create an opinion
        String createOpinionMutation = """
            mutation {
                createOpinion(
                    storeId: %d,
                    userId: "%s",
                    description: "First opinion",
                    stars: 4
                ) {
                    id
                }
            }
            """.formatted(testStore.getId(), testUser.getId());

        graphQlTester
                .document(createOpinionMutation)
                .execute()
                .path("createOpinion.id").entity(Long.class).isNotEqualTo(null);

        // Try to create a duplicate opinion and expect error
        graphQlTester.document(createOpinionMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });
    }

    @Test
    public void testOpinionInvalidStoreId() {
        // Test creating opinion with invalid store ID
        String invalidStoreMutation = """
            mutation {
                createOpinion(
                    storeId: 999999,
                    userId: "%s",
                    description: "Test description",
                    stars: 5
                ) {
                    id
                }
            }
            """.formatted(testUser.getId());

        graphQlTester.document(invalidStoreMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });
    }

    @Test
    public void testOpinionInvalidUserId() {
        // Test creating opinion with invalid user ID
        String invalidUserMutation = """
            mutation {
                createOpinion(
                    storeId: %d,
                    userId: "non-existent-user",
                    description: "Test description",
                    stars: 5
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
    public void testOpinionStarsValidation() {
        // Create an opinion with valid stars (should succeed)
        String validStarsMutation = """
            mutation {
                createOpinion(
                    storeId: %d,
                    userId: "%s",
                    description: "Valid stars rating",
                    stars: 5
                ) {
                    id
                    stars
                }
            }
            """.formatted(testStore.getId(), testUser.getId());

        GraphQlTester.Response validResponse = graphQlTester
                .document(validStarsMutation)
                .execute();

        Long opinionId = validResponse
                .path("createOpinion.id")
                .entity(Long.class)
                .get();

        // Then try to update with invalid stars (should fail)
        String invalidUpdateMutation = """
            mutation {
                updateOpinion(
                    id: %d,
                    stars: 6
                ) {
                    id
                }
            }
            """.formatted(opinionId);

        graphQlTester.document(invalidUpdateMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });

        // And try negative stars (should also fail)
        String negativeStarsMutation = """
            mutation {
                updateOpinion(
                    id: %d,
                    stars: -1
                ) {
                    id
                }
            }
            """.formatted(opinionId);

        graphQlTester.document(negativeStarsMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });
    }

    @Test
    public void testPartialOpinionUpdate() {
        // First create an opinion
        String createOpinionMutation = """
            mutation {
                createOpinion(
                    storeId: %d,
                    userId: "%s",
                    description: "Initial description",
                    stars: 3
                ) {
                    id
                }
            }
            """.formatted(testStore.getId(), testUser.getId());

        Long opinionId = graphQlTester
                .document(createOpinionMutation)
                .execute()
                .path("createOpinion.id")
                .entity(Long.class)
                .get();

        // Update only the description
        String updateDescriptionMutation = """
            mutation {
                updateOpinion(
                    id: %d,
                    description: "Updated description only"
                ) {
                    id
                    description
                    stars
                }
            }
            """.formatted(opinionId);

        graphQlTester
                .document(updateDescriptionMutation)
                .execute()
                .path("updateOpinion.description").entity(String.class).isEqualTo("Updated description only")
                .path("updateOpinion.stars").entity(Integer.class).isEqualTo(3); // Stars should remain unchanged

        // Update only the stars
        String updateStarsMutation = """
            mutation {
                updateOpinion(
                    id: %d,
                    stars: 4
                ) {
                    id
                    description
                    stars
                }
            }
            """.formatted(opinionId);

        graphQlTester
                .document(updateStarsMutation)
                .execute()
                .path("updateOpinion.description").entity(String.class).isEqualTo("Updated description only") // Description should remain unchanged
                .path("updateOpinion.stars").entity(Integer.class).isEqualTo(4);
    }
}