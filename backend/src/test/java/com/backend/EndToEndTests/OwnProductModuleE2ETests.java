package com.backend.EndToEndTests;

import com.backend.model.OwnProduct;
import com.backend.model.Product;
import com.backend.model.ProductCategory;
import com.backend.model.Store;
import com.backend.model.User;
import com.backend.repository.OwnProductRepository;
import com.backend.repository.ProductRepository;
import com.backend.repository.StoreRepository;
import com.backend.repository.UserRepository;
import com.backend.service.OwnProductService;
import com.backend.service.StoreService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
public class OwnProductModuleE2ETests {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private OwnProductRepository ownProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OwnProductService ownProductService;


    private Store testStore;
    private Product testProduct1;
    private Product testProduct2;
    private User storeOwnerUser;

    @BeforeEach
    public void setUp() {
        ownProductRepository.deleteAll();
        productRepository.deleteAll();
        storeRepository.deleteAll();
        userRepository.deleteAll();

        storeOwnerUser = new User(
                UUID.randomUUID().toString(),
                "Store",
                "Owner",
                "storeowner@example.com",
                "123456789",
                0,
                "owner-img.jpg"
        );
        storeOwnerUser = userRepository.save(storeOwnerUser);

        testStore = new Store(
                storeOwnerUser,
                "Test Store",
                "A store for testing",
                45.0,
                45.0,
                "Test City",
                "123 Test Street",
                "store-img.jpg",
                "test-store"
        );
        testStore = storeRepository.save(testStore);

        testProduct1 = new Product("Apple", ProductCategory.FRUIT, true);
        testProduct2 = new Product("Carrot", ProductCategory.VEGETABLE, true);

        testProduct1 = productRepository.save(testProduct1);
        testProduct2 = productRepository.save(testProduct2);
    }

