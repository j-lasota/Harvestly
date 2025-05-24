package com.backend.APITests;

import com.backend.config.GraphQLScalarConfig;
import com.backend.controller.UserController;
import com.backend.model.User;
import com.backend.service.StoreService;
import com.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@GraphQlTest(UserController.class)  // Specify the controller to test
@Import(GraphQLScalarConfig.class)

class UserControllerTests {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockBean
    private UserService userService;

    @MockBean
    private StoreService storeService;

    @Test
    void userById_ReturnsUser_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        User mockUser = new User("John", "Doe", "john@example.com", "123456789", 0, "img.jpg");
        when(userService.getUserById(userId)).thenReturn(Optional.of(mockUser));

        // Act & Assert
        String query = """
                query {
                  userById(id: 1) {
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
        // Arrange
        List<User> mockUsers = Arrays.asList(
                new User("John", "Doe", "john@example.com", "123456789", 0, "img1.jpg"),
                new User("Jane", "Smith", "jane@example.com", "987654321", 1, "img2.jpg")
        );
        when(userService.getAllUsers()).thenReturn(mockUsers);

        // Act & Assert
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
        // Arrange
        User createdUser = new User("John", "Doe", "john@example.com", "123456789", 0, "img.jpg");
        when(userService.saveUser(any(User.class))).thenReturn(createdUser);

        // Act & Assert
        String mutation = """
                mutation {
                  createUser(
                    firstName: "John"
                    lastName: "Doe"
                    email: "john@example.com"
                    password: "password"
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
        // Arrange
        Long userId = 1L;
        User updatedUser = new User("John", "Updated", "john@example.com", "123456789", 1, "newimg.jpg");
        when(userService.updateUser(anyLong(), any(), any(), any(), any(), any(), any()))
                .thenReturn(updatedUser);

        // Act & Assert
        String mutation = """
                mutation {
                  updateUser(
                    id: 1
                    firstName: "John"
                    lastName: "Updated"
                    email: "john@example.com"
                    password: "newpassword"
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
        // Arrange
        Long userId = 1L;
        when(userService.deleteUserById(userId)).thenReturn(true);

        // Act & Assert
        String mutation = """
                mutation {
                  deleteUser(id: 1)
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
        // Arrange
        String email = "john@example.com";
        User mockUser = new User("John", "Doe", email, "123456789", 0, "img.jpg");
        when(userService.getUserByEmail(email)).thenReturn(Optional.of(mockUser));

        // Act & Assert
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