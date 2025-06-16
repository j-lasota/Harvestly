package com.backend.APITests;

import com.backend.config.GraphQLScalarConfig;
import com.backend.controller.UserController;
import com.backend.model.User;
import com.backend.service.StoreService;
import com.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
@TestPropertySource(properties = "app.method-security.enabled=false")

class UserAPITests {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private StoreService storeService;

    @Test
    void userById_ReturnsUser_WhenUserExists() {
        String userId = "2a6e8658-d6db-45d8-9131-e8f87b62ed75";
        User mockUser = new User("2a6e8658-d6db-45d8-9131-e8f87b62ed75","John", "Doe", "john@example.com", "123456789", 0, "img.jpg");
        when(userService.getUserById(userId)).thenReturn(Optional.of(mockUser));

        String query = """
                query {
                  userById(id: "2a6e8658-d6db-45d8-9131-e8f87b62ed75") {
                    id
                    firstName
                    lastName
                    email
                    phoneNumber
                    img
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("userById")
                .entity(User.class)
                .isEqualTo(mockUser);
    }

    @Test
    void users_ReturnsAllUsers() {
        List<User> mockUsers = Arrays.asList(
                new User("2a6e8658-d6db-45d8-9131-e8f87b62ed75", "John", "Doe", "john@example.com", "123456789", 0, "img1.jpg"),
                new User("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3cfd62","Jane", "Smith", "jane@example.com", "987654321", 1, "img2.jpg")
        );
        when(userService.getAllUsers()).thenReturn(mockUsers);

        String query = """
                query {
                  users {
                    firstName
                    lastName
                    email
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("users")
                .entityList(User.class)
                .hasSize(2);
    }

    @Test
    void createUser_ReturnsCreatedUser() {
        User createdUser = new User("2a6e8658-d6db-45d8-9131-e8f87b62ed75","John", "Doe", "john@example.com", "123456789", 0, "img.jpg");
        when(userService.saveUser(any(User.class))).thenReturn(createdUser);

        String mutation = """
                mutation {
                  createUser(
                    id: "2a6e8658-d6db-45d8-9131-e8f87b62ed75"
                    firstName: "John"
                    lastName: "Doe"
                    email: "john@example.com"
                    phoneNumber: "123456789"
                    img: "img.jpg"
                  ) {
                    firstName
                    lastName
                    email
                  }
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .path("createUser")
                .entity(User.class)
                .satisfies(user -> {
                    assert user.getFirstName().equals("John");
                    assert user.getLastName().equals("Doe");
                    assert user.getEmail().equals("john@example.com");
                });
    }

    @Test
    void updateUser_ReturnsUpdatedUser() {
        Long userId = 1L;
        User updatedUser = new User("2a6e8658-d6db-45d8-9131-e8f87b62ed75","John", "Updated", "john@example.com", "123456789", 1, "newimg.jpg");
        when(userService.updateUser(eq("2a6e8658-d6db-45d8-9131-e8f87b62ed75"), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(updatedUser);

        String mutation = """
                mutation {
                  updateUser(
                    id: "2a6e8658-d6db-45d8-9131-e8f87b62ed75"
                    firstName: "John"
                    lastName: "Updated"
                    email: "john@example.com"
                    phoneNumber: "123456789"
                    tier: 1
                    img: "newimg.jpg"
                  ) {
                    firstName
                    lastName
                    email
                    tier
                  }
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .path("updateUser")
                .entity(User.class)
                .satisfies(user -> {
                    assert user.getFirstName().equals("John");
                    assert user.getLastName().equals("Updated");
                    assert user.getTier() == 1;
                });
    }

    @Test
    void deleteUser_ReturnsTrue_WhenUserDeleted() {
        String userId = "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3cfd62";
        when(userService.deleteUserById(userId)).thenReturn(true);

        String mutation = """
                mutation {
                  deleteUser(id: "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3cfd62")
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .path("deleteUser")
                .entity(Boolean.class)
                .isEqualTo(true);
    }

    @Test
    void userByEmail_ReturnsUser() {
        String email = "john@example.com";
        User mockUser = new User("2a6e8658-d6db-45d8-9131-e8f87b62ed75","John", "Doe", email, "123456789", 0, "img.jpg");
        when(userService.getUserByEmail(email)).thenReturn(Optional.of(mockUser));

        String query = """
                query {
                  userByEmail(email: "john@example.com") {
                    firstName
                    lastName
                    email
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("userByEmail")
                .entity(User.class)
                .satisfies(user -> {
                    assert user.getEmail().equals(email);
                    assert user.getFirstName().equals("John");
                });
    }
}