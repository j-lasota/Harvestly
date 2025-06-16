package com.backend.IntegrationTests;

import com.backend.model.Opinion;
import com.backend.model.OpinionReport;
import com.backend.model.Store;
import com.backend.model.User;
import com.backend.repository.OpinionReportRepository;
import com.backend.repository.OpinionRepository;
import com.backend.repository.StoreRepository;
import com.backend.repository.UserRepository;
import com.backend.service.OpinionReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class OpinionReportIntegrationTests {

    @Autowired
    private OpinionReportRepository opinionReportRepository;
    @Autowired
    private OpinionRepository opinionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OpinionReportService opinionReportService;

    @Autowired
    private StoreRepository storeRepository;

    private Opinion opinion;
    private User user;
    private Store store;
    @BeforeEach
    void setUp() {
        opinionReportRepository.deleteAll();
        opinionRepository.deleteAll();
        userRepository.deleteAll();
        storeRepository.deleteAll();
        store = new Store();
        store.setAddress("Test Address");
        store.setCity("Test City");
        store.setName("Test Store");
        User storeOwner = new User();
        storeOwner.setId("storeOwner1");
        store.setUser(storeOwner);
        userRepository.save(storeOwner);
        storeRepository.save(store);
        user = new User();
        user.setId("user1");
        user = userRepository.save(user);

        opinion = new Opinion();
        opinion.setReported(false);
        opinion.setStars(1);
        opinion.setUser(user);
        opinion.setStore(store);
        opinion = opinionRepository.save(opinion);
    }

    @Test
    void testSaveOpinionReport_Success() {
        OpinionReport report = new OpinionReport(opinion, user);
        OpinionReport saved = opinionReportService.saveOpinionReport(report);

        assertNotNull(saved.getId());
        assertEquals(opinion.getId(), saved.getOpinion().getId());
        assertEquals(user.getId(), saved.getUser().getId());
        assertFalse(saved.getOpinion().isReported());
    }

    @Test
    void testSaveOpinionReport_DuplicateThrows() {
        OpinionReport report = new OpinionReport(opinion, user);
        opinionReportService.saveOpinionReport(report);

        OpinionReport duplicate = new OpinionReport(opinion, user);
        assertThrows(IllegalArgumentException.class, () -> opinionReportService.saveOpinionReport(duplicate));
    }

    @Test
    void testSaveOpinionReport_ThresholdSetsReported() {
        for (int i = 0; i < 4; i++) {
            User u = new User();
            u.setId("user" + (i + 2));
            userRepository.save(u);
            opinionReportService.saveOpinionReport(new OpinionReport(opinion, u));
        }
        OpinionReport report = new OpinionReport(opinion, user);
        opinionReportService.saveOpinionReport(report);

        Opinion updatedOpinion = opinionRepository.findById(opinion.getId()).orElseThrow();
        assertTrue(updatedOpinion.isReported());
    }

    @Test
    void testDeleteOpinionReportById() {
        OpinionReport report = new OpinionReport(opinion, user);
        OpinionReport saved = opinionReportService.saveOpinionReport(report);

        boolean deleted = opinionReportService.deleteOpinionReportById(saved.getId());
        assertTrue(deleted);

        Optional<OpinionReport> found = opinionReportRepository.findById(saved.getId());
        assertFalse(found.isPresent());
    }
}