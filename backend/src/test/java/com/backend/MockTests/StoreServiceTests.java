package com.backend.MockTests;

import com.backend.model.Store;
import com.backend.model.User;
import com.backend.repository.StoreRepository;
import com.backend.repository.UserRepository;
import com.backend.service.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StoreServiceTests {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StoreService storeService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetShopById_StoreExists() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("johndoe@example.com");
        user.setPhoneNumber("+48123456789");
        user.setTier(1);
        user = userRepository.save(user);
        Store store = new Store(user,"Shop1", "Great shop", 50.0, 20.0, "CityA", "Street 1", "image.jpg", "test");
        store.setId(1L);

        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));

        Optional<Store> result = storeService.getStoreById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(storeRepository, times(1)).findById(1L);
    }

    @Test
    void testGetShopById_StoreDoesNotExist() {
        when(storeRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Store> result = storeService.getStoreById(2L);

        assertFalse(result.isPresent());
        verify(storeRepository, times(1)).findById(2L);
    }

    @Test
    void testSaveStore() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("johndoe@example.com");
        user.setPhoneNumber("+48123456789");
        user.setTier(1);
        when(userRepository.save(user)).thenReturn(user);
        user = userRepository.save(user);
        Store store = new Store(user,"Shop2", "Another shop", 45.0, 19.0, "CityB", "Street 2", "image2.jpg","test1");
        when(storeRepository.save(store)).thenReturn(store);

        Store savedStore = storeService.saveStore(store);

        assertNotNull(savedStore);
        assertEquals("Shop2", savedStore.getName());
        verify(storeRepository, times(1)).save(store);
    }

    @Test
    void testUpdateStore_Success() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("johndoe@example.com");
        user.setPhoneNumber("+48123456789");
        user.setTier(1);
        user = userRepository.save(user);
        Store existingStore = new Store(user,"Old Shop", "Old desc", 10.0, 10.0, "Old City", "Old Address", "old.jpg","test");
        existingStore.setId(3L);

        when(storeRepository.findById(3L)).thenReturn(Optional.of(existingStore));
        when(storeRepository.save(any(Store.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Store updatedStore = storeService.updateStore(
                3L,
                "New Shop",
                "New description",
                55.5,
                22.2,
                "New City",
                "New Address",
                "new.jpg"
        );

        assertEquals("New Shop", updatedStore.getName());
        assertEquals("New description", updatedStore.getDescription());
        assertEquals(55.5, updatedStore.getLatitude());
        assertEquals(22.2, updatedStore.getLongitude());
        assertEquals("New City", updatedStore.getCity());
        assertEquals("New Address", updatedStore.getAddress());
        assertEquals("new.jpg", updatedStore.getImageUrl());
        verify(storeRepository, times(1)).findById(3L);
        verify(storeRepository, times(1)).save(existingStore);
    }

    @Test
    void testUpdateShop_StoreNotFound() {
        when(storeRepository.findById(4L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            storeService.updateStore(4L, "New Name", "New Desc", 0.0, 0.0, "New City", "New Address", "new.jpg");
        });

        assertEquals("Store not found", exception.getMessage());
        verify(storeRepository, times(1)).findById(4L);
        verify(storeRepository, times(0)).save(any());
    }

    @Test
    void testDeleteShopById_StoreExists() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("johndoe@example.com");
        user.setPhoneNumber("+48123456789");
        user.setTier(1);
        user = userRepository.save(user);
        Store store = new Store(user, "Deletable Shop", "desc", 1.0, 1.0, "City", "Address", "img.jpg", "test");
        store.setId(5L);

        when(storeRepository.findById(5L)).thenReturn(Optional.of(store));

        Boolean result = storeService.deleteStoreById(5L);

        assertTrue(result);
        verify(storeRepository, times(1)).deleteById(5L);
    }

    @Test
    void testDeleteShopById_StoreDoesNotExist() {
        when(storeRepository.findById(6L)).thenReturn(Optional.empty());

        Boolean result = storeService.deleteStoreById(6L);

        assertFalse(result);
        verify(storeRepository, never()).deleteById(any());
    }

    @Test
    void testGetAllStores() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("johndoe@example.com");
        user.setPhoneNumber("+48123456789");
        user.setTier(1);
        user = userRepository.save(user);
        List<Store> stores = List.of(
                new Store(user,"ShopA", "descA", 11.0, 11.0, "CityA", "AddressA", "imgA.jpg","test"),
                new Store(user,"ShopB", "descB", 22.0, 22.0, "CityB", "AddressB", "imgB.jpg", "test")
        );

        when(storeRepository.findAll()).thenReturn(stores);

        List<Store> result = storeService.getAllStores();

        assertEquals(2, result.size());
        verify(storeRepository, times(1)).findAll();
    }
}
