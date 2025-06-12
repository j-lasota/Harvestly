package com.backend.APITests;

import com.backend.config.GraphQLScalarConfig;
import com.backend.controller.OwnProductController;
import com.backend.model.OwnProduct;
import com.backend.model.Product;
import com.backend.model.Store;
import com.backend.service.OwnProductService;
import com.backend.service.ProductService;
import com.backend.service.StoreService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static graphql.Assert.assertFalse;
import static graphql.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
class OwnProductAPITests {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private OwnProductService ownProductService;

    @MockitoBean
    private StoreService storeService;

    @MockitoBean
    private ProductService productService;

    @Test
    void ownProductById_ReturnsOwnProduct_WhenOwnProductExists() {
        Long ownProductId = 1L;
        Store store = new Store();
        store.setId(1L);
        store.setName("Test Store");

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");

        OwnProduct mockOwnProduct = new OwnProduct();
        mockOwnProduct.setId(ownProductId);
        mockOwnProduct.setStore(store);
        mockOwnProduct.setProduct(product);
        mockOwnProduct.setPrice(BigDecimal.valueOf(9.99));
        mockOwnProduct.setQuantity(50);
        mockOwnProduct.setImageUrl("image.jpg");

        when(ownProductService.getOwnProductById(ownProductId)).thenReturn(Optional.of(mockOwnProduct));

        String query = """
                query {
                  ownProductById(id: 1) {
                    id
                    store {
                      id
                      name
                    }
                    product {
                      id
                      name
                    }
                    price
                    quantity
                    imageUrl
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("ownProductById")
                .entity(OwnProduct.class)
                .isEqualTo(mockOwnProduct);
    }

    @Test
    void ownProductById_ReturnsNull_WhenOwnProductDoesNotExist() {
        Long ownProductId = 999L;
        when(ownProductService.getOwnProductById(ownProductId)).thenReturn(Optional.empty());

        String query = """
                query {
                  ownProductById(id: 999) {
                    id
                    price
                    quantity
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("ownProductById")
                .valueIsNull();
    }

    @Test
    void ownProducts_ReturnsAllOwnProducts() {
        Store store = new Store();
        store.setId(1L);
        store.setName("Test Store");

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Apple");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Orange");

        List<OwnProduct> mockOwnProducts = Arrays.asList(
                new OwnProduct(1L, store, product1, BigDecimal.valueOf(2.99), 100, "apple.jpg"),
                new OwnProduct(2L, store, product2, BigDecimal.valueOf(3.49), 80, "orange.jpg")
        );

        when(ownProductService.getAllOwnProducts()).thenReturn(mockOwnProducts);

        String query = """
                query {
                  ownProducts {
                    id
                    price
                    quantity
                    product {
                      name
                    }
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("ownProducts")
                .entityList(OwnProduct.class)
                .hasSize(2);
    }

    @Test
    void ownProductsByStore_ReturnsOwnProductsForStore() {
        Long storeId = 1L;
        Store store = new Store();
        store.setId(storeId);

        Product product = new Product();
        product.setId(1L);

        List<OwnProduct> mockOwnProducts = Arrays.asList(
                new OwnProduct(1L, store, product, BigDecimal.valueOf(5.99), 30, "img1.jpg"),
                new OwnProduct(2L, store, product, BigDecimal.valueOf(6.99), 20, "img2.jpg")
        );

        when(ownProductService.getByStore(storeId)).thenReturn(mockOwnProducts);

        String query = """
                query {
                  ownProductsByStore(storeId: 1) {
                    id
                    price
                    quantity
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("ownProductsByStore")
                .entityList(OwnProduct.class)
                .hasSize(2);
    }

    @Test
    void createOwnProduct_ReturnsCreatedOwnProduct() {
        Long storeId = 1L;
        Long productId = 1L;
        BigDecimal price = BigDecimal.valueOf(12.99);
        int quantity = 50;
        String imageUrl = "new_product.jpg";

        Store store = new Store();
        store.setId(storeId);

        Product product = new Product();
        product.setId(productId);

        OwnProduct createdOwnProduct = new OwnProduct();
        createdOwnProduct.setId(1L);
        createdOwnProduct.setStore(store);
        createdOwnProduct.setProduct(product);
        createdOwnProduct.setPrice(price);
        createdOwnProduct.setQuantity(quantity);
        createdOwnProduct.setImageUrl(imageUrl);

        when(storeService.getStoreById(storeId)).thenReturn(Optional.of(store));
        when(productService.getProductById(productId)).thenReturn(Optional.of(product));
        when(ownProductService.save(any(OwnProduct.class))).thenReturn(createdOwnProduct);

        String mutation = """
                mutation {
                  createOwnProduct(
                    storeId: 1
                    productId: 1
                    price: 12.99
                    quantity: 50
                    imageUrl: "new_product.jpg"
                  ) {
                    id
                    price
                    quantity
                    imageUrl
                    store {
                      id
                    }
                    product {
                      id
                    }
                  }
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .path("createOwnProduct")
                .entity(OwnProduct.class)
                .satisfies(ownProduct -> {
                    assert ownProduct.getPrice().compareTo(price) == 0;
                    assert ownProduct.getQuantity() == quantity;
                    assert ownProduct.getImageUrl().equals(imageUrl);
                });
    }

    @Test
    void createOwnProduct_ThrowsError_WhenStoreNotFound() {
        Long storeId = 999L;
        Long productId = 1L;

        Product product = new Product();
        product.setId(productId);

        when(storeService.getStoreById(storeId)).thenReturn(Optional.empty());
        when(productService.getProductById(productId)).thenReturn(Optional.of(product));

        String mutation = """
                mutation {
                  createOwnProduct(
                    storeId: 999
                    productId: 1
                    price: 12.99
                    quantity: 50
                    imageUrl: "image.jpg"
                  ) {
                    id
                    price
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
    void createOwnProduct_ThrowsError_WhenProductNotFound() {
        Long storeId = 1L;
        Long productId = 999L;

        Store store = new Store();
        store.setId(storeId);

        when(storeService.getStoreById(storeId)).thenReturn(Optional.of(store));
        when(productService.getProductById(productId)).thenReturn(Optional.empty());

        String mutation = """
                mutation {
                  createOwnProduct(
                    storeId: 1
                    productId: 999
                    price: 12.99
                    quantity: 50
                    imageUrl: "image.jpg"
                  ) {
                    id
                    price
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
    void updateOwnProduct_ReturnsUpdatedOwnProduct() {
        Long ownProductId = 1L;
        Long storeId = 1L;
        Long productId = 1L;

        Store store = new Store();
        store.setId(storeId);
        store.setName("Test Store");

        Product product = new Product();
        product.setId(productId);
        product.setName("Test Product");

        OwnProduct originalOwnProduct = new OwnProduct();
        originalOwnProduct.setId(ownProductId);
        originalOwnProduct.setStore(store);
        originalOwnProduct.setProduct(product);
        originalOwnProduct.setPrice(BigDecimal.valueOf(9.99));
        originalOwnProduct.setQuantity(30);
        originalOwnProduct.setImageUrl("original_image.jpg");

        BigDecimal newPrice = BigDecimal.valueOf(19.99);
        int newQuantity = 75;
        String newImageUrl = "updated_image.jpg";

        OwnProduct updatedOwnProduct = new OwnProduct();
        updatedOwnProduct.setId(ownProductId);
        updatedOwnProduct.setStore(store);
        updatedOwnProduct.setProduct(product);
        updatedOwnProduct.setPrice(newPrice);
        updatedOwnProduct.setQuantity(newQuantity);
        updatedOwnProduct.setImageUrl(newImageUrl);

        when(ownProductService.getOwnProductById(ownProductId)).thenReturn(Optional.of(originalOwnProduct));
        when(ownProductService.updateOwnProduct(
                ownProductId, storeId, productId, newPrice, newQuantity, newImageUrl
        )).thenReturn(updatedOwnProduct);

        String mutation = """
            mutation {
              updateOwnProduct(
                id: 1
                storeId: 1
                productId: 1
                price: 19.99
                quantity: 75
                imageUrl: "updated_image.jpg"
              ) {
                id
                price
                quantity
                imageUrl
              }
            }
            """;

        graphQlTester.document(mutation)
                .execute()
                .path("updateOwnProduct")
                .entity(OwnProduct.class)
                .satisfies(ownProduct -> {
                    assertTrue(ownProduct.getPrice().compareTo(newPrice) == 0);
                    assertTrue(ownProduct.getQuantity() == newQuantity);
                    assertTrue(ownProduct.getImageUrl().equals(newImageUrl));
                });
    }

    @Test
    void updateOwnProduct_ThrowsError_WhenOwnProductNotFound() {
        when(ownProductService.updateOwnProduct(
                anyLong(), anyLong(), anyLong(), any(), any(), any()
        )).thenThrow(new IllegalArgumentException("OwnProduct not found"));

        String mutation = """
                mutation {
                  updateOwnProduct(
                    id: 999
                    storeId: 1
                    productId: 1
                    price: 19.99
                    quantity: 75
                    imageUrl: "updated_image.jpg"
                  ) {
                    id
                    price
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
    void deleteOwnProduct_ReturnsTrue_WhenOwnProductDeleted() {
        Long ownProductId = 1L;
        when(ownProductService.deleteOwnProductById(ownProductId)).thenReturn(true);

        String mutation = """
                mutation {
                  deleteOwnProduct(id: 1)
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .path("deleteOwnProduct")
                .entity(Boolean.class)
                .isEqualTo(true);
    }

    @Test
    void deleteOwnProduct_ReturnsFalse_WhenOwnProductNotFound() {
        Long ownProductId = 999L;
        when(ownProductService.deleteOwnProductById(ownProductId)).thenReturn(false);

        String mutation = """
                mutation {
                  deleteOwnProduct(id: 999)
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .path("deleteOwnProduct")
                .entity(Boolean.class)
                .isEqualTo(false);
    }
}