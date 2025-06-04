package com.backend.IntegrationTests;

import com.backend.model.DailyClickCount;
import com.backend.model.EventType;
import com.backend.model.Opinion;
import com.backend.model.Store;
import com.backend.model.User;
import com.backend.repository.DailyClickCountRepository;
import com.backend.repository.OpinionRepository;
import com.backend.repository.StoreRepository;
import com.backend.repository.UserRepository;
import com.backend.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class StatisticsIntegrationTests {

    @Autowired
    private DailyClickCountRepository clickRepo;
    @Autowired
    private OpinionRepository opinionRepo;
    @Autowired
    private StoreRepository storeRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private StatisticsService service;

    private static final String STORE_SLUG = "test-store";

    @BeforeEach
    void setUp() {
        clickRepo.deleteAll();
        opinionRepo.deleteAll();
        storeRepo.deleteAll();
        userRepo.deleteAll();
    }

    @Test
    void recordPageClick_createsNewRecordWhenNoneExist() {
        LocalDate today = LocalDate.now();

        // Act
        service.recordEvent(EventType.STORE_PAGE, STORE_SLUG);

        // Assert
        DailyClickCount.Key key = new DailyClickCount.Key(STORE_SLUG, today);
        DailyClickCount dc = clickRepo.findById(key)
                .orElseThrow(() -> new AssertionError("Expected DailyClickCount"));
        assertEquals(1, dc.getStorePageClicks());
        assertEquals(0, dc.getMapPinClicks());
    }

    @Test
    void recordPageClick_incrementsExisting() {
        LocalDate today = LocalDate.now();
        DailyClickCount existing = new DailyClickCount(STORE_SLUG, today);
        existing.setStorePageClicks(2);
        existing.setMapPinClicks(1);
        clickRepo.save(existing);

        // Act
        service.recordEvent(EventType.STORE_PAGE, STORE_SLUG);

        // Assert
        DailyClickCount updated = clickRepo.findById(existing.getId()).orElseThrow();
        assertEquals(3, updated.getStorePageClicks());
        assertEquals(1, updated.getMapPinClicks());
    }

    @Test
    void recordPinClick_createsNewRecordWhenNoneExist() {
        LocalDate today = LocalDate.now();

        // Act
        service.recordEvent(EventType.MAP_PIN, STORE_SLUG);

        // Assert
        DailyClickCount.Key key = new DailyClickCount.Key(STORE_SLUG, today);
        DailyClickCount dc = clickRepo.findById(key).orElseThrow();
        assertEquals(0, dc.getStorePageClicks());
        assertEquals(1, dc.getMapPinClicks());
    }

    @Test
    void recordPinClick_incrementsExisting() {
        LocalDate today = LocalDate.now();
        DailyClickCount existing = new DailyClickCount(STORE_SLUG, today);
        existing.setStorePageClicks(5);
        existing.setMapPinClicks(2);
        clickRepo.save(existing);

        // Act
        service.recordEvent(EventType.MAP_PIN, STORE_SLUG);

        // Assert
        DailyClickCount updated = clickRepo.findById(existing.getId()).orElseThrow();
        assertEquals(5, updated.getStorePageClicks());
        assertEquals(3, updated.getMapPinClicks());
    }

    @Test
    void getAverageRating_noRatingsReturnsZero() {
        double avg = service.getAverageRating(STORE_SLUG);
        assertEquals(0.0, avg, 1e-6);
    }

    @Test
    void getAverageRating_realData_returnsCorrectAverage() {
        // Persist store



        // Persist two users
        User u1 = new User("u1", "Alice", "A", "a@x.com", null, 0, null);
        User u2 = new User("u2", "Bob",   "B", "b@x.com", null, 0, null);
        userRepo.save(u1);
        userRepo.save(u2);

        Store store = new Store(u1, "store", "description", 12.34, 56.78, "city", "address", "http://example.com/image.jpg", "slug");
        store.setSlug(STORE_SLUG);
        storeRepo.save(store);
        // Persist opinions: 2 stars and 4 stars => average 3.0
        opinionRepo.save(new Opinion(store, u1, "meh", 2));
        opinionRepo.save(new Opinion(store, u2, "good", 4));

        double avg = service.getAverageRating(STORE_SLUG);
        assertEquals(3.0, avg, 1e-6);
    }
}