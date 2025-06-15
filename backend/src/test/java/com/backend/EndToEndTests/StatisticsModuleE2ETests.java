package com.backend.EndToEndTests;

import com.backend.model.DailyClickCount;
import com.backend.model.EventType;
import com.backend.model.Store;
import com.backend.model.User;
import com.backend.repository.DailyClickCountRepository;
import com.backend.repository.StoreRepository;
import com.backend.repository.UserRepository;
import com.backend.repository.OpinionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StatisticsModuleE2ETests {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

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
    void setUp() {
        // budujemy MockMvc z włączonym Spring Security
        this.mockMvc = org.springframework.test.web.servlet.setup.MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();

        // czyszczenie bazy
        dailyClickCountRepository.deleteAll();
        opinionRepository.deleteAll();
        storeRepository.deleteAll();
        userRepository.deleteAll();

        // tworzymy użytkownika–właściciela i sklep
        storeOwnerUser = new User(
                UUID.randomUUID().toString(),
                "OwnerFirst",
                "OwnerLast",
                "owner@example.com",
                "123456789",
                0,
                "img.jpg"
        );
        storeOwnerUser = userRepository.save(storeOwnerUser);

        testStore = new Store(
                storeOwnerUser,
                "Stat Store",
                "Desc",
                10.0,
                20.0,
                "City",
                "Addr",
                "img.jpg",
                "stat-store"
        );
        testStore = storeRepository.save(testStore);
    }

    @AfterEach
    void tearDown() {
        dailyClickCountRepository.deleteAll();
        opinionRepository.deleteAll();
        storeRepository.deleteAll();
        userRepository.deleteAll();
    }

    // helper aby dodać JWT do żądania
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtForOwner() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt.subject(storeOwnerUser.getId()));
    }

    @Test
    void testStorePageClickEvent() throws Exception {
        mockMvc.perform(post("/api/stats/event")
                        .param("slug", testStore.getSlug())
                        .param("type", EventType.STORE_PAGE.name())
                        .with(jwtForOwner())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        var key = new DailyClickCount.Key(testStore.getSlug(), today);
        Optional<DailyClickCount> saved = dailyClickCountRepository.findById(key);
        assertThat(saved).isPresent();
        assertThat(saved.get().getStorePageClicks()).isEqualTo(1);
        assertThat(saved.get().getMapPinClicks()).isZero();
    }

    @Test
    void testMapPinClickEvent() throws Exception {
        mockMvc.perform(post("/api/stats/event")
                        .param("slug", testStore.getSlug())
                        .param("type", EventType.MAP_PIN.name())
                        .with(jwtForOwner())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        var key = new DailyClickCount.Key(testStore.getSlug(), today);
        Optional<DailyClickCount> saved = dailyClickCountRepository.findById(key);
        assertThat(saved).isPresent();
        assertThat(saved.get().getMapPinClicks()).isEqualTo(1);
        assertThat(saved.get().getStorePageClicks()).isZero();
    }

    @Test
    void testCombinedClickEvents() throws Exception {
        // 3 × STORE_PAGE
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/api/stats/event")
                            .param("slug", testStore.getSlug())
                            .param("type", EventType.STORE_PAGE.name())
                            .with(jwtForOwner())
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk());
        }
        // 2 × MAP_PIN
        for (int i = 0; i < 2; i++) {
            mockMvc.perform(post("/api/stats/event")
                            .param("slug", testStore.getSlug())
                            .param("type", EventType.MAP_PIN.name())
                            .with(jwtForOwner())
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk());
        }

        var key = new DailyClickCount.Key(testStore.getSlug(), today);
        var saved = dailyClickCountRepository.findById(key).orElseThrow();
        assertThat(saved.getStorePageClicks()).isEqualTo(3);
        assertThat(saved.getMapPinClicks()).isEqualTo(2);
    }

    @Test
    void testHistoricalClickStatistics() throws Exception {
        // przygotowujemy starsze rekordy
        var y = new DailyClickCount(testStore.getSlug(), yesterday);
        y.setStorePageClicks(5); y.setMapPinClicks(3);
        dailyClickCountRepository.save(y);

        var lw = new DailyClickCount(testStore.getSlug(), lastWeek);
        lw.setStorePageClicks(10); lw.setMapPinClicks(7);
        dailyClickCountRepository.save(lw);

        // dodajemy dzisiejsze eventy
        mockMvc.perform(post("/api/stats/event")
                .param("slug", testStore.getSlug())
                .param("type", EventType.STORE_PAGE.name())
                .with(jwtForOwner())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        mockMvc.perform(post("/api/stats/event")
                .param("slug", testStore.getSlug())
                .param("type", EventType.MAP_PIN.name())
                .with(jwtForOwner())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        // sumujemy lokalnie
        AtomicLong totalPage = new AtomicLong();
        AtomicLong totalPin = new AtomicLong();
        dailyClickCountRepository.findById(new DailyClickCount.Key(testStore.getSlug(), today))
                .ifPresent(c -> {
                    totalPage.addAndGet(c.getStorePageClicks());
                    totalPin.addAndGet(c.getMapPinClicks());
                });
        totalPage.addAndGet(y.getStorePageClicks());
        totalPin.addAndGet(y.getMapPinClicks());
        totalPage.addAndGet(lw.getStorePageClicks());
        totalPin.addAndGet(lw.getMapPinClicks());

        assertThat(totalPage.get()).isEqualTo(5 + 10 + 1);
        assertThat(totalPin.get()).isEqualTo(3 + 7 + 1);
    }

    @Test
    void testMultipleStoreStatistics() throws Exception {
        // drugi sklep
        Store second = new Store(
                storeOwnerUser, "Another", "Desc", 11.0, 22.0,
                "SecondCity", "Addr2", "img2.jpg", "second-store"
        );
        second = storeRepository.save(second);

        // eventy dla pierwszego i drugiego
        mockMvc.perform(post("/api/stats/event")
                .param("slug", testStore.getSlug())
                .param("type", EventType.STORE_PAGE.name())
                .with(jwtForOwner())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        mockMvc.perform(post("/api/stats/event")
                .param("slug", second.getSlug())
                .param("type", EventType.MAP_PIN.name())
                .with(jwtForOwner())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        assertThat(dailyClickCountRepository.findById(
                new DailyClickCount.Key(testStore.getSlug(), today))
        ).map(DailyClickCount::getStorePageClicks).contains(1L);

        assertThat(dailyClickCountRepository.findById(
                new DailyClickCount.Key(second.getSlug(), today))
        ).map(DailyClickCount::getMapPinClicks).contains(1L);
    }

    @Test
    void testAverageRating() throws Exception {
        // nic nie ma → zwróci 0 lub null
        var mvcResult = mockMvc.perform(get("/api/stats/average-rating")
                        .param("slug", testStore.getSlug())
                        .with(jwtForOwner())
                )
                .andExpect(status().isOk())
                .andReturn();

        String body = mvcResult.getResponse().getContentAsString();
        Double resp = body.isEmpty() ? null : Double.valueOf(body);

        Double expected = opinionRepository.findAverageStarsByStoreSlug(testStore.getSlug());
        if (expected == null) {
            assertThat(resp).isEqualTo(0.0);
        } else {
            assertThat(resp).isEqualTo(expected);
        }
    }
}
