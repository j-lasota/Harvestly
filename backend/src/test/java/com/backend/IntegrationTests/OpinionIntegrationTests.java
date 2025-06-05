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
        opinionRepository.deleteAll();

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
        Opinion opinion = new Opinion(testStore, testUser, "Great products and service!", 5);
        Opinion savedOpinion = opinionService.saveOpinion(opinion);

        assertNotNull(savedOpinion.getId());
        assertEquals("Great products and service!", savedOpinion.getDescription());
        assertEquals(5, savedOpinion.getStars());

        Optional<Opinion> retrievedOpinion = opinionService.getOpinionById(savedOpinion.getId());
        assertTrue(retrievedOpinion.isPresent());
        assertEquals(savedOpinion.getId(), retrievedOpinion.get().getId());
        assertEquals(savedOpinion.getDescription(), retrievedOpinion.get().getDescription());
    }

    @Test
    @Transactional
    void testGetOpinionsByStoreId() {
        Opinion opinion1 = new Opinion(testStore, testUser, "First opinion", 4);
        Opinion opinion2 = new Opinion(testStore, secondUser, "Second opinion", 5);

        opinionService.saveOpinion(opinion1);
        opinionService.saveOpinion(opinion2);

        List<Opinion> storeOpinions = opinionService.getOpinionsByStoreId(testStore.getId());

        assertEquals(2, storeOpinions.size());
        assertTrue(storeOpinions.stream().anyMatch(o -> o.getDescription().equals("First opinion")));
        assertTrue(storeOpinions.stream().anyMatch(o -> o.getDescription().equals("Second opinion")));
    }

    @Test
    @Transactional
    void testUpdateOpinion() {
        Opinion opinion = new Opinion(testStore, testUser, "Initial review", 3);
        Opinion savedOpinion = opinionService.saveOpinion(opinion);

        Opinion updatedOpinion = opinionService.updateOpinion(
                savedOpinion.getId(),
                "Updated review - much better service",
                4
        );

        assertEquals("Updated review - much better service", updatedOpinion.getDescription());
        assertEquals(4, updatedOpinion.getStars());

        Optional<Opinion> retrievedOpinion = opinionService.getOpinionById(savedOpinion.getId());
        assertTrue(retrievedOpinion.isPresent());
        assertEquals("Updated review - much better service", retrievedOpinion.get().getDescription());
        assertEquals(4, retrievedOpinion.get().getStars());
    }

    @Test
    @Transactional
    void testDeleteOpinion() {
        Opinion opinion = new Opinion(testStore, testUser, "Opinion to be deleted", 4);
        Opinion savedOpinion = opinionService.saveOpinion(opinion);
        Long opinionId = savedOpinion.getId();

        assertTrue(opinionService.getOpinionById(opinionId).isPresent());

        boolean deleted = opinionService.deleteOpinionById(opinionId);

        assertTrue(deleted);
        assertFalse(opinionService.getOpinionById(opinionId).isPresent());
    }

    @Test
    @Transactional
    void testCreateDuplicateOpinion() {
        Opinion opinion1 = new Opinion(testStore, testUser, "First opinion", 4);
        opinionService.saveOpinion(opinion1);

        Opinion opinion2 = new Opinion(testStore, testUser, "Second opinion attempt", 5);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> opinionService.saveOpinion(opinion2)
        );

        assertEquals("Opinion already exists for the given shop and user.", exception.getMessage());
    }

    @Test
    @Transactional
    void testCreateOpinionWithInvalidStars() {
        Opinion invalidOpinion = new Opinion(testStore, testUser, "Invalid stars", 6);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> opinionService.saveOpinion(invalidOpinion)
        );

        assertEquals("Stars must be between 0 and 5.", exception.getMessage());

        invalidOpinion.setStars(-1);

        exception = assertThrows(
                IllegalArgumentException.class,
                () -> opinionService.saveOpinion(invalidOpinion)
        );

        assertEquals("Stars must be between 0 and 5.", exception.getMessage());
    }
}