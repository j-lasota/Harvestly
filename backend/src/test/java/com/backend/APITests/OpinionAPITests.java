package com.backend.APITests;

import com.backend.config.GraphQLScalarConfig;
import com.backend.controller.OpinionController;
import com.backend.model.Opinion;
import com.backend.model.Store;
import com.backend.model.User;
import com.backend.service.OpinionService;
import com.backend.service.StoreService;
import com.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static graphql.Assert.assertFalse;
import static graphql.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureGraphQlTester
class OpinionAPITests {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private OpinionService opinionService;

    @MockitoBean
    private StoreService storeService;

    @MockitoBean
    private UserService userService;

    @Test
    void createOpinion_ReturnsCreatedOpinion() {
        Long storeId = 1L;
        String userId = "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3cfd62";
        String description = "Great store with fresh products!";
        Integer stars = 5;

        Store mockStore = new Store();
        mockStore.setId(storeId);
        mockStore.setName("Test Store");

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");

        Opinion createdOpinion = new Opinion(mockStore, mockUser, description, stars);

        when(storeService.getStoreById(storeId)).thenReturn(Optional.of(mockStore));
        when(userService.getUserById(userId)).thenReturn(Optional.of(mockUser));
        when(opinionService.saveOpinion(any(Opinion.class))).thenReturn(createdOpinion);

        String mutation = """
                mutation {
                  createOpinion(
                    storeId: 1
                    userId: "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3cfd62"
                    description: "Great store with fresh products!"
                    stars: 5
                  ) {
                    description
                    stars
                    store {
                      id
                      name
                    }
                    user {
                      id
                      firstName
                      lastName
                    }
                  }
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .path("createOpinion")
                .entity(Opinion.class)
                .satisfies(opinion -> {
                    assert opinion.getDescription().equals(description);
                    assert opinion.getStars() == 5;
                    assert opinion.getStore().getId().equals(storeId);
                    assert opinion.getUser().getId().equals(userId);
                });
    }

    @Test
    void createOpinion_ThrowsException_WhenStoreNotFound() {
        Long storeId = 999L;
        String userId = "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3cfd62";

        User mockUser = new User();
        mockUser.setId(userId);

        when(storeService.getStoreById(storeId)).thenReturn(Optional.empty());
        when(userService.getUserById(userId)).thenReturn(Optional.of(mockUser));

        String mutation = """
                mutation {
                  createOpinion(
                    storeId: 999
                    userId: 2
                    description: "Description"
                    stars: 4
                  ) {
                    description
                    stars
                  }
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertFalse(errors.isEmpty());

                });
    }

    @Test
    void createOpinion_ThrowsException_WhenUserNotFound() {
        Long storeId = 1L;
        String userId = "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3cfd62";

        Store mockStore = new Store();
        mockStore.setId(storeId);

        when(storeService.getStoreById(storeId)).thenReturn(Optional.of(mockStore));
        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        String mutation = """
                mutation {
                  createOpinion(
                    storeId: 1
                    userId: 999
                    description: "Description"
                    stars: 3
                  ) {
                    description
                    stars
                  }
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertFalse(errors.isEmpty());
                });



    }


    @Test
    void getOpinionsByStoreId_ReturnsOpinions() {
        Long storeId = 1L;

        Store mockStore = new Store();
        mockStore.setId(storeId);
        mockStore.setName("Test Store");

        User user1 = new User();
        user1.setId("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3cfd62");
        user1.setFirstName("John");

        User user2 = new User();
        user2.setId("2a6e8658-d6db-45d8-9131-e8f87b62ed75");
        user2.setFirstName("Jane");

        List<Opinion> mockOpinions = Arrays.asList(
                new Opinion(mockStore, user1, "Great place!", 5),
                new Opinion(mockStore, user2, "Good products", 4)
        );

        when(opinionService.getOpinionsByStoreId(storeId)).thenReturn(mockOpinions);
        when(storeService.getStoreById(storeId)).thenReturn(Optional.of(mockStore));
        String query = """
                query {
                  opinionsByStoreId(storeId: 1) {
                    description
                    stars
                    user {
                      firstName
                    }
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("opinionsByStoreId")
                .entityList(Opinion.class)
                .hasSize(2)
                .satisfies(opinions -> {
                    assertTrue(opinions.get(0).getStars() == 5);
                    assertTrue(opinions.get(1).getStars() == 4);
                });
    }
}