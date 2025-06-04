package com.backend.EndToEndTests;

import com.backend.model.Store;
import com.backend.model.User;
import com.backend.repository.StoreRepository;
import com.backend.repository.UserRepository;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
public class StoreModuleE2ETests {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreService storeService;

    @Autowired
    private UserService userService;

    private User testUser;
    private User tierOneUser;

    @BeforeEach
    public void setUp() {
        // Clean up existing data
        storeRepository.deleteAll();
        userRepository.deleteAll();

        // Create test users
        testUser = new User(
                UUID.randomUUID().toString(),
                "Store",
                "Owner",
                "storeowner@example.com",
                "123456789",
                0,
                "owner-img.jpg"
        );
        testUser = userRepository.save(testUser);

        tierOneUser = new User(
                UUID.randomUUID().toString(),
                "Tier",
                "One",
                "tierone@example.com",
                "987654321",
                1,
                "tierone-img.jpg"
        );
        tierOneUser = userRepository.save(tierOneUser);
    }

    @AfterEach
    public void tearDown() {
        // Clean up all test data
        storeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    public void testCompleteStoreLifecycle() {
        // 1. Create a new store through GraphQL
        String createStoreMutation = """
            mutation {
                createStore(
                    userId: "%s",
                    name: "Test Coffee Shop",
                    description: "A great place for coffee",
                    latitude: 51.5074,
                    longitude: -0.1278,
                    city: "London",
                    address: "123 Baker Street",
                    imageUrl: "coffee-shop.jpg"
                ) {
                    id
                    name
                    description
                    latitude
                    longitude
                    city
                    address
                    imageUrl
                    slug
                    verified
                    user {
                        id
                        firstName
                        lastName
                    }
                }
            }
            """.formatted(testUser.getId());

        // Execute the mutation and verify the response
        GraphQlTester.Response createResponse = graphQlTester
                .document(createStoreMutation)
                .execute();

        Long storeId = createResponse
                .path("createStore.id")
                .entity(Long.class)
                .get();

        createResponse
                .path("createStore.name").entity(String.class).isEqualTo("Test Coffee Shop")
                .path("createStore.description").entity(String.class).isEqualTo("A great place for coffee")
                .path("createStore.latitude").entity(Double.class).isEqualTo(51.5074)
                .path("createStore.longitude").entity(Double.class).isEqualTo(-0.1278)
                .path("createStore.city").entity(String.class).isEqualTo("London")
                .path("createStore.address").entity(String.class).isEqualTo("123 Baker Street")
                .path("createStore.imageUrl").entity(String.class).isEqualTo("coffee-shop.jpg")
                .path("createStore.verified").entity(Boolean.class).isEqualTo(false)
                .path("createStore.user.id").entity(String.class).isEqualTo(testUser.getId());

        // 2. Verify store exists in database
        Optional<Store> savedStore = storeService.getStoreById(storeId);
        assertTrue(savedStore.isPresent());
        assertEquals("Test Coffee Shop", savedStore.get().getName());
        assertEquals(testUser.getId(), savedStore.get().getUser().getId());
        assertFalse(savedStore.get().isVerified());

        // 3. Get the store by ID using GraphQL
        String getStoreQuery = """
            query {
                storeById(id: %d) {
                    id
                    name
                    description
                    latitude
                    longitude
                    city
                    address
                    imageUrl
                    slug
                    verified
                    user {
                        id
                        firstName
                        lastName
                    }
                }
            }
            """.formatted(storeId);

        graphQlTester
                .document(getStoreQuery)
                .execute()
                .path("storeById.id").entity(String.class).isEqualTo(storeId.toString())
                .path("storeById.name").entity(String.class).isEqualTo("Test Coffee Shop")
                .path("storeById.user.id").entity(String.class).isEqualTo(testUser.getId());

        // 4. Get all stores
        String getAllStoresQuery = """
            query {
                stores {
                    id
                    name
                    city
                    slug
                    user {
                        id
                    }
                }
            }
            """;

        List<Store> allStores = storeService.getAllStores();
        assertEquals(1, allStores.size());

        graphQlTester
                .document(getAllStoresQuery)
                .execute()
                .path("stores").entityList(Store.class).hasSize(1);

        // 5. Update the store
        String updateStoreMutation = """
            mutation {
                updateStore(
                    id: %d,
                    name: "Updated Coffee Shop",
                    description: "The best coffee in town",
                    latitude: 51.5075,
                    longitude: -0.1279,
                    city: "London",
                    address: "456 Oxford Street",
                    imageUrl: "updated-coffee-shop.jpg"
                ) {
                    id
                    name
                    description
                    latitude
                    longitude
                    city
                    address
                    imageUrl
                    slug
                }
            }
            """.formatted(storeId);

        graphQlTester
                .document(updateStoreMutation)
                .execute()
                .path("updateStore.id").entity(String.class).isEqualTo(storeId.toString())
                .path("updateStore.name").entity(String.class).isEqualTo("Updated Coffee Shop")
                .path("updateStore.description").entity(String.class).isEqualTo("The best coffee in town")
                .path("updateStore.address").entity(String.class).isEqualTo("456 Oxford Street");

        // 6. Verify store was updated in the database
        Store updatedStore = storeService.getStoreById(storeId).orElseThrow();
        assertEquals("Updated Coffee Shop", updatedStore.getName());
        assertEquals("The best coffee in town", updatedStore.getDescription());
        assertEquals("456 Oxford Street", updatedStore.getAddress());

        // 7. Get store by slug
        String storeBySlugQuery = """
            query {
                storeBySlug(slug: "%s") {
                    id
                    name
                    slug
                }
            }
            """.formatted(updatedStore.getSlug());

        graphQlTester
                .document(storeBySlugQuery)
                .execute()
                .path("storeBySlug.id").entity(String.class).isEqualTo(storeId.toString())
                .path("storeBySlug.name").entity(String.class).isEqualTo("Updated Coffee Shop");

        // 8. Delete the store
        String deleteStoreMutation = """
            mutation {
                deleteStore(id: %d)
            }
            """.formatted(storeId);

        graphQlTester
                .document(deleteStoreMutation)
                .execute()
                .path("deleteStore").entity(Boolean.class).isEqualTo(true);

        // 9. Verify store was deleted
        Optional<Store> deletedStore = storeService.getStoreById(storeId);
        assertTrue(deletedStore.isEmpty());
    }

    @Test
    public void testStoreTierRestrictions() {
        // 1. Create first store for tier 0 user
        String createFirstStoreMutation = """
            mutation {
                createStore(
                    userId: "%s",
                    name: "First Store",
                    description: "First store description",
                    latitude: 40.7128,
                    longitude: -74.0060,
                    city: "New York",
                    address: "123 Broadway",
                    imageUrl: "first-store.jpg"
                ) {
                    id
                    name
                }
            }
            """.formatted(testUser.getId());

        graphQlTester
                .document(createFirstStoreMutation)
                .execute()
                .path("createStore.name").entity(String.class).isEqualTo("First Store");

        // 2. Try to create a second store for tier 0 user (should fail)
        String createSecondStoreMutation = """
            mutation {
                createStore(
                    userId: "%s",
                    name: "Second Store",
                    description: "Second store description",
                    latitude: 40.7129,
                    longitude: -74.0061,
                    city: "New York",
                    address: "456 Broadway",
                    imageUrl: "second-store.jpg"
                ) {
                    id
                    name
                }
            }
            """.formatted(testUser.getId());

        graphQlTester
                .document(createSecondStoreMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });

        // 3. Create multiple stores for tier 1 user (up to 3)
        for (int i = 1; i <= 3; i++) {
            String createTierOneStoreMutation = """
                mutation {
                    createStore(
                        userId: "%s",
                        name: "Tier One Store %d",
                        description: "Store description %d",
                        latitude: 51.50%d,
                        longitude: -0.12%d,
                        city: "London",
                        address: "%d Oxford Street",
                        imageUrl: "store-%d.jpg"
                    ) {
                        id
                        name
                    }
                }
                """.formatted(tierOneUser.getId(), i, i, i, i, i * 100, i);

            graphQlTester
                    .document(createTierOneStoreMutation)
                    .execute()
                    .path("createStore.name").entity(String.class).isEqualTo("Tier One Store " + i);
        }

        // 4. Try to create a fourth store for tier 1 user (should fail)
        String createFourthStoreMutation = """
            mutation {
                createStore(
                    userId: "%s",
                    name: "Fourth Store",
                    description: "Fourth store description",
                    latitude: 51.504,
                    longitude: -0.124,
                    city: "London",
                    address: "400 Oxford Street",
                    imageUrl: "store-4.jpg"
                ) {
                    id
                    name
                }
            }
            """.formatted(tierOneUser.getId());

        graphQlTester
                .document(createFourthStoreMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });
    }

    @Test
    public void testNonExistentStoreOperations() {
        // 1. Try to get non-existent store by ID
        String getNonExistentStoreQuery = """
            query {
                storeById(id: 999999) {
                    id
                    name
                }
            }
            """;

        graphQlTester
                .document(getNonExistentStoreQuery)
                .execute()
                .path("storeById")
                .valueIsNull();

        // 2. Try to get non-existent store by slug
        String getNonExistentStoreBySlugQuery = """
            query {
                storeBySlug(slug: "non-existent-store") {
                    id
                    name
                }
            }
            """;

        graphQlTester
                .document(getNonExistentStoreBySlugQuery)
                .execute()
                .path("storeBySlug")
                .valueIsNull();

        // 3. Try to update non-existent store
        String updateNonExistentStoreMutation = """
            mutation {
                updateStore(
                    id: 999999,
                    name: "Updated Non-existent Store",
                    description: "This store doesn't exist",
                    latitude: 0.0,
                    longitude: 0.0,
                    city: "Nowhere",
                    address: "404 Not Found Street",
                    imageUrl: "not-found.jpg"
                ) {
                    id
                    name
                }
            }
            """;

        graphQlTester
                .document(updateNonExistentStoreMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });

        // 4. Try to delete non-existent store
        String deleteNonExistentStoreMutation = """
            mutation {
                deleteStore(id: 999999)
            }
            """;

        graphQlTester
                .document(deleteNonExistentStoreMutation)
                .execute()
                .path("deleteStore").entity(Boolean.class).isEqualTo(false);
    }

    @Test
    public void testCreateStoreWithInvalidUser() {
        // Try to create store with non-existent user ID
        String createStoreWithInvalidUserMutation = """
            mutation {
                createStore(
                    userId: "non-existent-user-id",
                    name: "Invalid User Store",
                    description: "This user doesn't exist",
                    latitude: 0.0,
                    longitude: 0.0,
                    city: "Nowhere",
                    address: "404 Not Found Street",
                    imageUrl: "not-found.jpg"
                ) {
                    id
                    name
                }
            }
            """;

        graphQlTester
                .document(createStoreWithInvalidUserMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });
    }

    @Test
    public void testCreateStoreWithInvalidData() {
        // Try to create store with invalid latitude/longitude
        String createStoreWithInvalidLatLngMutation = """
            mutation {
                createStore(
                    userId: "%s",
                    name: "Invalid Store",
                    description: "This store has invalid coordinates",
                    latitude: 100.0,
                    longitude: 200.0,
                    city: "Invalid City",
                    address: "Invalid Address",
                    imageUrl: "invalid.jpg"
                ) {
                    id
                    name
                }
            }
            """.formatted(testUser.getId());

        graphQlTester
                .document(createStoreWithInvalidLatLngMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });

        // Try to create store with missing required fields
        String createStoreWithMissingFieldsMutation = """
            mutation {
                createStore(
                    userId: "%s",
                    name: "",
                    description: "Store with missing required fields",
                    latitude: 51.5074,
                    longitude: -0.1278,
                    city: "",
                    address: "",
                    imageUrl: "store.jpg"
                ) {
                    id
                    name
                }
            }
            """.formatted(testUser.getId());

        graphQlTester
                .document(createStoreWithMissingFieldsMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });
    }
}