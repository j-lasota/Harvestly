package com.backend.IntegrationTests;

import com.backend.model.Store;
import com.backend.model.StoreReport;
import com.backend.model.User;
import com.backend.repository.StoreReportRepository;
import com.backend.repository.StoreRepository;
import com.backend.repository.UserRepository;
import com.backend.service.Auth0UserService;
import com.backend.service.StoreReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class StoreReportIntegrationTests {

    @Autowired
    private StoreReportRepository storeReportRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StoreReportService storeReportService;

    private Store store;
    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        storeReportRepository.deleteAll();
        storeRepository.deleteAll();

        store = new Store();

        store.setName("Test Store");
        store.setReported(false);
        store.setAddress("Test Address");
        store.setCity("Test City");
        store.setUser(user);
        user = new User();
        user.setId("user1");
        store.setUser(user);

        user = userRepository.save(user);
        store = storeRepository.save(store);


    }

    @Test
    void testSaveStoreReport_Success() {
        StoreReport report = new StoreReport(store, user);
        StoreReport saved = storeReportService.saveStoreReport(report);

        assertNotNull(saved.getId());
        assertEquals(store.getId(), saved.getStore().getId());
        assertEquals(user.getId(), saved.getUser().getId());
        assertFalse(saved.getStore().isReported());
    }

    @Test
    void testSaveStoreReport_DuplicateThrows() {
        StoreReport report = new StoreReport(store, user);
        storeReportService.saveStoreReport(report);

        StoreReport duplicate = new StoreReport(store, user);
        assertThrows(IllegalArgumentException.class, () -> storeReportService.saveStoreReport(duplicate));
    }

    @Test
    void testSaveStoreReport_ThresholdSetsReported() {
        // Add 4 reports from different users
        for (int i = 0; i < 4; i++) {
            User u = new User();
            u.setId("user" + (i + 2));
            userRepository.save(u);
            storeReportService.saveStoreReport(new StoreReport(store, u));
        }
        // 5th report triggers reported=true
        StoreReport report = new StoreReport(store, user);
        storeReportService.saveStoreReport(report);

        Store updatedStore = storeRepository.findById(store.getId()).orElseThrow();
        assertTrue(updatedStore.isReported());
    }

    @Test
    void testDeleteStoreReportById() {
        StoreReport report = new StoreReport(store, user);
        StoreReport saved = storeReportService.saveStoreReport(report);

        boolean deleted = storeReportService.deleteStoreReportById(saved.getId());
        assertTrue(deleted);

        Optional<StoreReport> found = storeReportRepository.findById(saved.getId());
        assertFalse(found.isPresent());
    }
}