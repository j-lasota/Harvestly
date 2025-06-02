package com.backend.APITests;

import com.backend.config.GraphQLScalarConfig;
import com.backend.controller.StoreController;
import com.backend.model.Store;
import com.backend.model.User;
import com.backend.service.StoreService;
import com.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@GraphQlTest(StoreController.class)
@Import(GraphQLScalarConfig.class)
class StoreControllerTests {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockBean
    private StoreService storeService;

    @MockBean
    private UserService userService;

    @Test
    void stores_ReturnsAllStores() {
        // Arrange
        User user1 = new User();
        user1.setId("9b1deb4d-3b7d-4bad-9bdd-2b0d7b3cfd62 ");
        user1.setFirstName("John");
        user1.setLastName("Doe");

        User user2 = new User();
        user2.setId("2a6e8658-d6db-45d8-9131-e8f87b62ed75  ");
        user2.setFirstName("Jane");
        user2.setLastName("Smith");

        List<Store> mockStores = Arrays.asList(
                new Store(user1, "Farm Fresh", "Local produce", 40.7128, -74.0060, "New York", "123 Broadway", "image1.jpg", "farm-fresh"),
                new Store(user2, "Green Market", "Organic food", 34.0522, -118.2437, "Los Angeles", "456 Hollywood Blvd", "image2.jpg", "green-market")
        );

        when(storeService.getAllStores()).thenReturn(mockStores);

        // Act & Assert
        String query = """
                query {
                  stores {
                    name
                    description
                    city
                    address
                    imageUrl
                    slug
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("stores")
                .entityList(Store.class)
                .hasSize(2);
    }

    @Test
    void storeById_ReturnsStore_WhenStoreExists() {
        // Arrange
        Long storeId = 1L;
        User user = new User();
        user.setId("2a6e8658-d6db-45d8-9131-e8f87b62ed75  ");
        user.setFirstName("John");
        user.setLastName("Doe");

        Store mockStore = new Store(user, "Farm Fresh", "Local produce", 40.7128, -74.0060, "New York", "123 Broadway", "image1.jpg", "farm-fresh");
        mockStore.setId(storeId);
        when(storeService.getStoreById(storeId)).thenReturn(Optional.of(mockStore));

        // Act & Assert
        String query = """
                query {
                  storeById(id: 1) {
                    id
                    name
                    description
                    latitude
                    longitude
                    city
                    address
                    imageUrl
                    slug
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("storeById")
                .entity(Store.class)
                .satisfies(store -> {
                    assert store.getId().equals(storeId);
                    assert store.getName().equals("Farm Fresh");
                    assert store.getCity().equals("New York");
                    assert store.getSlug().equals("farm-fresh");
                });
    }

    @Test
    void storeById_ReturnsNull_WhenStoreDoesNotExist() {
        // Arrange
        Long storeId = 999L;
        when(storeService.getStoreById(storeId)).thenReturn(Optional.empty());

        // Act & Assert
        String query = """
                query {
                  storeById(id: 999) {
                    id
                    name
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("storeById")
                .valueIsNull();
    }

    @Test
    void storeBySlug_ReturnsStore_WhenStoreExists() {
        // Arrange
        String slug = "farm-fresh";
        User user = new User();
        user.setId("2a6e8658-d6db-45d8-9131-e8f87b62ed75  ");

        Store mockStore = new Store(user, "Farm Fresh", "Local produce", 40.7128, -74.0060, "New York", "123 Broadway", "image1.jpg", slug);
        mockStore.setId(1L);
        when(storeService.getStoreBySlug(slug)).thenReturn(mockStore);

        // Act & Assert
        String query = """
                query {
                  storeBySlug(slug: "farm-fresh") {
                    id
                    name
                    slug
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("storeBySlug")
                .entity(Store.class)
                .satisfies(store -> {
                    assert store.getName().equals("Farm Fresh");
                    assert store.getSlug().equals(slug);
                });
    }

    @Test
    void createStore_ReturnsCreatedStore() {
        // Arrange
        String userId = "2a6e8658-d6db-45d8-9131-e8f87b62ed75";
        User user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");

        Store createdStore = new Store(user, "New Farm", "Fresh vegetables", 41.8781, -87.6298, "Chicago", "789 State St", "new_image.jpg", "new-farm");
        createdStore.setId(1L);

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(storeService.generateUniqueSlug(anyString())).thenReturn("new-farm");
        when(storeService.saveStore(any(Store.class))).thenReturn(createdStore);

        // Act & Assert
        String mutation = """
                mutation {
                  createStore(
                    userId: "2a6e8658-d6db-45d8-9131-e8f87b62ed75"
                    name: "New Farm"
                    description: "Fresh vegetables"
                    latitude: 41.8781
                    longitude: -87.6298
                    city: "Chicago"
                    address: "789 State St"
                    imageUrl: "new_image.jpg"
                  ) {
                    id
                    name
                    description
                    city
                    address
                    imageUrl
                    slug
                    user {
                      id
                      firstName
                    }
                  }
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .path("createStore")
                .entity(Store.class)
                .satisfies(store -> {
                    assert store.getName().equals("New Farm");
                    assert store.getCity().equals("Chicago");
                    assert store.getUser().getId().equals(userId);
                    assert store.getSlug().equals("new-farm");
                });
    }

    @Test
    void createStore_ThrowsException_WhenUserNotFound() {
        // Arrange
        String userId = "2a6e8658-d6db-45d8-9131-e8f87b62ed75  ";
        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        String mutation = """
                mutation {
                  createStore(
                    userId: 999
                    name: "New Farm"
                    description: "Fresh vegetables"
                    latitude: 41.8781
                    longitude: -87.6298
                    city: "Chicago"
                    address: "789 State St"
                    imageUrl: "new_image.jpg"
                  ) {
                    id
                    name
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
    void updateStore_ReturnsUpdatedStore() {
        // Arrange
        Long storeId = 1L;
        Store originalStore = new Store();
        originalStore.setId(storeId);
        originalStore.setName("Old Name");
        originalStore.setDescription("Old Description");
        originalStore.setLatitude(10.0);
        originalStore.setLongitude(20.0);
        originalStore.setCity("Old City");
        originalStore.setAddress("Old Address");
        originalStore.setImageUrl("old_image.jpg");

        Store updatedStore = new Store();
        updatedStore.setId(storeId);
        updatedStore.setName("Updated Farm");
        updatedStore.setDescription("Updated description");
        updatedStore.setLatitude(42.3601);
        updatedStore.setLongitude(-71.0589);
        updatedStore.setCity("Boston");
        updatedStore.setAddress("101 Main St");
        updatedStore.setImageUrl("updated_image.jpg");

        when(storeService.getStoreById(storeId)).thenReturn(Optional.of(originalStore));
        when(storeService.updateStore(
                eq(storeId), anyString(), anyString(), anyDouble(), anyDouble(), anyString(), anyString(), anyString()
        )).thenReturn(updatedStore);

        // Act & Assert
        String mutation = """
                mutation {
                  updateStore(
                    id: 1
                    name: "Updated Farm"
                    description: "Updated description"
                    latitude: 42.3601
                    longitude: -71.0589
                    city: "Boston"
                    address: "101 Main St"
                    imageUrl: "updated_image.jpg"
                  ) {
                    id
                    name
                    description
                    latitude
                    longitude
                    city
                    address
                    imageUrl
                  }
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .path("updateStore")
                .entity(Store.class)
                .satisfies(store -> {
                    assert store.getId().equals(storeId);
                    assert store.getName().equals("Updated Farm");
                    assert store.getCity().equals("Boston");
                    assert store.getLatitude() == 42.3601;
                    assert store.getLongitude() == -71.0589;
                });
    }

    @Test
    void updateStore_ThrowsException_WhenStoreNotFound() {
        // Arrange
        Long storeId = 999L;
        when(storeService.getStoreById(storeId)).thenReturn(Optional.empty());
        when(storeService.updateStore(
                eq(storeId), anyString(), anyString(), anyDouble(), anyDouble(), anyString(), anyString(), anyString()
        )).thenThrow(new IllegalArgumentException("Store not found"));

        // Act & Assert
        String mutation = """
                mutation {
                  updateStore(
                    id: 999
                    name: "Updated Farm"
                    description: "Updated description"
                    latitude: 42.3601
                    longitude: -71.0589
                    city: "Boston"
                    address: "101 Main St"
                    imageUrl: "updated_image.jpg"
                  ) {
                    id
                    name
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
    void deleteStore_ReturnsTrue_WhenStoreDeleted() {
        // Arrange
        Long storeId = 1L;
        when(storeService.deleteStoreById(storeId)).thenReturn(true);

        // Act & Assert
        String mutation = """
                mutation {
                  deleteStore(id: 1)
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .path("deleteStore")
                .entity(Boolean.class)
                .isEqualTo(true);
    }

    @Test
    void deleteStore_ReturnsFalse_WhenStoreNotFound() {
        // Arrange
        Long storeId = 999L;
        when(storeService.deleteStoreById(storeId)).thenReturn(false);

        // Act & Assert
        String mutation = """
                mutation {
                  deleteStore(id: 999)
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .path("deleteStore")
                .entity(Boolean.class)
                .isEqualTo(false);
    }
}