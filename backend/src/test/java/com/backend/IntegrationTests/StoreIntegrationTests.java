package com.backend.IntegrationTests;

import com.backend.model.Store;
import com.backend.model.User;
import com.backend.repository.StoreRepository;
import com.backend.repository.UserRepository;
import com.backend.service.StoreService;
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
class StoreIntegrationTests {

    @Autowired
    private StoreService storeService;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    private Store createTestShop() {
        User user = new User();
        user.setId("1");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("johndoe@example.com");
        user.setPhoneNumber("+48123456789");
        user.setTier(1);
        user = userRepository.save(user);

        Store store = new Store();
        store.setName("Test Shop");
        store.setUser(user);
        store.setDescription("Test Description");
        store.setLatitude(12.3456);
        store.setLongitude(65.4321);
        store.setCity("Test City");
        store.setAddress("123 Test Street");
        store.setImageUrl("http://example.com/image.jpg");
        return store;
    }

    @BeforeEach
    void setUp() {
        storeRepository.deleteAll();
        userRepository.deleteAll();// Clean up
    }

    @Test
    void testSaveStore() {
        Store store = createTestShop();
        Store saved = storeService.saveStore(store);

        assertNotNull(saved.getId());
        assertEquals("Test Shop", saved.getName());
    }

    @Test
    void testGetStoreById_Found() {
        Store saved = storeService.saveStore(createTestShop());

        Optional<Store> found = storeService.getStoreById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void testGetStoreById_NotFound() {
        Optional<Store> found = storeService.getStoreById(999L);

        assertFalse(found.isPresent());
    }

    @Test
    void testUpdateStore_Success() {
        Store saved = storeService.saveStore(createTestShop());

        Store updated = storeService.updateStore(saved.getId(),
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
    void testUpdateStore_NotFound() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            storeService.updateStore(999L, "New", null, null, null, null, null, null);
        });

        assertEquals("Store not found", exception.getMessage());
    }

    @Test
    void testDeleteStoreById_Success() {
        Store saved = storeService.saveStore(createTestShop());

        Boolean result = storeService.deleteStoreById(saved.getId());

        assertTrue(result);
        assertFalse(storeRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void testDeleteStoreById_NotFound() {
        Boolean result = storeService.deleteStoreById(404L);

        assertFalse(result);
    }

    @Test
    void testGetAllStores() {
        storeService.saveStore(createTestShop());
//        shopService.saveShop(createTestShop());

        List<Store> stores = storeService.getAllStores();

//        assertEquals(2, shops.size());
        assertEquals(1, stores.size());
    }

}
