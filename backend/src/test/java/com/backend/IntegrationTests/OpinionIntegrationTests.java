package com.backend.IntegrationTests;

import com.backend.model.Opinion;
import com.backend.model.Store;
import com.backend.model.User;
import com.backend.repository.OpinionRepository;
import com.backend.repository.StoreRepository;
import com.backend.repository.UserRepository;
import com.backend.service.OpinionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class OpinionIntegrationTests {

    @Autowired
    private OpinionService opinionService;

    @Autowired
    private OpinionRepository opinionRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    private Store testStore;
    private User testUser;
    private User secondUser;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        opinionRepository.deleteAll();

        // Create test user if not exists
        Optional<User> existingUser = userRepository.findByEmail("test@example.com");
        if (existingUser.isPresent()) {
            testUser = existingUser.get();
        } else {
            testUser = new User(
                    "test-user-id",
                    "Test",
                    "User",
                    "test@example.com",
                    "123456789",
                    0,
                    "test-img.jpg"
            );
            testUser = userRepository.save(testUser);
        }

        // Create second user if not exists
        Optional<User> existingSecondUser = userRepository.findByEmail("second@example.com");
        if (existingSecondUser.isPresent()) {
            secondUser = existingSecondUser.get();
        } else {
            secondUser = new User(
                    "second-user-id",
                    "Second",
                    "User",
                    "second@example.com",
                    "987654321",
                    0,
                    "second-img.jpg"
            );
            secondUser = userRepository.save(secondUser);
        }

        // Create test store
        testStore = new Store(
                testUser,
                "Test Opinion Store",
                "A store for testing opinions",
                45.0,
                45.0,
                "Test City",
                "123 Test Street",
                "store-img.jpg",
                "test-opinion-store"
        );
        testStore = storeRepository.save(testStore);
    }

    @Test
    @Transactional
    void testCreateAndRetrieveOpinion() {
        // Create a new opinion
        Opinion opinion = new Opinion(testStore, testUser, "Great products and service!", 5);
        Opinion savedOpinion = opinionService.saveOpinion(opinion);

        // Verify it was saved with an ID
        assertNotNull(savedOpinion.getId());
        assertEquals("Great products and service!", savedOpinion.getDescription());
        assertEquals(5, savedOpinion.getStars());

        // Retrieve the opinion by ID
        Optional<Opinion> retrievedOpinion = opinionService.getOpinionById(savedOpinion.getId());
        assertTrue(retrievedOpinion.isPresent());
        assertEquals(savedOpinion.getId(), retrievedOpinion.get().getId());
        assertEquals(savedOpinion.getDescription(), retrievedOpinion.get().getDescription());
    }

    @Test
    @Transactional
    void testGetOpinionsByStoreId() {
        // Create multiple opinions for the same store
        Opinion opinion1 = new Opinion(testStore, testUser, "First opinion", 4);
        Opinion opinion2 = new Opinion(testStore, secondUser, "Second opinion", 5);

        opinionService.saveOpinion(opinion1);
        opinionService.saveOpinion(opinion2);

        // Retrieve opinions by store ID
        List<Opinion> storeOpinions = opinionService.getOpinionsByStoreId(testStore.getId());

        // Verify results
        assertEquals(2, storeOpinions.size());
        assertTrue(storeOpinions.stream().anyMatch(o -> o.getDescription().equals("First opinion")));
        assertTrue(storeOpinions.stream().anyMatch(o -> o.getDescription().equals("Second opinion")));
    }

    @Test
    @Transactional
    void testUpdateOpinion() {
        // Create an opinion
        Opinion opinion = new Opinion(testStore, testUser, "Initial review", 3);
        Opinion savedOpinion = opinionService.saveOpinion(opinion);

        // Update the opinion
        Opinion updatedOpinion = opinionService.updateOpinion(
                savedOpinion.getId(),
                "Updated review - much better service",
                4
        );

        // Verify the update
        assertEquals("Updated review - much better service", updatedOpinion.getDescription());
        assertEquals(4, updatedOpinion.getStars());

        // Verify the opinion in the database was updated
        Optional<Opinion> retrievedOpinion = opinionService.getOpinionById(savedOpinion.getId());
        assertTrue(retrievedOpinion.isPresent());
        assertEquals("Updated review - much better service", retrievedOpinion.get().getDescription());
        assertEquals(4, retrievedOpinion.get().getStars());
    }

    @Test
    @Transactional
    void testDeleteOpinion() {
        // Create an opinion
        Opinion opinion = new Opinion(testStore, testUser, "Opinion to be deleted", 4);
        Opinion savedOpinion = opinionService.saveOpinion(opinion);
        Long opinionId = savedOpinion.getId();

        // Verify it exists
        assertTrue(opinionService.getOpinionById(opinionId).isPresent());

        // Delete the opinion
        boolean deleted = opinionService.deleteOpinionById(opinionId);

        // Verify deletion was successful
        assertTrue(deleted);
        assertFalse(opinionService.getOpinionById(opinionId).isPresent());
    }

    @Test
    @Transactional
    void testCreateDuplicateOpinion() {
        // Create an opinion
        Opinion opinion1 = new Opinion(testStore, testUser, "First opinion", 4);
        opinionService.saveOpinion(opinion1);

        // Try to create another opinion for the same store and user
        Opinion opinion2 = new Opinion(testStore, testUser, "Second opinion attempt", 5);

        // Should throw exception
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> opinionService.saveOpinion(opinion2)
        );

        assertEquals("Opinion already exists for the given shop and user.", exception.getMessage());
    }

    @Test
    @Transactional
    void testCreateOpinionWithInvalidStars() {
        // Create an opinion with stars > 5
        Opinion invalidOpinion = new Opinion(testStore, testUser, "Invalid stars", 6);

        // Save should validate and throw exception
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> opinionService.saveOpinion(invalidOpinion)
        );

        assertEquals("Stars must be between 0 and 5.", exception.getMessage());

        // Also test with negative stars
        invalidOpinion.setStars(-1);

        exception = assertThrows(
                IllegalArgumentException.class,
                () -> opinionService.saveOpinion(invalidOpinion)
        );

        assertEquals("Stars must be between 0 and 5.", exception.getMessage());
    }
}