    @AfterEach
    public void tearDown() {
        ownProductRepository.deleteAll();
        productRepository.deleteAll();
        storeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    public void testCompleteOwnProductLifecycle() {
        String createOwnProductMutation = """
            mutation {
                createOwnProduct(
                    storeId: %d,
                    productId: %d,
                    price: 2.99,
                    quantity: 100,
                    imageUrl: "apple.jpg"
                ) {
                    id
                    price
                    quantity
                    imageUrl
                    product {
                        id
                        name
                    }
                    store {
                        id
                        name
                    }
                }
            }
            """.formatted(testStore.getId(), testProduct1.getId());

        GraphQlTester.Response createResponse = graphQlTester
                .document(createOwnProductMutation)
                .execute();

        Long ownProductId = createResponse
                .path("createOwnProduct.id")
                .entity(Long.class)
                .get();

        createResponse
                .path("createOwnProduct.price").entity(BigDecimal.class).isEqualTo(new BigDecimal("2.99"))
                .path("createOwnProduct.quantity").entity(Integer.class).isEqualTo(100)
                .path("createOwnProduct.imageUrl").entity(String.class).isEqualTo("apple.jpg")
                .path("createOwnProduct.product.id").entity(String.class).isEqualTo(testProduct1.getId().toString())
                .path("createOwnProduct.store.id").entity(String.class).isEqualTo(testStore.getId().toString());

        Optional<OwnProduct> savedOwnProduct = ownProductService.getOwnProductById(ownProductId);
        assertTrue(savedOwnProduct.isPresent());
        assertEquals(new BigDecimal("2.99"), savedOwnProduct.get().getPrice());
        assertEquals(100, savedOwnProduct.get().getQuantity());
        assertEquals("apple.jpg", savedOwnProduct.get().getImageUrl());

        String getOwnProductQuery = """
            query {
                ownProductById(id: %d) {
                    id
                    price
                    quantity
                    imageUrl
                    product {
                        id
                        name
                    }
                    store {
                        id
                        name
                    }
                }
            }
            """.formatted(ownProductId);

        graphQlTester
                .document(getOwnProductQuery)
                .execute()
                .path("ownProductById.id").entity(String.class).isEqualTo(ownProductId.toString())
                .path("ownProductById.price").entity(BigDecimal.class).isEqualTo(new BigDecimal("2.99"))
                .path("ownProductById.quantity").entity(Integer.class).isEqualTo(100)
                .path("ownProductById.imageUrl").entity(String.class).isEqualTo("apple.jpg");

        String createAnotherOwnProductMutation = """
            mutation {
                createOwnProduct(
                    storeId: %d,
                    productId: %d,
                    price: 1.49,
                    quantity: 50,
                    imageUrl: "carrot.jpg"
                ) {
                    id
                    product {
                        name
                    }
                }
            }
            """.formatted(testStore.getId(), testProduct2.getId());

        graphQlTester
                .document(createAnotherOwnProductMutation)
                .execute()
                .path("createOwnProduct.product.name").entity(String.class).isEqualTo("Carrot");

        String getAllOwnProductsQuery = """
            query {
                ownProducts {
                    id
                    price
                    quantity
                    imageUrl
                    product {
                        id
                        name
                    }
                    store {
                        id
                        name
                    }
                }
            }
            """;

        List<OwnProduct> allOwnProducts = ownProductService.getAllOwnProducts();
        assertEquals(2, allOwnProducts.size());

        graphQlTester
                .document(getAllOwnProductsQuery)
                .execute()
                .path("ownProducts").entityList(OwnProduct.class).hasSize(2);

        String updateOwnProductMutation = """
            mutation {
                updateOwnProduct(
                    id: %d,
                    productId: %d,
                    price: 3.49,
                    quantity: 75,
                    imageUrl: "updated-apple.jpg"
                ) {
                    id
                    price
                    quantity
                    imageUrl
                    product {
                        id
                        name
                    }
                }
            }
            """.formatted(ownProductId, testProduct1.getId());

        graphQlTester
                .document(updateOwnProductMutation)
                .execute()
                .path("updateOwnProduct.id").entity(String.class).isEqualTo(ownProductId.toString())
                .path("updateOwnProduct.price").entity(BigDecimal.class).isEqualTo(new BigDecimal("3.49"))
                .path("updateOwnProduct.quantity").entity(Integer.class).isEqualTo(75)
                .path("updateOwnProduct.imageUrl").entity(String.class).isEqualTo("updated-apple.jpg");

        OwnProduct updatedOwnProduct = ownProductService.getOwnProductById(ownProductId).orElseThrow();
        assertEquals(new BigDecimal("3.49"), updatedOwnProduct.getPrice());
        assertEquals(75, updatedOwnProduct.getQuantity());
        assertEquals("updated-apple.jpg", updatedOwnProduct.getImageUrl());

        String deleteOwnProductMutation = """
            mutation {
                deleteOwnProduct(id: %d)
            }
            """.formatted(ownProductId);

        graphQlTester
                .document(deleteOwnProductMutation)
                .execute()
                .path("deleteOwnProduct").entity(Boolean.class).isEqualTo(true);

        Optional<OwnProduct> deletedOwnProduct = ownProductService.getOwnProductById(ownProductId);
        assertTrue(deletedOwnProduct.isEmpty());
    }

    @Test
    public void testPartialOwnProductUpdate() {
        String createOwnProductMutation = """
            mutation {
                createOwnProduct(
                    storeId: %d,
                    productId: %d,
                    price: 2.99,
                    quantity: 100,
                    imageUrl: "apple.jpg"
                ) {
                    id
                }
            }
            """.formatted(testStore.getId(), testProduct1.getId());

        Long ownProductId = graphQlTester
                .document(createOwnProductMutation)
                .execute()
                .path("createOwnProduct.id")
                .entity(Long.class)
                .get();

        String updatePriceMutation = """
            mutation {
                updateOwnProduct(
                    id: %d,
                    price: 3.99
                ) {
                    id
                    price
                    quantity
                    imageUrl
                }
            }
            """.formatted(ownProductId);

        graphQlTester
                .document(updatePriceMutation)
                .execute()
                .path("updateOwnProduct.price").entity(BigDecimal.class).isEqualTo(new BigDecimal("3.99"))
                .path("updateOwnProduct.quantity").entity(Integer.class).isEqualTo(100)
                .path("updateOwnProduct.imageUrl").entity(String.class).isEqualTo("apple.jpg");

        String updateQuantityMutation = """
            mutation {
                updateOwnProduct(
                    id: %d,
                    quantity: 75
                ) {
                    id
                    price
                    quantity
                    imageUrl
                }
            }
            """.formatted(ownProductId);

        graphQlTester
                .document(updateQuantityMutation)
                .execute()
                .path("updateOwnProduct.price").entity(BigDecimal.class).isEqualTo(new BigDecimal("3.99"))
                .path("updateOwnProduct.quantity").entity(Integer.class).isEqualTo(75)
                .path("updateOwnProduct.imageUrl").entity(String.class).isEqualTo("apple.jpg");

        String updateImageUrlMutation = """
            mutation {
                updateOwnProduct(
                    id: %d,
                    imageUrl: "new-apple.jpg"
                ) {
                    id
                    price
                    quantity
                    imageUrl
                }
            }
            """.formatted(ownProductId);

        graphQlTester
                .document(updateImageUrlMutation)
                .execute()
                .path("updateOwnProduct.price").entity(BigDecimal.class).isEqualTo(new BigDecimal("3.99"))
                .path("updateOwnProduct.quantity").entity(Integer.class).isEqualTo(75)
                .path("updateOwnProduct.imageUrl").entity(String.class).isEqualTo("new-apple.jpg");
    }

    @Test
    public void testDuplicateOwnProducts() {
        String createFirstOwnProductMutation = """
            mutation {
                createOwnProduct(
                    storeId: %d,
                    productId: %d,
                    price: 2.99,
                    quantity: 100,
                    imageUrl: "apple.jpg"
                ) {
                    id
                }
            }
            """.formatted(testStore.getId(), testProduct1.getId());

        graphQlTester
                .document(createFirstOwnProductMutation)
                .execute()
                .path("createOwnProduct.id").entity(Long.class).isNotEqualTo(null);

        String createDuplicateOwnProductMutation = """
            mutation {
                createOwnProduct(
                    storeId: %d,
                    productId: %d,
                    price: 3.99,
                    quantity: 50,
                    imageUrl: "duplicate-apple.jpg"
                ) {
                    id
                }
            }
            """.formatted(testStore.getId(), testProduct1.getId());

        graphQlTester.document(createDuplicateOwnProductMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertFalse(errors.isEmpty());
                });
    }

