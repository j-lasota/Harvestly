package com.backend.IntegrationTests;

import com.backend.model.Store;
import com.backend.model.User;
import com.backend.repository.StoreRepository;
import com.backend.repository.UserRepository;
import com.backend.repository.OpinionRepository;

import com.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserIntegrationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testGetAllUsers() {
        userRepository.save(new User("user1", "John", "Doe", "john@example.com", "123456789", 0, "john.jpg"));
        userRepository.save(new User("user2", "Jane", "Smith", "jane@example.com", "987654321", 0, "jane.jpg"));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
    }

    @Test
    void testGetUserById_UserExists() {
        User user = userRepository.save(new User("user3", "Bob", "Johnson", "bob@example.com", "555555555", 0, "bob.jpg"));

        Optional<User> result = userService.getUserById(user.getId());

        assertTrue(result.isPresent());
        assertEquals("Bob", result.get().getFirstName());
        assertEquals("Johnson", result.get().getLastName());
    }

    @Test
    void testGetUserById_UserDoesNotExist() {
        Optional<User> result = userService.getUserById("non-existent-id");

        assertFalse(result.isPresent());
    }

    @Test
    void testSaveUser_Success() {
        User user = new User(
                "user4",
                "Alice",
                "Williams",
                "alice@example.com",
                "444444444",
                0,
                "alice.jpg"
        );

        User savedUser = userService.saveUser(user);

        assertEquals("user4", savedUser.getId());
        assertEquals("Alice", savedUser.getFirstName());
        assertEquals("Williams", savedUser.getLastName());
        assertEquals("alice@example.com", savedUser.getEmail());
    }

    @Test
    void testSaveUser_DuplicateEmail() {
        userRepository.save(new User("user5", "Charlie", "Brown", "charlie@example.com", "777777777", 0, "charlie.jpg"));

        User duplicateUser = new User(
                "user6",
                "Charlie",
                "Brown",
                "charlie@example.com",
                "888888888",
                0,
                "charlie2.jpg"
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.saveUser(duplicateUser);
        });

        assertEquals("User with the same email already exists.", exception.getMessage());
    }

    @Test
    void testSaveUser_DuplicatePhoneNumber() {
        userRepository.save(new User("user7", "David", "Miller", "david@example.com", "999999999", 0, "david.jpg"));

        User duplicateUser = new User(
                "user8",
                "Daniel",
                "Wilson",
                "daniel@example.com",
                "999999999",
                0,
                "daniel.jpg"
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.saveUser(duplicateUser);
        });

        assertEquals("User with the same phone number already exists.", exception.getMessage());
    }

    @Test
    void testUpdateUser_Success() {
        User user = userRepository.save(new User("user9", "Emma", "Davis", "emma@example.com", "111222333", 0, "emma.jpg"));

        User updatedUser = userService.updateUser(
                user.getId(),
                "Emily",
                "Davidson",
                "emily@example.com",
                "111222444",
                1,
                "emily.jpg"
        );

        assertEquals("Emily", updatedUser.getFirstName());
        assertEquals("Davidson", updatedUser.getLastName());
        assertEquals("emily@example.com", updatedUser.getEmail());
        assertEquals("111222444", updatedUser.getPhoneNumber());
        assertEquals(1, updatedUser.getTier());
        assertEquals("emily.jpg", updatedUser.getImg());
    }

    @Test
    void testUpdateUser_PartialUpdate() {
        User user = userRepository.save(new User("user10", "Frank", "Thomas", "frank@example.com", "222333444", 0, "frank.jpg"));

        User updatedUser = userService.updateUser(
                user.getId(),
                "Francis",
                null,
                null,
                null,
                1,
                null
        );

        assertEquals("Francis", updatedUser.getFirstName());
        assertEquals("Thomas", updatedUser.getLastName());
        assertEquals("frank@example.com", updatedUser.getEmail());
        assertEquals("222333444", updatedUser.getPhoneNumber());
        assertEquals(1, updatedUser.getTier());
        assertEquals("frank.jpg", updatedUser.getImg());
    }

    @Test
    void testUpdateUser_UserNotFound() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(
                    "non-existent-id",
                    "Test",
                    "User",
                    "test@example.com",
                    "123123123",
                    0,
                    "test.jpg"
            );
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testDeleteUserById_UserExists() {
        User user = userRepository.save(new User("user11", "George", "Brown", "george@example.com", "333444555", 0, "george.jpg"));
        String id = user.getId();

        Boolean result = userService.deleteUserById(id);

        assertTrue(result);
        assertFalse(userRepository.findById(id).isPresent());
    }

    @Test
    void testDeleteUserById_UserDoesNotExist() {
        Boolean result = userService.deleteUserById("non-existent-id");

        assertFalse(result);
    }

    @Test
    void testFindByEmail_UserExists() {
        userRepository.save(new User("user12", "Harry", "Wilson", "harry@example.com", "444555666", 0, "harry.jpg"));

        Optional<User> result = userService.findByEmail("harry@example.com");

        assertTrue(result.isPresent());
        assertEquals("Harry", result.get().getFirstName());
    }

    @Test
    void testFindByEmail_UserDoesNotExist() {
        Optional<User> result = userService.findByEmail("nonexistent@example.com");

        assertFalse(result.isPresent());
    }

    @Test
    @Transactional
    void testAddFavoriteShop() {
        User user = userRepository.save(new User("user13", "Ian", "Scott", "ian@example.com", "555666777", 0, "ian.jpg"));
        User storeOwner = userRepository.save(new User("owner1", "Owner", "One", "owner1@example.com", "666777888", 0, "owner1.jpg"));

        Store store = new Store(
                storeOwner,
                "Test Store",
                "A test store",
                45.0,
                45.0,
                "Test City",
                "123 Test St",
                "store.jpg",
                "test-store"
        );
        store = storeRepository.save(store);

        User updatedUser = userService.addFavoriteShop(user.getId(), store.getId());

        assertEquals(1, updatedUser.getFavoriteStores().size());
        assertTrue(updatedUser.getFavoriteStores().contains(store));
    }

    @Test
    void testAddFavoriteShop_UserNotFound() {
        User storeOwner = userRepository.save(new User("owner2", "Owner", "Two", "owner2@example.com", "777888999", 0, "owner2.jpg"));
        Store store = new Store(
                storeOwner,
                "Another Store",
                "Another test store",
                46.0,
                46.0,
                "Another City",
                "456 Test Ave",
                "store2.jpg",
                "another-store"
        );
        final Store savedStore = storeRepository.save(store);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.addFavoriteShop("non-existent-id", savedStore.getId());
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testAddFavoriteShop_StoreNotFound() {
        User user = userRepository.save(new User("user14", "Jack", "Roberts", "jack@example.com", "888999000", 0, "jack.jpg"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.addFavoriteShop(user.getId(), 999L);
        });

        assertEquals("Shop not found", exception.getMessage());
    }

    @Test
    @Transactional
    void testRemoveFavoriteShop() {
        User user = userRepository.save(new User("user15", "Kate", "Johnson", "kate@example.com", "999000111", 0, "kate.jpg"));
        User storeOwner = userRepository.save(new User("owner3", "Owner", "Three", "owner3@example.com", "000111222", 0, "owner3.jpg"));

        Store store = new Store(
                storeOwner,
                "Kate's Favorite Store",
                "A store Kate likes",
                47.0,
                47.0,
                "Kate's City",
                "789 Kate St",
                "kate-store.jpg",
                "kates-store"
        );
        store = storeRepository.save(store);

        userService.addFavoriteShop(user.getId(), store.getId());

        User updatedUser = userService.removeFavoriteShop(user.getId(), store.getId());

        assertEquals(0, updatedUser.getFavoriteStores().size());
    }
}