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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
@TestPropertySource(properties = "app.method-security.enabled=false")

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
        productRepository.deleteAll();

        testProduct = new Product("Test Apple", ProductCategory.FRUIT);
        testProduct = productRepository.save(testProduct);
    }

    @AfterEach
    public void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    @Transactional
    public void testCompleteProductLifecycle() {
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

        Optional<Product> savedProduct = productService.getProductById(productId);
        assertTrue(savedProduct.isPresent());
        assertEquals("Organic Banana", savedProduct.get().getName());
        assertEquals(ProductCategory.FRUIT, savedProduct.get().getCategory());
        assertFalse(savedProduct.get().isVerified());

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
        assertEquals(2, allProducts.size());

        allProductsResponse
                .path("products").entityList(Product.class).hasSize(2);


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

        Product updatedProduct = productService.getProductById(productId).orElseThrow();
        assertEquals("Premium Organic Banana", updatedProduct.getName());
        assertEquals(ProductCategory.FRUIT, updatedProduct.getCategory());
        assertTrue(updatedProduct.isVerified());

        String deleteProductMutation = """
            mutation {
                deleteProduct(id: %d)
            }
            """.formatted(productId);

        graphQlTester
                .document(deleteProductMutation)
                .execute()
                .path("deleteProduct").entity(Boolean.class).isEqualTo(true);

        Optional<Product> deletedProduct = productService.getProductById(productId);
        assertTrue(deletedProduct.isEmpty());
    }

    @Test
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed75")

    public void testProductCategoryChange() {
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

        Product updatedProduct = productService.getProductById(productId).orElseThrow();
        assertEquals(ProductCategory.VEGETABLE, updatedProduct.getCategory());
    }

    @Test
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed75")
    public void testVerifyUnverifyProduct() {
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
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed75")
    public void testPartialProductUpdate() {
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
                .path("updateProduct.category").entity(String.class).isEqualTo("VEGETABLE")
                .path("updateProduct.verified").entity(Boolean.class).isEqualTo(false);
    }

    @Test
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed75")
    public void testInvalidProductId() {
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
        productRepository.save(new Product("Test Tomato", ProductCategory.VEGETABLE));
        productRepository.save(new Product("Test Blueberry", ProductCategory.FRUIT));

        String allProductsQuery = """
            query {
                products {
                    id
                    name
                    category
                }
            }
            """;

        graphQlTester
                .document(allProductsQuery)
                .execute()
                .path("products").entityList(Product.class).hasSizeGreaterThan(2);
    }
}