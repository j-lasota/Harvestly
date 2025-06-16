package com.backend.EndToEndTests;

import com.backend.model.Store;
import com.backend.model.User;
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

import static graphql.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureGraphQlTester
@SpringBootTest
@Transactional
public class StoreReportModuleE2ETests {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    private Store store;

    private User user;

    @BeforeEach
    void setUp() {
        store = new Store();
        store.setName("E2E Test Store");
        store.setReported(false);
        user = new User();
        user.setId("e2euser1");
        store.setAddress("E2E Test Address");
        store.setCity("Test City");
        store.setUser(user);
        user = userRepository.save(user);
        store = storeRepository.save(store);



    }

    @Test
    void testReportStore_SuccessAndDuplicate() {
        String mutation = """
            mutation {
                reportStore(storeId: %d, userId: "%s") {
                    id
                    store { id }
                    user { id }
                }
            }
        """.formatted(store.getId(), user.getId());

        graphQlTester.document(mutation)
                .execute()
                .path("reportStore.id").hasValue();

        graphQlTester.document(mutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();

                });
    }

    @Test
    void testReportStore_ThresholdSetsReported() {
        for (int i = 0; i < 4; i++) {
            User u = new User();
            u.setId("e2euser" + (i + 2));
            userRepository.save(u);

            String mutation = """
                mutation {
                    reportStore(storeId: %d, userId: "%s") {
                        id
                    }
                }
            """.formatted(store.getId(), u.getId());

            graphQlTester.document(mutation).execute().path("reportStore.id").hasValue();
        }

        String mutation = """
            mutation {
                reportStore(storeId: %d, userId: "%s") {
                    id
                }
            }
        """.formatted(store.getId(), user.getId());

        graphQlTester.document(mutation).execute().path("reportStore.id").hasValue();

        Store updated = storeRepository.findById(store.getId()).orElseThrow();
        assertTrue(updated.isReported());
    }
}