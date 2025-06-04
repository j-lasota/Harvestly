package com.backend.EndToEndTests;

import com.backend.model.Product;
import com.backend.model.ProductCategory;
import com.backend.repository.ProductRepository;
import com.backend.service.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
public class ProductModuleE2ETests {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    public void setUp() {
        // Clean up existing data
        productRepository.deleteAll();

        // Create test product
        testProduct = new Product("Test Apple", ProductCategory.FRUIT);
        testProduct = productRepository.save(testProduct);
    }

    @AfterEach
    public void tearDown() {
        // Clean up all test data
        productRepository.deleteAll();
    }

    @Test
    @Transactional
    public void testCompleteProductLifecycle() {
        // 1. Create a new product through GraphQL
        String createProductMutation = """
            mutation {
                createProduct(
                    name: "Organic Banana",
                    category: FRUIT
                ) {
                    id
                    name
                    category
                    verified
                }
            }
            """;

        // Execute the mutation and verify the response
        GraphQlTester.Response createResponse = graphQlTester
                .document(createProductMutation)
                .execute();

        Long productId = createResponse
                .path("createProduct.id")
                .entity(Long.class)
                .get();

        createResponse
                .path("createProduct.name").entity(String.class).isEqualTo("Organic Banana")
                .path("createProduct.category").entity(String.class).isEqualTo("FRUIT")
                .path("createProduct.verified").entity(Boolean.class).isEqualTo(false);

        // 2. Verify product exists in database
        Optional<Product> savedProduct = productService.getProductById(productId);
        assertTrue(savedProduct.isPresent());
        assertEquals("Organic Banana", savedProduct.get().getName());
        assertEquals(ProductCategory.FRUIT, savedProduct.get().getCategory());
        assertFalse(savedProduct.get().isVerified());

        // 3. Get the product by ID using GraphQL
        String getProductQuery = """
            query {
                productById(id: %d) {
                    id
                    name
                    category
                    verified
                }
            }
            """.formatted(productId);

        graphQlTester
                .document(getProductQuery)
                .execute()
                .path("productById.id").entity(String.class).isEqualTo(productId.toString())
                .path("productById.name").entity(String.class).isEqualTo("Organic Banana")
                .path("productById.category").entity(String.class).isEqualTo("FRUIT")
                .path("productById.verified").entity(Boolean.class).isEqualTo(false);

        // 4. Get all products
        String getAllProductsQuery = """
            query {
                products {
                    id
                    name
                    category
                    verified
                }
            }
            """;

        GraphQlTester.Response allProductsResponse = graphQlTester
                .document(getAllProductsQuery)
                .execute();

        List<Product> allProducts = productService.getAllProducts();
        assertEquals(2, allProducts.size()); // Test product + newly created product

        allProductsResponse
                .path("products").entityList(Product.class).hasSize(2);

        // 5. Update the product
        String updateProductMutation = """
            mutation {
                updateProduct(
                    id: %d,
                    name: "Premium Organic Banana",
                    category: FRUIT,
                    verified: true
                ) {
                    id
                    name
                    category
                    verified
                }
            }
            """.formatted(productId);

        graphQlTester
                .document(updateProductMutation)
                .execute()
                .path("updateProduct.id").entity(String.class).isEqualTo(productId.toString())
                .path("updateProduct.name").entity(String.class).isEqualTo("Premium Organic Banana")
                .path("updateProduct.category").entity(String.class).isEqualTo("FRUIT")
                .path("updateProduct.verified").entity(Boolean.class).isEqualTo(true);

        // 6. Verify update in the database
        Product updatedProduct = productService.getProductById(productId).orElseThrow();
        assertEquals("Premium Organic Banana", updatedProduct.getName());
        assertEquals(ProductCategory.FRUIT, updatedProduct.getCategory());
        assertTrue(updatedProduct.isVerified());

        // 7. Delete the product
        String deleteProductMutation = """
            mutation {
                deleteProduct(id: %d)
            }
            """.formatted(productId);

        graphQlTester
                .document(deleteProductMutation)
                .execute()
                .path("deleteProduct").entity(Boolean.class).isEqualTo(true);

        // 8. Verify product was deleted
        Optional<Product> deletedProduct = productService.getProductById(productId);
        assertTrue(deletedProduct.isEmpty());
    }

