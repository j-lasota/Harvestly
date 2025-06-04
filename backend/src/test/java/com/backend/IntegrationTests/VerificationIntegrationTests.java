package com.backend.IntegrationTests;

import com.backend.model.Store;
import com.backend.model.User;
import com.backend.model.Verification;
import com.backend.repository.StoreRepository;
import com.backend.repository.UserRepository;
import com.backend.repository.VerificationRepository;
import com.backend.service.StoreService;
import com.backend.service.UserService;
import com.backend.service.VerificationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class VerificationIntegrationTests {

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    private Store testStore;
    private User storeOwner;
    private List<User> verifiers = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // Clean repositories
        verificationRepository.deleteAll();
        storeRepository.deleteAll();
        userRepository.deleteAll();

        // Create store owner
        storeOwner = new User(
                "owner-" + UUID.randomUUID(),
                "Store",
                "Owner",
                "storeowner@example.com",
                "123456789",
                0,
                "owner-img.jpg"
        );
        storeOwner = userRepository.save(storeOwner);

        // Create test store
        testStore = new Store(
                storeOwner,
                "Test Verification Store",
                "A store for testing verifications",
                45.0,
                45.0,
                "Test City",
                "123 Test Street",
                "store-img.jpg",
                "test-verification-store"
        );
        testStore = storeRepository.save(testStore);

        // Create verifier users
        for (int i = 0; i < 6; i++) { // Create 6 users for verification tests
            User verifier = new User(
                    "verifier-" + UUID.randomUUID(),
                    "Verifier",
                    "User" + i,
                    "verifier" + i + "@example.com",
                    "9876" + i + "5432",
                    0,
                    "verifier" + i + "-img.jpg"
            );
            verifiers.add(userRepository.save(verifier));
        }
    }

    @AfterEach
    void tearDown() {
        verificationRepository.deleteAll();
        storeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void testSaveVerification_SingleVerification() {
        // Arrange
        User verifier = verifiers.get(0);
        Verification verification = new Verification(testStore, verifier);

        // Act
        Verification savedVerification = verificationService.saveVerification(verification);

        // Assert
        assertNotNull(savedVerification.getId());
        assertEquals(testStore.getId(), savedVerification.getStore().getId());
        assertEquals(verifier.getId(), savedVerification.getUser().getId());

        // Store should still be unverified with only one verification
        Store updatedStore = storeService.getStoreById(testStore.getId()).orElseThrow();
        assertFalse(updatedStore.isVerified());
        assertEquals(0, updatedStore.getUser().getTier()); // Owner still tier 0
    }

    @Test
    @Transactional
    void testSaveVerification_DuplicateVerification() {
        // Arrange
        User verifier = verifiers.get(0);
        Verification verification = new Verification(testStore, verifier);
        verificationService.saveVerification(verification);

        // Try to save the same verification again
        Verification duplicateVerification = new Verification(testStore, verifier);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> verificationService.saveVerification(duplicateVerification)
        );

        assertEquals("Verification already exists for the given shop and user.", exception.getMessage());
    }

    @Test
    @Transactional
    void testStoreVerification_With5Verifications() {
        // Add 5 verifications from different users
        for (int i = 0; i < 5; i++) {
            Verification verification = new Verification(testStore, verifiers.get(i));
            verificationService.saveVerification(verification);
        }

        // Verify store is now verified
        Store updatedStore = storeService.getStoreById(testStore.getId()).orElseThrow();
        assertTrue(updatedStore.isVerified());

        // Check that owner was upgraded to tier 1
        User updatedOwner = userService.getUserById(storeOwner.getId()).orElseThrow();
        assertEquals(1, updatedOwner.getTier());
    }

    @Test
    @Transactional
    void testGetAllVerifications() {
        // Add 3 verifications
        for (int i = 0; i < 3; i++) {
            Verification verification = new Verification(testStore, verifiers.get(i));
            verificationService.saveVerification(verification);
        }

        // Get all verifications
        List<Verification> allVerifications = verificationService.getAllVerifications();

        // Verify we have exactly 3 verifications
        assertEquals(3, allVerifications.size());
    }

    @Test
    @Transactional
    void testGetVerificationById() {
        // Save a verification
        Verification verification = new Verification(testStore, verifiers.get(0));
        Verification savedVerification = verificationService.saveVerification(verification);

        // Get verification by ID
        Optional<Verification> retrievedVerification = verificationService.getVerificationById(savedVerification.getId());

        // Verify it was retrieved correctly
        assertTrue(retrievedVerification.isPresent());
        assertEquals(savedVerification.getId(), retrievedVerification.get().getId());
    }

    @Test
    @Transactional
    void testDeleteVerification() {
        // Save a verification
        Verification verification = new Verification(testStore, verifiers.get(0));
        Verification savedVerification = verificationService.saveVerification(verification);

        // Delete the verification
        boolean result = verificationService.deleteVerificationById(savedVerification.getId());

        // Verify it was deleted
        assertTrue(result);
        assertEquals(0, verificationRepository.count());
    }

    @Test
    @Transactional
    void testDeleteNonExistentVerification() {
        // Try to delete a non-existent verification
        boolean result = verificationService.deleteVerificationById(999L);

        // Should return false
        assertFalse(result);
    }

    @Test
    @Transactional
    void testStoreRemainsVerifiedAfterDeletingVerifications() {
        // Add 5 verifications from different users
        List<Verification> savedVerifications = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Verification verification = new Verification(testStore, verifiers.get(i));
            savedVerifications.add(verificationService.saveVerification(verification));
        }

        // Verify store is now verified
        Store verifiedStore = storeService.getStoreById(testStore.getId()).orElseThrow();
        assertTrue(verifiedStore.isVerified());

        // Delete one verification
        verificationService.deleteVerificationById(savedVerifications.get(0).getId());

        // Store should still be verified (verification count is now 4, but once verified it stays verified)
        Store stillVerifiedStore = storeService.getStoreById(testStore.getId()).orElseThrow();
        assertTrue(stillVerifiedStore.isVerified());
    }
}