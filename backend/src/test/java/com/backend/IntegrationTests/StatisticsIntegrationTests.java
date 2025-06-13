package com.backend.IntegrationTests;

import com.backend.model.*;
import com.backend.repository.DailyClickCountRepository;
import com.backend.repository.OpinionRepository;
import com.backend.repository.StoreRepository;
import com.backend.repository.UserRepository;
import com.backend.service.StatisticsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class StatisticsIntegrationTests {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private DailyClickCountRepository clickRepository;

    @Autowired
    private OpinionRepository opinionRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    private Store testStore;
    private User testUser;
    private final String TEST_SLUG = "test-store";

    @BeforeEach
    void setUp() {
        opinionRepository.deleteAll();
        clickRepository.deleteAll();

        testUser = new User(
                "test-user-id",
                "Test",
                "User",
                "testuser@example.com",
                "987654321",
                0,
                "user-img.jpg"
        );
        testUser = userRepository.save(testUser);

        testStore = new Store(
                testUser,
                "Test Store",
                "A store for testing",
                45.0,
                45.0,
                "Test City",
                "123 Test Street",
                "store-img.jpg",
                TEST_SLUG
        );
        testStore = storeRepository.save(testStore);
    }

    @AfterEach
    void tearDown() {
        opinionRepository.deleteAll();
        clickRepository.deleteAll();
    }

    @Test
    @Transactional
    void testRecordAndRetrieveEvents() {
        statisticsService.recordEvent(EventType.STORE_PAGE, TEST_SLUG);
        statisticsService.recordEvent(EventType.STORE_PAGE, TEST_SLUG);
        statisticsService.recordEvent(EventType.MAP_PIN, TEST_SLUG);

        assertEquals(2, clickRepository.totalStorePageClicks(TEST_SLUG));
        assertEquals(1, clickRepository.totalMapPinClicks(TEST_SLUG));

        assertEquals(0.5, statisticsService.getClickRatio(TEST_SLUG), 0.001);
    }

    @Test
    @Transactional
    void testClickRatioWithTimeframe() {
        LocalDate today = LocalDate.now();


        DailyClickCount todayCount = new DailyClickCount(TEST_SLUG, today);
        todayCount.setStorePageClicks(10);
        todayCount.setMapPinClicks(5);
        clickRepository.save(todayCount);

        DailyClickCount yesterdayCount = new DailyClickCount(TEST_SLUG, today.minusDays(1));
        yesterdayCount.setStorePageClicks(20);
        yesterdayCount.setMapPinClicks(10);
        clickRepository.save(yesterdayCount);

        DailyClickCount oldCount = new DailyClickCount(TEST_SLUG, today.minusDays(10));
        oldCount.setStorePageClicks(30);
        oldCount.setMapPinClicks(15);
        clickRepository.save(oldCount);

        assertEquals(0.5, statisticsService.getClickRatio(TEST_SLUG), 0.001);
        assertEquals(0.5, statisticsService.getClickRatio(TEST_SLUG, 2), 0.001);
        assertEquals(0.5, statisticsService.getClickRatio(TEST_SLUG, 15), 0.001);
    }

    @Test
    @Transactional
    void testZeroClicks() {
        assertEquals(0.0, statisticsService.getClickRatio(TEST_SLUG));

        statisticsService.recordEvent(EventType.MAP_PIN, TEST_SLUG);
        statisticsService.recordEvent(EventType.MAP_PIN, TEST_SLUG);

        assertEquals(0.0, statisticsService.getClickRatio(TEST_SLUG));

        statisticsService.recordEvent(EventType.STORE_PAGE, TEST_SLUG);

        assertEquals(2.0, statisticsService.getClickRatio(TEST_SLUG), 0.001);
    }

    @Test
    @Transactional
    void testNonExistentStoreEvents() {
        String nonExistentSlug = "non-existent-store";

        statisticsService.recordEvent(EventType.STORE_PAGE, nonExistentSlug);
        statisticsService.recordEvent(EventType.MAP_PIN, nonExistentSlug);

        assertEquals(1, clickRepository.totalStorePageClicks(nonExistentSlug));
        assertEquals(1, clickRepository.totalMapPinClicks(nonExistentSlug));
        assertEquals(1.0, statisticsService.getClickRatio(nonExistentSlug), 0.001);
    }

    @Test
    @Transactional
    void testAverageRating() {
        assertEquals(0.0, statisticsService.getAverageRating(TEST_SLUG));

        Opinion opinion1 = new Opinion(testStore, testUser, "Great store!", 5);
        opinionRepository.save(opinion1);

        assertEquals(5.0, statisticsService.getAverageRating(TEST_SLUG), 0.001);

        User secondUser = new User(
                "second-user-id",
                "Second",
                "User",
                "second@example.com",
                "123123123",
                0,
                "user2-img.jpg"
        );
        secondUser = userRepository.save(secondUser);

        Opinion opinion2 = new Opinion(testStore, secondUser, "Decent store", 3);
        opinionRepository.save(opinion2);

        assertEquals(4.0, statisticsService.getAverageRating(TEST_SLUG), 0.001);
    }

    @Test
    @Transactional
    void testAverageRatingForNonExistentStore() {
        assertEquals(0.0, statisticsService.getAverageRating("non-existent-store"));
    }

    @Test
    @Transactional
    void testMultipleStoreStatistics() {
        Store secondStore = new Store(
                testUser,
                "Second Store",
                "Another test store",
                46.0,
                46.0,
                "Another City",
                "456 Test Street",
                "store2-img.jpg",
                "second-store"
        );
        secondStore = storeRepository.save(secondStore);

        statisticsService.recordEvent(EventType.STORE_PAGE, TEST_SLUG);
        statisticsService.recordEvent(EventType.STORE_PAGE, TEST_SLUG);
        statisticsService.recordEvent(EventType.MAP_PIN, TEST_SLUG);

        statisticsService.recordEvent(EventType.STORE_PAGE, "second-store");
        statisticsService.recordEvent(EventType.MAP_PIN, "second-store");
        statisticsService.recordEvent(EventType.MAP_PIN, "second-store");

        assertEquals(0.5, statisticsService.getClickRatio(TEST_SLUG), 0.001);
        assertEquals(2.0, statisticsService.getClickRatio("second-store"), 0.001);

        Opinion opinion1 = new Opinion(testStore, testUser, "First store review", 4);
        opinionRepository.save(opinion1);

        Opinion opinion2 = new Opinion(secondStore, testUser, "Second store review", 2);
        opinionRepository.save(opinion2);

        assertEquals(4.0, statisticsService.getAverageRating(TEST_SLUG), 0.001);
        assertEquals(2.0, statisticsService.getAverageRating("second-store"), 0.001);
    }
}