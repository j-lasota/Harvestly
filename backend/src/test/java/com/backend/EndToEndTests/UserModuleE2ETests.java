package com.backend.EndToEndTests;

import com.backend.model.Store;
import com.backend.model.User;
import com.backend.repository.OpinionRepository;
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
public class UserModuleE2ETests {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OpinionRepository opinionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreService storeService;

    private Store testStore;
    private User testUser;
    private String testUserId;

    @BeforeEach
    public void setUp() {
        // Clean up existing data
        userRepository.deleteAll();

        // Generate a unique ID for the test user
        testUserId = UUID.randomUUID().toString();
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
        if (testStore != null) {
            storeRepository.delete(testStore);
        }
    }

    @Test
    @Transactional
    public void testCompleteUserLifecycle() {
        // 1. Create a new user through GraphQL
        String createUserMutation = """
            mutation {
                createUser(
                    id: "%s",
                    firstName: "John",
                    lastName: "Doe",
                    email: "john.doe@example.com",
                    phoneNumber: "+1234567890",
                    img: "profile.jpg"
                ) {
                    id
                    firstName
                    lastName
                    email
                    phoneNumber
                    tier
                    img
                }
            }
            """.formatted(testUserId);

        // Execute the mutation and verify the response
        GraphQlTester.Response createResponse = graphQlTester
                .document(createUserMutation)
                .execute();

        createResponse
                .path("createUser.id").entity(String.class).isEqualTo(testUserId)
                .path("createUser.firstName").entity(String.class).isEqualTo("John")
                .path("createUser.lastName").entity(String.class).isEqualTo("Doe")
                .path("createUser.email").entity(String.class).isEqualTo("john.doe@example.com")
                .path("createUser.phoneNumber").entity(String.class).isEqualTo("+1234567890")
                .path("createUser.tier").entity(Integer.class).isEqualTo(0)
                .path("createUser.img").entity(String.class).isEqualTo("profile.jpg");

        // 2. Verify user exists in database
        Optional<User> savedUser = userService.getUserById(testUserId);
        assertTrue(savedUser.isPresent());
        assertEquals("John", savedUser.get().getFirstName());
        assertEquals("Doe", savedUser.get().getLastName());
        assertEquals("john.doe@example.com", savedUser.get().getEmail());

        // 3. Get the user by ID using GraphQL
        String getUserQuery = """
            query {
                userById(id: "%s") {
                    id
                    firstName
                    lastName
                    email
                    phoneNumber
                    tier
                    img
                }
            }
            """.formatted(testUserId);

        graphQlTester
                .document(getUserQuery)
                .execute()
                .path("userById.id").entity(String.class).isEqualTo(testUserId)
                .path("userById.firstName").entity(String.class).isEqualTo("John")
                .path("userById.lastName").entity(String.class).isEqualTo("Doe")
                .path("userById.email").entity(String.class).isEqualTo("john.doe@example.com")
                .path("userById.phoneNumber").entity(String.class).isEqualTo("+1234567890")
                .path("userById.tier").entity(Integer.class).isEqualTo(0)
                .path("userById.img").entity(String.class).isEqualTo("profile.jpg");

        // 4. Get the user by email using GraphQL
        String getUserByEmailQuery = """
            query {
                userByEmail(email: "john.doe@example.com") {
                    id
                    firstName
                    lastName
                    email
                }
            }
            """;

        graphQlTester
                .document(getUserByEmailQuery)
                .execute()
                .path("userByEmail.id").entity(String.class).isEqualTo(testUserId)
                .path("userByEmail.firstName").entity(String.class).isEqualTo("John")
                .path("userByEmail.lastName").entity(String.class).isEqualTo("Doe")
                .path("userByEmail.email").entity(String.class).isEqualTo("john.doe@example.com");

        // 5. Update the user
        String updateUserMutation = """
            mutation {
                updateUser(
                    id: "%s",
                    firstName: "John",
                    lastName: "Smith",
                    email: "john.smith@example.com",
                    phoneNumber: "+9876543210",
                    tier: 1,
                    img: "updated-profile.jpg"
                ) {
                    id
                    firstName
                    lastName
                    email
                    phoneNumber
                    tier
                    img
                }
            }
            """.formatted(testUserId);

        graphQlTester
                .document(updateUserMutation)
                .execute()
                .path("updateUser.id").entity(String.class).isEqualTo(testUserId)
                .path("updateUser.firstName").entity(String.class).isEqualTo("John")
                .path("updateUser.lastName").entity(String.class).isEqualTo("Smith")
                .path("updateUser.email").entity(String.class).isEqualTo("john.smith@example.com")
                .path("updateUser.phoneNumber").entity(String.class).isEqualTo("+9876543210")
                .path("updateUser.tier").entity(Integer.class).isEqualTo(1)
                .path("updateUser.img").entity(String.class).isEqualTo("updated-profile.jpg");

        // 6. Verify update in the database
        User updatedUser = userService.getUserById(testUserId).orElseThrow();
        assertEquals("Smith", updatedUser.getLastName());
        assertEquals("john.smith@example.com", updatedUser.getEmail());
        assertEquals("+9876543210", updatedUser.getPhoneNumber());
        assertEquals(1, updatedUser.getTier());

        // 7. Test getting all users
        String getAllUsersQuery = """
            query {
                users {
                    id
                    firstName
                    lastName
                    email
                }
            }
            """;

        List<User> allUsers = userService.getAllUsers();
        assertEquals(1, allUsers.size());

        graphQlTester
                .document(getAllUsersQuery)
                .execute()
                .path("users").entityList(User.class).hasSize(1);

        // 8. Delete the user
        String deleteUserMutation = """
            mutation {
                deleteUser(id: "%s")
            }
            """.formatted(testUserId);

        graphQlTester
                .document(deleteUserMutation)
                .execute()
                .path("deleteUser").entity(Boolean.class).isEqualTo(true);

        // 9. Verify user was deleted
        Optional<User> deletedUser = userService.getUserById(testUserId);
        assertTrue(deletedUser.isEmpty());
    }

