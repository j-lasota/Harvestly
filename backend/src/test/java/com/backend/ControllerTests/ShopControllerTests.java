package com.backend.ControllerTests;

import com.backend.controller.ShopController;
import com.backend.model.Shop;
import com.backend.model.User;
import com.backend.repository.ShopRepository;
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
class ShopControllerTests {

    @Autowired
    private ShopController shopController;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        shopRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User("John", "Doe", "john.doe@example.com", "password123", "+48123456789", 1, null);
        testUser = userRepository.save(testUser);
    }

    @Test
    void testCreateShop_Success() {
        Shop created = shopController.createShop(
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
        Shop created = shopController.createShop(
                testUser.getId(),
                "Shop1",
                "Desc",
                10.0,
                20.0,
                "City",
                "Address",
                "http://img.jpg"
        );

        Optional<Shop> found = shopController.shopById(created.getId());
        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
    }

    @Test
    void testGetShopById_NotFound() {
        Optional<Shop> found = shopController.shopById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    void testUpdateShop_Success() {
        Shop created = shopController.createShop(
                testUser.getId(),
                "Old Name",
                "Old Desc",
                1.0,
                2.0,
                "Old City",
                "Old Address",
                "http://old.jpg"
        );

        Shop updated = shopController.updateShop(
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
            shopController.updateShop(999L, "Name", "Desc", 0.0, 0.0, "City", "Address", "url");
        });
        assertEquals("Shop not found", exception.getMessage());
    }

    @Test
    void testDeleteShopById_Success() {
        Shop created = shopController.createShop(
                testUser.getId(),
                "Shop to delete",
                "Desc",
                0.0,
                0.0,
                "City",
                "Addr",
                "url"
        );

        Boolean deleted = shopController.deleteShop(created.getId());
        assertTrue(deleted);
        assertFalse(shopRepository.findById(created.getId()).isPresent());
    }

    @Test
    void testDeleteShopById_NotFound() {
        Boolean deleted = shopController.deleteShop(123456L);
        assertFalse(deleted);
    }

    @Test
    void testGetAllShops() {
        shopController.createShop(
                testUser.getId(),
                "Shop 1",
                "Desc",
                1.0,
                1.0,
                "City",
                "Addr",
                "url"
        );
        shopController.createShop(
                testUser.getId(),
                "Shop 2",
                "Desc",
                2.0,
                2.0,
                "City",
                "Addr",
                "url"
        );

        List<Shop> shops = shopController.shops();
        assertEquals(2, shops.size());
    }

    @Test
    void testCreateShop_SlugUniqueness() {
        Shop first = shopController.createShop(
                testUser.getId(),
                "Duplicate Name",
                "Desc1",
                1.0,
                1.0,
                "City",
                "Addr1",
                "url1"
        );

        Shop second = shopController.createShop(
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