    @Test
    public void testProductCategoryChange() {
        // Create a product with FRUIT category
        String createProductMutation = """
            mutation {
                createProduct(
                    name: "Carrot",
                    category: FRUIT
                ) {
                    id
                    category
                }
            }
            """;

        Long productId = graphQlTester
                .document(createProductMutation)
                .execute()
                .path("createProduct.id")
                .entity(Long.class)
                .get();

        // Update the product category to VEGETABLE
        String updateCategoryMutation = """
            mutation {
                updateProduct(
                    id: %d,
                    category: VEGETABLE
                ) {
                    id
                    name
                    category
                }
            }
            """.formatted(productId);

        graphQlTester
                .document(updateCategoryMutation)
                .execute()
                .path("updateProduct.category").entity(String.class).isEqualTo("VEGETABLE");

        // Verify in the database
        Product updatedProduct = productService.getProductById(productId).orElseThrow();
        assertEquals(ProductCategory.VEGETABLE, updatedProduct.getCategory());
    }

    @Test
    public void testVerifyUnverifyProduct() {
        // First create an unverified product
        String createProductMutation = """
            mutation {
                createProduct(
                    name: "Organic Strawberry",
                    category: FRUIT
                ) {
                    id
                    verified
                }
            }
            """;

        Long productId = graphQlTester
                .document(createProductMutation)
                .execute()
                .path("createProduct.id")
                .entity(Long.class)
                .get();

        // Verify the product
        String verifyProductMutation = """
            mutation {
                updateProduct(
                    id: %d,
                    verified: true
                ) {
                    id
                    verified
                }
            }
            """.formatted(productId);

        graphQlTester
                .document(verifyProductMutation)
                .execute()
                .path("updateProduct.verified").entity(Boolean.class).isEqualTo(true);

        // Now unverify the product
        String unverifyProductMutation = """
            mutation {
                updateProduct(
                    id: %d,
                    verified: false
                ) {
                    id
                    verified
                }
            }
            """.formatted(productId);

        graphQlTester
                .document(unverifyProductMutation)
                .execute()
                .path("updateProduct.verified").entity(Boolean.class).isEqualTo(false);
    }

    @Test
    public void testPartialProductUpdate() {
        // Create a product
        String createProductMutation = """
            mutation {
                createProduct(
                    name: "Cucumber",
                    category: VEGETABLE
                ) {
                    id
                    name
                    category
                    verified
                }
            }
            """;

        Long productId = graphQlTester
                .document(createProductMutation)
                .execute()
                .path("createProduct.id")
                .entity(Long.class)
                .get();

        // Update only the name
        String updateNameMutation = """
            mutation {
                updateProduct(
                    id: %d,
                    name: "Organic Cucumber"
                ) {
                    id
                    name
                    category
                    verified
                }
            }
            """.formatted(productId);

        graphQlTester
                .document(updateNameMutation)
                .execute()
                .path("updateProduct.name").entity(String.class).isEqualTo("Organic Cucumber")
                .path("updateProduct.category").entity(String.class).isEqualTo("VEGETABLE") // Category should remain unchanged
                .path("updateProduct.verified").entity(Boolean.class).isEqualTo(false);     // Verified should remain unchanged
    }

    @Test
    public void testInvalidProductId() {
        // Test retrieving a product with invalid ID
        String invalidIdQuery = """
            query {
                productById(id: 999999) {
                    id
                    name
                }
            }
            """;

        graphQlTester
                .document(invalidIdQuery)
                .execute()
                .path("productById").valueIsNull();

        // Test updating a product with invalid ID
        String invalidUpdateMutation = """
            mutation {
                updateProduct(
                    id: 999999,
                    name: "Invalid Product"
                ) {
                    id
                }
            }
            """;

        graphQlTester
                .document(invalidUpdateMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertFalse(errors.isEmpty());
                });

        // Test deleting a product with invalid ID
        String invalidDeleteMutation = """
            mutation {
                deleteProduct(id: 999999)
            }
            """;

        graphQlTester
                .document(invalidDeleteMutation)
                .execute()
                .path("deleteProduct").entity(Boolean.class).isEqualTo(false);
    }

    @Test
    public void testQueryProductsListNotEmpty() {
        // Create a few products to ensure list is not empty
        productRepository.save(new Product("Test Tomato", ProductCategory.VEGETABLE));
        productRepository.save(new Product("Test Blueberry", ProductCategory.FRUIT));

        // Query all products
        String allProductsQuery = """
            query {
                products {
                    id
                    name
                    category
                }
            }
            """;

        // Should return at least 3 products (including test product from setup)
        graphQlTester
                .document(allProductsQuery)
                .execute()
                .path("products").entityList(Product.class).hasSizeGreaterThan(2);
    }
}