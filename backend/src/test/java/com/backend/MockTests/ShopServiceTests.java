package com.backend.MockTests;

import com.backend.model.Shop;
import com.backend.model.User;
import com.backend.repository.ShopRepository;
import com.backend.repository.UserRepository;
import com.backend.service.ShopService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShopServiceTests {

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ShopService shopService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetShopById_ShopExists() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("johndoe@example.com");
        user.setPassword("hashedPassword");
        user.setPhoneNumber("+48123456789");
        user.setTier(1);
        user = userRepository.save(user);
        Shop shop = new Shop(user,"Shop1", "Great shop", 50.0, 20.0, "CityA", "Street 1", "image.jpg", "test");
        shop.setId(1L);

        when(shopRepository.findById(1L)).thenReturn(Optional.of(shop));

        Optional<Shop> result = shopService.getShopById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(shopRepository, times(1)).findById(1L);
    }

    @Test
    void testGetShopById_ShopDoesNotExist() {
        when(shopRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Shop> result = shopService.getShopById(2L);

        assertFalse(result.isPresent());
        verify(shopRepository, times(1)).findById(2L);
    }

    @Test
    void testSaveShop() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("johndoe@example.com");
        user.setPassword("hashedPassword");
        user.setPhoneNumber("+48123456789");
        user.setTier(1);
        when(userRepository.save(user)).thenReturn(user);
        user = userRepository.save(user);
        Shop shop = new Shop(user,"Shop2", "Another shop", 45.0, 19.0, "CityB", "Street 2", "image2.jpg","test");
        when(shopRepository.save(shop)).thenReturn(shop);

        Shop savedShop = shopService.saveShop(shop);

        assertNotNull(savedShop);
        assertEquals("Shop2", savedShop.getName());
        verify(shopRepository, times(1)).save(shop);
    }

    @Test
    void testUpdateShop_Success() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("johndoe@example.com");
        user.setPassword("hashedPassword");
        user.setPhoneNumber("+48123456789");
        user.setTier(1);
        user = userRepository.save(user);
        Shop existingShop = new Shop(user,"Old Shop", "Old desc", 10.0, 10.0, "Old City", "Old Address", "old.jpg","test");
        existingShop.setId(3L);

        when(shopRepository.findById(3L)).thenReturn(Optional.of(existingShop));
        when(shopRepository.save(any(Shop.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Shop updatedShop = shopService.updateShop(
                3L,
                "New Shop",
                "New description",
                55.5,
                22.2,
                "New City",
                "New Address",
                "new.jpg"
        );

        assertEquals("New Shop", updatedShop.getName());
        assertEquals("New description", updatedShop.getDescription());
        assertEquals(55.5, updatedShop.getLatitude());
        assertEquals(22.2, updatedShop.getLongitude());
        assertEquals("New City", updatedShop.getCity());
        assertEquals("New Address", updatedShop.getAddress());
        assertEquals("new.jpg", updatedShop.getImageUrl());
        verify(shopRepository, times(1)).findById(3L);
        verify(shopRepository, times(1)).save(existingShop);
    }

    @Test
    void testUpdateShop_ShopNotFound() {
        when(shopRepository.findById(4L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            shopService.updateShop(4L, "New Name", "New Desc", 0.0, 0.0, "New City", "New Address", "new.jpg");
        });

        assertEquals("Shop not found", exception.getMessage());
        verify(shopRepository, times(1)).findById(4L);
        verify(shopRepository, times(0)).save(any());
    }

    @Test
    void testDeleteShopById_ShopExists() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("johndoe@example.com");
        user.setPassword("hashedPassword");
        user.setPhoneNumber("+48123456789");
        user.setTier(1);
        user = userRepository.save(user);
        Shop shop = new Shop(user, "Deletable Shop", "desc", 1.0, 1.0, "City", "Address", "img.jpg", "test");
        shop.setId(5L);

        when(shopRepository.findById(5L)).thenReturn(Optional.of(shop));

        Boolean result = shopService.deleteShopById(5L);

        assertTrue(result);
        verify(shopRepository, times(1)).deleteById(5L);
    }

    @Test
    void testDeleteShopById_ShopDoesNotExist() {
        when(shopRepository.findById(6L)).thenReturn(Optional.empty());

        Boolean result = shopService.deleteShopById(6L);

        assertFalse(result);
        verify(shopRepository, never()).deleteById(any());
    }

    @Test
    void testGetAllShops() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("johndoe@example.com");
        user.setPassword("hashedPassword");
        user.setPhoneNumber("+48123456789");
        user.setTier(1);
        user = userRepository.save(user);
        List<Shop> shops = List.of(
                new Shop(user,"ShopA", "descA", 11.0, 11.0, "CityA", "AddressA", "imgA.jpg","test"),
                new Shop(user,"ShopB", "descB", 22.0, 22.0, "CityB", "AddressB", "imgB.jpg", "test")
        );

        when(shopRepository.findAll()).thenReturn(shops);

        List<Shop> result = shopService.getAllShops();

        assertEquals(2, result.size());
        verify(shopRepository, times(1)).findAll();
    }
}
