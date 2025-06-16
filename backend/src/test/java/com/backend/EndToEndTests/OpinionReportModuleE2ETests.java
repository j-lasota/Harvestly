package com.backend.EndToEndTests;

import com.backend.model.Opinion;
import com.backend.model.Store;
import com.backend.model.User;
import com.backend.repository.OpinionRepository;
import com.backend.repository.StoreRepository;
import com.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureGraphQlTester
@SpringBootTest
@Transactional
public class OpinionReportModuleE2ETests {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private OpinionRepository opinionRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    private Store store;
    private User user;
    private Opinion opinion;

    @BeforeEach
    void setUp() {
        store = new Store();
        store.setName("E2E Test Store");
        store.setReported(false);
        store.setAddress("E2E Test Address");
        store.setCity("Test City");
        User storeOwner = new User();
        storeOwner.setId("e2estoreowner1");
        store.setUser(storeOwner);
        userRepository.save(storeOwner);
        store = storeRepository.save(store);

        user = new User();
        user.setId("e2euser1");
        user = userRepository.save(user);

        opinion = new Opinion();
        opinion.setReported(false);
        opinion.setStars(1);
        opinion.setUser(user);
        opinion.setStore(store);
        opinion = opinionRepository.save(opinion);
    }

    @Test
    void testReportOpinion_SuccessAndDuplicate() {
        String mutation = """
            mutation {
                reportOpinion(opinionId: %d, userId: "%s") {
                    id
                    opinion { id }
                    user { id }
                }
            }
        """.formatted(opinion.getId(), user.getId());

        graphQlTester.document(mutation)
                .execute()
                .path("reportOpinion.id").hasValue();

        graphQlTester.document(mutation)
                .execute()
                .errors()
                .satisfy(errors -> assertFalse(errors.isEmpty()));
    }

    @Test
    void testReportOpinion_ThresholdSetsReported() {
        IntStream.range(0, 4).forEach(i -> {
            User u = new User();
            u.setId("e2euser" + (i + 2));
            userRepository.save(u);

            String mutation = """
                mutation {
                    reportOpinion(opinionId: %d, userId: "%s") {
                        id
                    }
                }
            """.formatted(opinion.getId(), u.getId());

            graphQlTester.document(mutation).execute().path("reportOpinion.id").hasValue();
        });

        String mutation = """
            mutation {
                reportOpinion(opinionId: %d, userId: "%s") {
                    id
                }
            }
        """.formatted(opinion.getId(), user.getId());

        graphQlTester.document(mutation).execute().path("reportOpinion.id").hasValue();

        Opinion updated = opinionRepository.findById(opinion.getId()).orElseThrow();
        assertTrue(updated.isReported());
    }
}