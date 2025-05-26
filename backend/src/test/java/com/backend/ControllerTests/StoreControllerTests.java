package com.backend.ControllerTests;

import com.backend.controller.StoreController;
import com.backend.model.Store;
import com.backend.model.User;
import com.backend.repository.StoreRepository;
import com.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class StoreControllerTests {

    @Autowired
    private StoreController storeController;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        storeRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3cfd62","John", "Doe", "john.doe@example.com", "+48123456789", 1, null);
        testUser = userRepository.save(testUser);
    }

    @Test
    void testCreateShop_Success() {
        Store created = storeController.createStore(
                testUser.getId(),
                "Test Shop",
                "Great products",
                45.0,
                90.0,
                "City",
                "123 Main St",
                "http://img.jpg"
        );

        assertNotNull(created.getId());
        assertEquals("Test Shop", created.getName());
        assertEquals(testUser.getId(), created.getUser().getId());
    }

    @Test
    void testGetShopById_Found() {
        Store created = storeController.createStore(
                testUser.getId(),
                "Shop1",
                "Desc",
                10.0,
                20.0,
                "City",
                "Address",
                "http://img.jpg"
        );

        Optional<Store> found = storeController.storeById(created.getId());
        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
    }

    @Test
    void testGetShopById_NotFound() {
        Optional<Store> found = storeController.storeById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    void testUpdateShop_Success() {
        Store created = storeController.createStore(
                testUser.getId(),
                "Old Name",
                "Old Desc",
                1.0,
                2.0,
                "Old City",
                "Old Address",
                "http://old.jpg"
        );

        Store updated = storeController.updateStore(
                created.getId(),
                "New Name",
                "New Desc",
                5.0,
                10.0,
                "New City",
                "New Address",
                "http://new.jpg"
        );

        assertEquals("New Name", updated.getName());
        assertEquals("New Desc", updated.getDescription());
        assertEquals(5.0, updated.getLatitude());
        assertEquals("New City", updated.getCity());
    }

    @Test
    void testUpdateShop_NotFound() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            storeController.updateStore(999L, "Name", "Desc", 0.0, 0.0, "City", "Address", "url");
        });
        assertEquals("Store not found", exception.getMessage());
    }

    @Test
    void testDeleteShopById_Success() {
        Store created = storeController.createStore(
                testUser.getId(),
                "Shop to delete",
                "Desc",
                0.0,
                0.0,
                "City",
                "Addr",
                "url"
        );

        Boolean deleted = storeController.deleteStore(created.getId());
        assertTrue(deleted);
        assertFalse(storeRepository.findById(created.getId()).isPresent());
    }

    @Test
    void testDeleteShopById_NotFound() {
        Boolean deleted = storeController.deleteStore(123456L);
        assertFalse(deleted);
    }

    @Test
    void testGetAllShops() {
        storeController.createStore(
                testUser.getId(),
                "Shop 1",
                "Desc",
                1.0,
                1.0,
                "City",
                "Addr",
                "url"
        );
        storeController.createStore(
                testUser.getId(),
                "Shop 2",
                "Desc",
                2.0,
                2.0,
                "City",
                "Addr",
                "url"
        );

        List<Store> stores = storeController.stores();
        assertEquals(2, stores.size());
    }

    @Test
    void testCreateShop_SlugUniqueness() {
        Store first = storeController.createStore(
                testUser.getId(),
                "Duplicate Name",
                "Desc1",
                1.0,
                1.0,
                "City",
                "Addr1",
                "url1"
        );

        Store second = storeController.createStore(
                testUser.getId(),
                "Duplicate Name",
                "Desc2",
                2.0,
                2.0,
                "City",
                "Addr2",
                "url2"
        );

        assertNotEquals(first.getSlug(), second.getSlug());
        assertEquals("duplicate-name", first.getSlug());
        assertEquals("duplicate-name-1", second.getSlug());
    }
}
