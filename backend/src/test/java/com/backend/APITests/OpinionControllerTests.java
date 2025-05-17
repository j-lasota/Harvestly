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
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@GraphQlTest(OpinionController.class)
@Import(GraphQLScalarConfig.class)
class OpinionControllerTests {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockBean
    private OpinionService opinionService;

    @MockBean
    private StoreService storeService;

    @MockBean
    private UserService userService;

    @Test
    void createOpinion_ReturnsCreatedOpinion() {
        // Arrange
        Long storeId = 1L;
        Long userId = 2L;
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

        // Act & Assert
        String mutation = """
                mutation {
                  createOpinion(
                    storeId: 1
                    userId: 2
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
        // Arrange
        Long storeId = 999L;
        Long userId = 2L;

        User mockUser = new User();
        mockUser.setId(userId);

        when(storeService.getStoreById(storeId)).thenReturn(Optional.empty());
        when(userService.getUserById(userId)).thenReturn(Optional.of(mockUser));

        // Act & Assert
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
                    assert !errors.isEmpty();

                });
    }

    @Test
    void createOpinion_ThrowsException_WhenUserNotFound() {
        // Arrange
        Long storeId = 1L;
        Long userId = 999L;

        Store mockStore = new Store();
        mockStore.setId(storeId);

        when(storeService.getStoreById(storeId)).thenReturn(Optional.of(mockStore));
        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        // Act & Assert
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
                    assert !errors.isEmpty();
                });



    }

    // Additional test for a query to get opinions by storeId
    // This assumes you have or will implement such a query in your controller
    @Test
    void getOpinionsByStoreId_ReturnsOpinions() {
        // Arrange
        Long storeId = 1L;

        Store mockStore = new Store();
        mockStore.setId(storeId);
        mockStore.setName("Test Store");

        User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("John");

        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Jane");

        List<Opinion> mockOpinions = Arrays.asList(
                new Opinion(mockStore, user1, "Great place!", 5),
                new Opinion(mockStore, user2, "Good products", 4)
        );

        when(opinionService.getOpinionsByStoreId(storeId)).thenReturn(mockOpinions);
        when(storeService.getStoreById(storeId)).thenReturn(Optional.of(mockStore));
        // Act & Assert
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
                    assert opinions.get(0).getStars() == 5;
                    assert opinions.get(1).getStars() == 4;
                });
    }
}