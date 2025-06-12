package com.backend.UnitTests;

import com.backend.model.Store;
import com.backend.model.User;
import com.backend.repository.UserRepository;
import com.backend.service.StoreService;
import com.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreService storeService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Store testStore;

    @BeforeEach
    void setUp() {
        testUser = new User("user123", "John", "Doe", "john@example.com", "123456789", 0, "user.jpg");
        testStore = new Store();
        testStore.setId(1L);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        User secondUser = new User("user456", "Jane", "Smith", "jane@example.com", "987654321", 0, "jane.jpg");
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, secondUser));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserById("user123");

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository, times(1)).findById("user123");
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldReturnEmpty() {
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById("nonexistent");

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById("nonexistent");
    }

    @Test
    void findByEmail_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findByEmail("john@example.com");

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository, times(1)).findByEmail("john@example.com");
    }

    @Test
    void findByEmail_WhenUserDoesNotExist_ShouldReturnEmpty() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmail("nonexistent@example.com");

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    void saveUser_WhenEmailAndPhoneAreUnique_ShouldSaveAndReturnUser() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.existsByPhoneNumber("123456789")).thenReturn(false);
        when(userRepository.save(testUser)).thenReturn(testUser);

        User savedUser = userService.saveUser(testUser);

        assertEquals(testUser, savedUser);
        verify(userRepository, times(1)).existsByEmail("john@example.com");
        verify(userRepository, times(1)).existsByPhoneNumber("123456789");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void saveUser_WhenEmailExists_ShouldThrowException() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.saveUser(testUser)
        );

        assertEquals("User with the same email already exists.", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail("john@example.com");
        verify(userRepository, never()).existsByPhoneNumber(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void saveUser_WhenPhoneNumberExists_ShouldThrowException() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.existsByPhoneNumber("123456789")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.saveUser(testUser)
        );

        assertEquals("User with the same phone number already exists.", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail("john@example.com");
        verify(userRepository, times(1)).existsByPhoneNumber("123456789");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WhenUserExists_ShouldUpdateAndReturnUser() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        User updatedUser = userService.updateUser(
                "user123",
                "Johnny",
                "Smith",
                "johnny@example.com",
                "987654321",
                1,
                "johnny.jpg",
                null
        );

        assertEquals("Johnny", updatedUser.getFirstName());
        assertEquals("Smith", updatedUser.getLastName());
        assertEquals("johnny@example.com", updatedUser.getEmail());
        assertEquals("987654321", updatedUser.getPhoneNumber());
        assertEquals(1, updatedUser.getTier());
        assertEquals("johnny.jpg", updatedUser.getImg());
        verify(userRepository, times(1)).findById("user123");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void updateUser_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(
                        "nonexistent",
                        "Test",
                        "User",
                        "test@example.com",
                        "123123123",
                        0,
                        "test.jpg",
                        null
                )
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById("nonexistent");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WithNullValues_ShouldKeepExistingValues() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);
        String originalFirstName = testUser.getFirstName();
        String originalLastName = testUser.getLastName();
        String originalEmail = testUser.getEmail();
        String originalPhone = testUser.getPhoneNumber();
        int originalTier = testUser.getTier();
        String originalImg = testUser.getImg();

        User updatedUser = userService.updateUser(
                "user123",
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertEquals(originalFirstName, updatedUser.getFirstName());
        assertEquals(originalLastName, updatedUser.getLastName());
        assertEquals(originalEmail, updatedUser.getEmail());
        assertEquals(originalPhone, updatedUser.getPhoneNumber());
        assertEquals(originalTier, updatedUser.getTier());
        assertEquals(originalImg, updatedUser.getImg());
        verify(userRepository, times(1)).findById("user123");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void deleteUserById_WhenUserExists_ShouldReturnTrue() {
        when(userRepository.existsById("user123")).thenReturn(true);
        doNothing().when(userRepository).deleteById("user123");

        boolean result = userService.deleteUserById("user123");

        assertTrue(result);
        verify(userRepository, times(1)).existsById("user123");
        verify(userRepository, times(1)).deleteById("user123");
    }

    @Test
    void deleteUserById_WhenUserDoesNotExist_ShouldReturnFalse() {
        when(userRepository.existsById("nonexistent")).thenReturn(false);

        boolean result = userService.deleteUserById("nonexistent");

        assertFalse(result);
        verify(userRepository, times(1)).existsById("nonexistent");
        verify(userRepository, never()).deleteById(anyString());
    }

    @Test
    void addFavoriteShop_WhenUserAndStoreExist_ShouldAddStoreToFavorites() {
        final String userId = "user123";
        final Long storeId = 1L;

        testUser.setFavoriteStores(new HashSet<>());
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(storeService.getStoreById(storeId)).thenReturn(Optional.of(testStore));
        when(userRepository.save(testUser)).thenReturn(testUser);

        User result = userService.addFavoriteShop(userId, storeId);

        assertEquals(1, result.getFavoriteStores().size());
        assertTrue(result.getFavoriteStores().contains(testStore));
        verify(userRepository, times(1)).findById(userId);
        verify(storeService, times(1)).getStoreById(storeId);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void addFavoriteShop_WhenUserDoesNotExist_ShouldThrowException() {
        final String userId = "nonexistent";
        final Long storeId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.addFavoriteShop(userId, storeId)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(storeService, times(1)).getStoreById(anyLong());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void addFavoriteShop_WhenStoreDoesNotExist_ShouldThrowException() {
        final String userId = "user123";
        final Long storeId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(storeService.getStoreById(storeId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.addFavoriteShop(userId, storeId)
        );

        assertEquals("Shop not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(storeService, times(1)).getStoreById(storeId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void removeFavoriteShop_WhenUserAndStoreExist_ShouldRemoveStoreFromFavorites() {
        final String userId = "user123";
        final Long storeId = 1L;

        HashSet<Store> favorites = new HashSet<>();
        favorites.add(testStore);
        testUser.setFavoriteStores(favorites);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(storeService.getStoreById(storeId)).thenReturn(Optional.of(testStore));
        when(userRepository.save(testUser)).thenReturn(testUser);

        User result = userService.removeFavoriteShop(userId, storeId);

        assertEquals(0, result.getFavoriteStores().size());
        verify(userRepository, times(1)).findById(userId);
        verify(storeService, times(1)).getStoreById(storeId);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void removeFavoriteShop_WhenUserDoesNotExist_ShouldThrowException() {
        final String userId = "nonexistent";
        final Long storeId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.removeFavoriteShop(userId, storeId)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(storeService, times(1)).getStoreById(anyLong());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void removeFavoriteShop_WhenStoreDoesNotExist_ShouldThrowException() {
        final String userId = "user123";
        final Long storeId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(storeService.getStoreById(storeId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.removeFavoriteShop(userId, storeId)
        );

        assertEquals("Shop not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(storeService, times(1)).getStoreById(storeId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserByEmail_ShouldCallFindByEmail() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserByEmail("john@example.com");

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository, times(1)).findByEmail("john@example.com");
    }
}