package com.backend.IntegrationTests;

import com.backend.model.Shop;
import com.backend.model.User;
import com.backend.repository.ShopRepository;
import com.backend.repository.UserRepository;
import com.backend.service.ShopService;
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
class ShopServiceTests {

    @Autowired
    private ShopService shopService;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserRepository userRepository;

    private Shop createTestShop() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("johndoe@example.com");
        user.setPassword("hashedPassword");
        user.setPhoneNumber("+48123456789");
        user.setTier(1);
        user = userRepository.save(user);

        Shop shop = new Shop();
        shop.setName("Test Shop");
        shop.setUser(user);
        shop.setDescription("Test Description");
        shop.setLatitude(12.3456);
        shop.setLongitude(65.4321);
        shop.setCity("Test City");
        shop.setAddress("123 Test Street");
        shop.setImageUrl("http://example.com/image.jpg");
        return shop;
    }

    @BeforeEach
    void setUp() {
        shopRepository.deleteAll();
        userRepository.deleteAll();// Clean up
    }

    @Test
    void testSaveShop() {
        Shop shop = createTestShop();
        Shop saved = shopService.saveShop(shop);

        assertNotNull(saved.getId());
        assertEquals("Test Shop", saved.getName());
    }

    @Test
    void testGetShopById_Found() {
        Shop saved = shopService.saveShop(createTestShop());

        Optional<Shop> found = shopService.getShopById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void testGetShopById_NotFound() {
        Optional<Shop> found = shopService.getShopById(999L);

        assertFalse(found.isPresent());
    }

    @Test
    void testUpdateShop_Success() {
        Shop saved = shopService.saveShop(createTestShop());

        Shop updated = shopService.updateShop(saved.getId(),
                "Updated Shop", "Updated Description", 11.1111, 22.2222,
                "Updated City", "456 Updated Street", "http://example.com/updated.jpg");

        assertEquals("Updated Shop", updated.getName());
        assertEquals("Updated Description", updated.getDescription());
        assertEquals(11.1111, updated.getLatitude());
        assertEquals(22.2222, updated.getLongitude());
        assertEquals("Updated City", updated.getCity());
        assertEquals("456 Updated Street", updated.getAddress());
        assertEquals("http://example.com/updated.jpg", updated.getImageUrl());
    }

    @Test
    void testUpdateShop_NotFound() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            shopService.updateShop(999L, "New", null, null, null, null, null, null);
        });

        assertEquals("Shop not found", exception.getMessage());
    }

    @Test
    void testDeleteShopById_Success() {
        Shop saved = shopService.saveShop(createTestShop());

        Boolean result = shopService.deleteShopById(saved.getId());

        assertTrue(result);
        assertFalse(shopRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void testDeleteShopById_NotFound() {
        Boolean result = shopService.deleteShopById(404L);

        assertFalse(result);
    }

    @Test
    void testGetAllShops() {
        shopService.saveShop(createTestShop());
//        shopService.saveShop(createTestShop());

        List<Shop> shops = shopService.getAllShops();

//        assertEquals(2, shops.size());
        assertEquals(1, shops.size());
    }
}