    @Test
    public void testInvalidOwnProductOperations() {
        String getNonExistentOwnProductQuery = """
            query {
                ownProductById(id: 999999) {
                    id
                    price
                }
            }
            """;

        graphQlTester
                .document(getNonExistentOwnProductQuery)
                .execute()
                .path("ownProductById")
                .valueIsNull();

        String updateNonExistentOwnProductMutation = """
            mutation {
                updateOwnProduct(
                    id: 999999,
                    price: 4.99
                ) {
                    id
                }
            }
            """;

        graphQlTester.document(updateNonExistentOwnProductMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });

        String deleteNonExistentOwnProductMutation = """
            mutation {
                deleteOwnProduct(id: 999999)
            }
            """;

        graphQlTester
                .document(deleteNonExistentOwnProductMutation)
                .execute()
                .path("deleteOwnProduct").entity(Boolean.class).isEqualTo(false);
    }

    @Test
    public void testCreateOwnProductWithInvalidStore() {
        String createWithInvalidStoreMutation = """
            mutation {
                createOwnProduct(
                    storeId: 999999,
                    productId: %d,
                    price: 2.99,
                    quantity: 100,
                    imageUrl: "apple.jpg"
                ) {
                    id
                }
            }
            """.formatted(testProduct1.getId());

        graphQlTester.document(createWithInvalidStoreMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });
    }

    @Test
    public void testCreateOwnProductWithInvalidProduct() {
        String createWithInvalidProductMutation = """
            mutation {
                createOwnProduct(
                    storeId: %d,
                    productId: 999999,
                    price: 2.99,
                    quantity: 100,
                    imageUrl: "product.jpg"
                ) {
                    id
                }
            }
            """.formatted(testStore.getId());

        graphQlTester.document(createWithInvalidProductMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });
    }

    @Test
    public void testOwnProductsForStore() {
        Store secondStore = new Store(
                storeOwnerUser,
                "Second Store",
                "Another store for testing",
                46.0,
                46.0,
                "Second City",
                "456 Test Avenue",
                "second-store-img.jpg",
                "second-store"
        );
        secondStore = storeRepository.save(secondStore);

        String createFirstStoreProductMutation = """
            mutation {
                createOwnProduct(
                    storeId: %d,
                    productId: %d,
                    price: 2.99,
                    quantity: 100,
                    imageUrl: "apple.jpg"
                ) {
                    id
                }
            }
            """.formatted(testStore.getId(), testProduct1.getId());

        graphQlTester
                .document(createFirstStoreProductMutation)
                .execute();

        String createSecondStoreProductMutation = """
            mutation {
                createOwnProduct(
                    storeId: %d,
                    productId: %d,
                    price: 1.49,
                    quantity: 50,
                    imageUrl: "carrot.jpg"
                ) {
                    id
                }
            }
            """.formatted(secondStore.getId(), testProduct2.getId());

        graphQlTester
                .document(createSecondStoreProductMutation)
                .execute();

        String getFirstStoreProductsQuery = """
            query {
                ownProductsByStore(storeId: %d) {
                    id
                    price
                    quantity
                    product {
                        name
                    }
                    store {
                        name
                    }
                }
            }
            """.formatted(testStore.getId());

        graphQlTester
                .document(getFirstStoreProductsQuery)
                .execute()
                .path("ownProductsByStore").entityList(OwnProduct.class).hasSize(1)
                .path("ownProductsByStore[0].product.name").entity(String.class).isEqualTo("Apple")
                .path("ownProductsByStore[0].store.name").entity(String.class).isEqualTo("Test Store");

        String getSecondStoreProductsQuery = """
            query {
                ownProductsByStore(storeId: %d) {
                    id
                    price
                    quantity
                    product {
                        name
                    }
                    store {
                        name
                    }
                }
            }
            """.formatted(secondStore.getId());

        graphQlTester
                .document(getSecondStoreProductsQuery)
                .execute()
                .path("ownProductsByStore").entityList(OwnProduct.class).hasSize(1)
                .path("ownProductsByStore[0].product.name").entity(String.class).isEqualTo("Carrot")
                .path("ownProductsByStore[0].store.name").entity(String.class).isEqualTo("Second Store");
    }

    @Test
    public void testNegativeValues() {
        String createWithNegativePriceMutation = """
            mutation {
                createOwnProduct(
                    storeId: %d,
                    productId: %d,
                    price: -2.99,
                    quantity: 100,
                    imageUrl: "apple.jpg"
                ) {
                    id
                }
            }
            """.formatted(testStore.getId(), testProduct1.getId());

        graphQlTester.document(createWithNegativePriceMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertFalse(errors.isEmpty());
                });

        String createWithNegativeQuantityMutation = """
            mutation {
                createOwnProduct(
                    storeId: %d,
                    productId: %d,
                    price: 2.99,
                    quantity: -10,
                    imageUrl: "apple.jpg"
                ) {
                    id
                }
            }
            """.formatted(testStore.getId(), testProduct1.getId());

        graphQlTester.document(createWithNegativeQuantityMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertFalse(errors.isEmpty());
                });
    }
}