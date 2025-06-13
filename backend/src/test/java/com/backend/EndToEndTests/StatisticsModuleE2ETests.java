package com.backend.EndToEndTests;

import com.backend.model.DailyClickCount;
import com.backend.model.EventType;
import com.backend.model.Store;
import com.backend.model.User;
import com.backend.repository.DailyClickCountRepository;
import com.backend.repository.OpinionRepository;
import com.backend.repository.StoreRepository;
import com.backend.repository.UserRepository;
import com.backend.service.StoreService;
import com.backend.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class StatisticsModuleE2ETests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DailyClickCountRepository dailyClickCountRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OpinionRepository opinionRepository;

    private Store testStore;
    private User storeOwnerUser;
    private final LocalDate today = LocalDate.now();
    private final LocalDate yesterday = today.minusDays(1);
    private final LocalDate lastWeek = today.minusDays(7);

    @BeforeEach
    public void setUp() {
        dailyClickCountRepository.deleteAll();
        storeRepository.deleteAll();
        userRepository.deleteAll();

        storeOwnerUser = new User(
                UUID.randomUUID().toString(),
                "Store",
                "Owner",
                "storeowner@example.com",
                "123456789",
                0,
                "owner-img.jpg"
        );
        storeOwnerUser = userRepository.save(storeOwnerUser);

        testStore = new Store(
                storeOwnerUser,
                "Test Store",
                "A store for testing",
                45.0,
                45.0,
                "Test City",
                "123 Test Street",
                "store-img.jpg",
                "test-store"
        );
        testStore = storeRepository.save(testStore);
    }

    @AfterEach
    public void tearDown() {
        dailyClickCountRepository.deleteAll();
        storeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testStorePageClickEvent() {
        String url = UriComponentsBuilder.fromPath("/api/stats/event")
                .queryParam("slug", testStore.getSlug())
                .queryParam("type", EventType.STORE_PAGE)
                .toUriString();

        ResponseEntity<Void> response = restTemplate.postForEntity(url, null, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<DailyClickCount> savedClick = dailyClickCountRepository.findById(
                new DailyClickCount.Key(testStore.getSlug(), today));

        assertTrue(savedClick.isPresent());
        assertEquals(1, savedClick.get().getStorePageClicks());
        assertEquals(0, savedClick.get().getMapPinClicks());

        restTemplate.postForEntity(url, null, Void.class);

        Optional<DailyClickCount> updatedClick = dailyClickCountRepository.findById(
                new DailyClickCount.Key(testStore.getSlug(), today));

        assertTrue(updatedClick.isPresent());
        assertEquals(2, updatedClick.get().getStorePageClicks());
        assertEquals(0, updatedClick.get().getMapPinClicks());
    }

    @Test
    public void testMapPinClickEvent() {
        String url = UriComponentsBuilder.fromPath("/api/stats/event")
                .queryParam("slug", testStore.getSlug())
                .queryParam("type", EventType.MAP_PIN)
                .toUriString();

        ResponseEntity<Void> response = restTemplate.postForEntity(url, null, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<DailyClickCount> savedClick = dailyClickCountRepository.findById(
                new DailyClickCount.Key(testStore.getSlug(), today));

        assertTrue(savedClick.isPresent());
        assertEquals(0, savedClick.get().getStorePageClicks());
        assertEquals(1, savedClick.get().getMapPinClicks());

        restTemplate.postForEntity(url, null, Void.class);

        Optional<DailyClickCount> updatedClick = dailyClickCountRepository.findById(
                new DailyClickCount.Key(testStore.getSlug(), today));

        assertTrue(updatedClick.isPresent());
        assertEquals(0, updatedClick.get().getStorePageClicks());
        assertEquals(2, updatedClick.get().getMapPinClicks());
    }

    @Test
    public void testCombinedClickEvents() {
        String storePageUrl = UriComponentsBuilder.fromPath("/api/stats/event")
                .queryParam("slug", testStore.getSlug())
                .queryParam("type", EventType.STORE_PAGE)
                .toUriString();

        String mapPinUrl = UriComponentsBuilder.fromPath("/api/stats/event")
                .queryParam("slug", testStore.getSlug())
                .queryParam("type", EventType.MAP_PIN)
                .toUriString();

        for (int i = 0; i < 3; i++) {
            restTemplate.postForEntity(storePageUrl, null, Void.class);
        }

        for (int i = 0; i < 2; i++) {
            restTemplate.postForEntity(mapPinUrl, null, Void.class);
        }

        Optional<DailyClickCount> combinedClicks = dailyClickCountRepository.findById(
                new DailyClickCount.Key(testStore.getSlug(), today));

        assertTrue(combinedClicks.isPresent());
        assertEquals(3, combinedClicks.get().getStorePageClicks());
        assertEquals(2, combinedClicks.get().getMapPinClicks());
    }

    @Test
    public void testHistoricalClickStatistics() {

        DailyClickCount yesterdayClicks = new DailyClickCount(testStore.getSlug(), yesterday);
        yesterdayClicks.setStorePageClicks(5);
        yesterdayClicks.setMapPinClicks(3);
        dailyClickCountRepository.save(yesterdayClicks);

        DailyClickCount lastWeekClicks = new DailyClickCount(testStore.getSlug(), lastWeek);
        lastWeekClicks.setStorePageClicks(10);
        lastWeekClicks.setMapPinClicks(7);
        dailyClickCountRepository.save(lastWeekClicks);

        String storePageUrl = UriComponentsBuilder.fromPath("/api/stats/event")
                .queryParam("slug", testStore.getSlug())
                .queryParam("type", EventType.STORE_PAGE)
                .toUriString();

        String mapPinUrl = UriComponentsBuilder.fromPath("/api/stats/event")
                .queryParam("slug", testStore.getSlug())
                .queryParam("type", EventType.MAP_PIN)
                .toUriString();

        restTemplate.postForEntity(storePageUrl, null, Void.class);
        restTemplate.postForEntity(storePageUrl, null, Void.class);
        restTemplate.postForEntity(mapPinUrl, null, Void.class);

        long totalStorePageClicks = 0;
        long totalMapPinClicks = 0;

        Optional<DailyClickCount> todayClicks = dailyClickCountRepository.findById(
                new DailyClickCount.Key(testStore.getSlug(), today));
        if (todayClicks.isPresent()) {
            totalStorePageClicks += todayClicks.get().getStorePageClicks();
            totalMapPinClicks += todayClicks.get().getMapPinClicks();
        }

        totalStorePageClicks += yesterdayClicks.getStorePageClicks();
        totalMapPinClicks += yesterdayClicks.getMapPinClicks();

        totalStorePageClicks += lastWeekClicks.getStorePageClicks();
        totalMapPinClicks += lastWeekClicks.getMapPinClicks();

        assertEquals(17L, totalStorePageClicks);
        assertEquals(11L, totalMapPinClicks);
    }

    @Test
    public void testMultipleStoreStatistics() {
        Store secondStore = new Store(
                storeOwnerUser,
                "Second Store",
                "Another store for testing",
                46.0,
                46.0,
                "Second City",
                "456 Test Avenue",
                "second-store-img.jpg",
                "second-store"
        );
        secondStore = storeRepository.save(secondStore);

        String firstStorePageUrl = UriComponentsBuilder.fromPath("/api/stats/event")
                .queryParam("slug", testStore.getSlug())
                .queryParam("type", EventType.STORE_PAGE)
                .toUriString();

        String secondStorePageUrl = UriComponentsBuilder.fromPath("/api/stats/event")
                .queryParam("slug", secondStore.getSlug())
                .queryParam("type", EventType.STORE_PAGE)
                .toUriString();

        String firstMapPinUrl = UriComponentsBuilder.fromPath("/api/stats/event")
                .queryParam("slug", testStore.getSlug())
                .queryParam("type", EventType.MAP_PIN)
                .toUriString();

        String secondMapPinUrl = UriComponentsBuilder.fromPath("/api/stats/event")
                .queryParam("slug", secondStore.getSlug())
                .queryParam("type", EventType.MAP_PIN)
                .toUriString();

        restTemplate.postForEntity(firstStorePageUrl, null, Void.class);
        restTemplate.postForEntity(firstStorePageUrl, null, Void.class);
        restTemplate.postForEntity(firstMapPinUrl, null, Void.class);

        restTemplate.postForEntity(secondStorePageUrl, null, Void.class);
        restTemplate.postForEntity(secondMapPinUrl, null, Void.class);
        restTemplate.postForEntity(secondMapPinUrl, null, Void.class);

        Optional<DailyClickCount> firstStoreClicks = dailyClickCountRepository.findById(
                new DailyClickCount.Key(testStore.getSlug(), today));
        Optional<DailyClickCount> secondStoreClicks = dailyClickCountRepository.findById(
                new DailyClickCount.Key(secondStore.getSlug(), today));

        assertTrue(firstStoreClicks.isPresent());
        assertEquals(2, firstStoreClicks.get().getStorePageClicks());
        assertEquals(1, firstStoreClicks.get().getMapPinClicks());

        assertTrue(secondStoreClicks.isPresent());
        assertEquals(1, secondStoreClicks.get().getStorePageClicks());
        assertEquals(2, secondStoreClicks.get().getMapPinClicks());
    }

    @Test
    public void testAverageRating() {
        String avgRatingUrl = UriComponentsBuilder.fromPath("/api/stats/average-rating")
                .queryParam("slug", testStore.getSlug())
                .toUriString();

        ResponseEntity<Double> response = restTemplate.getForEntity(avgRatingUrl, Double.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Double expectedRating = opinionRepository.findAverageStarsByStoreSlug(testStore.getSlug());
        Double actualRating = response.getBody();

        if (expectedRating == null) {
            assertTrue(actualRating == null || actualRating == 0.0);
        } else {
            assertEquals(expectedRating, actualRating, 0.001);
        }
    }
}