    @Test
    public void testUserEmailUniquenessValidation() {
        // 1. Create first user
        String createFirstUserMutation = """
            mutation {
                createUser(
                    id: "%s",
                    firstName: "John",
                    lastName: "Doe",
                    email: "unique.email@example.com",
                    phoneNumber: "+1234567890",
                    img: "profile.jpg"
                ) {
                    id
                }
            }
            """.formatted(UUID.randomUUID().toString());

        graphQlTester
                .document(createFirstUserMutation)
                .execute()
                .path("createUser.id").entity(String.class).isNotEqualTo(null);

        // 2. Try to create a second user with the same email
        String createSecondUserMutation = """
            mutation {
                createUser(
                    id: "%s",
                    firstName: "Jane",
                    lastName: "Smith",
                    email: "unique.email@example.com",
                    phoneNumber: "+9876543210",
                    img: "profile2.jpg"
                ) {
                    id
                }
            }
            """.formatted(UUID.randomUUID().toString());

        graphQlTester.document(createSecondUserMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });
    }

    @Test
    public void testUserPhoneNumberUniquenessValidation() {
        // 1. Create first user
        String createFirstUserMutation = """
            mutation {
                createUser(
                    id: "%s",
                    firstName: "John",
                    lastName: "Doe",
                    email: "first.user@example.com",
                    phoneNumber: "+1234567890",
                    img: "profile.jpg"
                ) {
                    id
                }
            }
            """.formatted(UUID.randomUUID().toString());

        graphQlTester
                .document(createFirstUserMutation)
                .execute()
                .path("createUser.id").entity(String.class).isNotEqualTo(null);

        // 2. Try to create a second user with the same phone number
        String createSecondUserMutation = """
            mutation {
                createUser(
                    id: "%s",
                    firstName: "Jane",
                    lastName: "Smith",
                    email: "second.user@example.com",
                    phoneNumber: "+1234567890",
                    img: "profile2.jpg"
                ) {
                    id
                }
            }
            """.formatted(UUID.randomUUID().toString());

        graphQlTester.document(createSecondUserMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });
    }

    @Test
    @Transactional
    public void testPartialUserUpdate() {
        // 1. Create a user
        String createUserMutation = """
            mutation {
                createUser(
                    id: "%s",
                    firstName: "John",
                    lastName: "Doe",
                    email: "john.doe@example.com",
                    phoneNumber: "+1234567890",
                    img: "profile.jpg"
                ) {
                    id
                }
            }
            """.formatted(testUserId);

        graphQlTester
                .document(createUserMutation)
                .execute()
                .path("createUser.id").entity(String.class).isNotEqualTo(null);

        // 2. Update only the firstName
        String updateFirstNameMutation = """
            mutation {
                updateUser(
                    id: "%s",
                    firstName: "Jonathan"
                ) {
                    id
                    firstName
                    lastName
                    email
                }
            }
            """.formatted(testUserId);

        graphQlTester
                .document(updateFirstNameMutation)
                .execute()
                .path("updateUser.firstName").entity(String.class).isEqualTo("Jonathan")
                .path("updateUser.lastName").entity(String.class).isEqualTo("Doe") // Should remain unchanged
                .path("updateUser.email").entity(String.class).isEqualTo("john.doe@example.com"); // Should remain unchanged

        // 3. Update only the tier
        String updateTierMutation = """
            mutation {
                updateUser(
                    id: "%s",
                    tier: 1
                ) {
                    id
                    firstName
                    tier
                }
            }
            """.formatted(testUserId);

        graphQlTester
                .document(updateTierMutation)
                .execute()
                .path("updateUser.firstName").entity(String.class).isEqualTo("Jonathan") // Should remain unchanged
                .path("updateUser.tier").entity(Integer.class).isEqualTo(1);
    }

    @Test
    @Transactional
    public void testFavoriteStoreOperations() {
        // 1. Create a user
        String createUserMutation = """
            mutation {
                createUser(
                    id: "%s",
                    firstName: "John",
                    lastName: "Doe",
                    email: "john.doe@example.com",
                    phoneNumber: "+1234567890",
                    img: "profile.jpg"
                ) {
                    id
                }
            }
            """.formatted(testUserId);

        graphQlTester
                .document(createUserMutation)
                .execute()
                .path("createUser.id").entity(String.class).isNotEqualTo(null);

        // 2. Create a store owner user
        String storeOwnerId = UUID.randomUUID().toString();
        User storeOwner = new User(
                storeOwnerId,
                "Store",
                "Owner",
                "store.owner@example.com",
                "+0987654321",
                0,
                "owner.jpg"
        );
        storeOwner = userRepository.save(storeOwner);

        // 3. Create a store
        Store store = new Store(
                storeOwner,
                "Test Store",
                "A store for testing",
                45.0,
                45.0,
                "Test City",
                "123 Test Street",
                "store-img.jpg",
                "test-store"
        );
        testStore = storeRepository.save(store);

        // 4. Add the store to user's favorites
        String addFavoriteStoreMutation = """
            mutation {
                addFavoriteStore(
                    userId: "%s",
                    storeId: %d
                ) {
                    id
                    firstName
                    favoriteStores {
                        id
                        name
                    }
                }
            }
            """.formatted(testUserId, testStore.getId());

        graphQlTester
                .document(addFavoriteStoreMutation)
                .execute()
                .path("addFavoriteStore.id").entity(String.class).isEqualTo(testUserId)
                .path("addFavoriteStore.favoriteStores[0].id").entity(String.class).isEqualTo(testStore.getId().toString())
                .path("addFavoriteStore.favoriteStores[0].name").entity(String.class).isEqualTo("Test Store");

        // 5. Verify the favorite store was added in the database
        User userWithFavorite = userService.getUserById(testUserId).orElseThrow();
        assertEquals(1, userWithFavorite.getFavoriteStores().size());
        assertTrue(userWithFavorite.getFavoriteStores().contains(testStore));

        // 6. Remove the store from favorites
        String removeFavoriteStoreMutation = """
            mutation {
                removeFavoriteStore(
                    userId: "%s",
                    storeId: %d
                ) {
                    id
                    favoriteStores {
                        id
                    }
                }
            }
            """.formatted(testUserId, testStore.getId());

        graphQlTester
                .document(removeFavoriteStoreMutation)
                .execute()
                .path("removeFavoriteStore.id").entity(String.class).isEqualTo(testUserId)
                .path("removeFavoriteStore.favoriteStores").entityList(Store.class).hasSize(0);

        // 7. Verify the favorite store was removed in the database
        User userWithoutFavorite = userService.getUserById(testUserId).orElseThrow();
        assertEquals(0, userWithoutFavorite.getFavoriteStores().size());
    }

    @Test
    public void testInvalidFavoriteStoreOperations() {
        // 1. Create a user
        String createUserMutation = """
            mutation {
                createUser(
                    id: "%s",
                    firstName: "John",
                    lastName: "Doe",
                    email: "john.doe@example.com",
                    phoneNumber: "+1234567890",
                    img: "profile.jpg"
                ) {
                    id
                }
            }
            """.formatted(testUserId);

        graphQlTester
                .document(createUserMutation)
                .execute()
                .path("createUser.id").entity(String.class).isNotEqualTo(null);

        // 2. Try to add a non-existent store to favorites
        String invalidStoreMutation = """
            mutation {
                addFavoriteStore(
                    userId: "%s",
                    storeId: 999999
                ) {
                    id
                }
            }
            """.formatted(testUserId);

        graphQlTester.document(invalidStoreMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });

        // 3. Try to add a store to a non-existent user
        String invalidUserMutation = """
            mutation {
                addFavoriteStore(
                    userId: "non-existent-user",
                    storeId: 1
                ) {
                    id
                }
            }
            """;

        graphQlTester.document(invalidUserMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });
    }

    @Test
    public void testInvalidUserOperations() {
        // 1. Try to get non-existent user by ID
        String getNonExistentUserQuery = """
            query {
                userById(id: "non-existent-user") {
                    id
                }
            }
            """;

        graphQlTester
                .document(getNonExistentUserQuery)
                .execute()
                .path("userById")
                .valueIsNull();

        // 2. Try to get non-existent user by email
        String getNonExistentEmailQuery = """
            query {
                userByEmail(email: "nonexistent@example.com") {
                    id
                }
            }
            """;

        graphQlTester
                .document(getNonExistentEmailQuery)
                .execute()
                .path("userByEmail")
                .valueIsNull();

        // 3. Try to delete non-existent user
        String deleteNonExistentUserMutation = """
            mutation {
                deleteUser(id: "non-existent-user")
            }
            """;

        graphQlTester
                .document(deleteNonExistentUserMutation)
                .execute()
                .path("deleteUser").entity(Boolean.class).isEqualTo(false);

        // 4. Try to update non-existent user
        String updateNonExistentUserMutation = """
            mutation {
                updateUser(
                    id: "non-existent-user",
                    firstName: "New Name"
                ) {
                    id
                }
            }
            """;

        graphQlTester.document(updateNonExistentUserMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });
    }
}