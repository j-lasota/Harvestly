package com.backend.APITests;

import com.backend.config.GraphQLScalarConfig;
import com.backend.controller.ProductController;
import com.backend.model.OwnProduct;
import com.backend.model.Product;
import com.backend.model.ProductCategory;
import com.backend.service.ProductService;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
@TestPropertySource(properties = "app.method-security.enabled=false")

class ProductAPITests {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private ProductService productService;

    @Test
    void products_ReturnsAllProducts() {
        List<Product> mockProducts = Arrays.asList(
                new Product("Apple", ProductCategory.FRUIT, true),
                new Product("Carrot", ProductCategory.VEGETABLE, true),
                new Product("Banana", ProductCategory.FRUIT, false)
        );
        when(productService.getAllProducts()).thenReturn(mockProducts);

        String query = """
                query {
                  products {
                    name
                    category
                    verified
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("products")
                .entityList(Product.class)
                .hasSize(3)
                .satisfies(products -> {
                    assert products.get(0).getName().equals("Apple");
                    assert products.get(1).getCategory() == ProductCategory.VEGETABLE;
                    assert !products.get(2).isVerified();
                });
    }

    @Test
    void productById_ReturnsProduct_WhenProductExists() {
        Long productId = 1L;
        Product mockProduct = new Product("Apple", ProductCategory.FRUIT, true);
        when(productService.getProductById(productId)).thenReturn(Optional.of(mockProduct));

        String query = """
                query {
                  productById(id: 1) {
                    name
                    category
                    verified
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("productById")
                .entity(Product.class)
                .satisfies(product -> {
                    assert product.getName().equals("Apple");
                    assert product.getCategory() == ProductCategory.FRUIT;
                    assert product.isVerified();
                });
    }

    @Test
    void productById_ReturnsNull_WhenProductDoesNotExist() {
        Long productId = 999L;
        when(productService.getProductById(productId)).thenReturn(Optional.empty());

        String query = """
                query {
                  productById(id: 999) {
                    name
                    category
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("productById")
                .valueIsNull();
    }

    @Test
    void createProduct_ReturnsCreatedProduct() {
        String productName = "Strawberry";
        ProductCategory category = ProductCategory.FRUIT;

        Product createdProduct = new Product(productName, category, false);
        when(productService.saveProduct(any(Product.class))).thenReturn(createdProduct);

        String mutation = """
                mutation {
                  createProduct(
                    name: "Strawberry"
                    category: FRUIT
                  ) {
                    name
                    category
                    verified
                  }
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .path("createProduct")
                .entity(Product.class)
                .satisfies(product -> {
                    assert product.getName().equals(productName);
                    assert product.getCategory() == category;
                    assert !product.isVerified();
                });
    }

    @Test
    void updateProduct_ReturnsUpdatedProduct() {
        Long productId = 1L;
        String updatedName = "Green Apple";
        ProductCategory updatedCategory = ProductCategory.FRUIT;
        boolean updatedVerified = true;

        Product updatedProduct = new Product(productId, updatedName, updatedCategory, updatedVerified);
        when(productService.updateProduct(eq(productId), anyString(), any(ProductCategory.class), anyBoolean()))
                .thenReturn(updatedProduct);

        String mutation = """
                mutation {
                  updateProduct(
                    id: 1
                    name: "Green Apple"
                    category: FRUIT
                    verified: true
                  ) {
                    id
                    name
                    category
                    verified
                  }
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .path("updateProduct")
                .entity(Product.class)
                .satisfies(product -> {
                    assert product.getId().equals(productId);
                    assert product.getName().equals(updatedName);
                    assert product.getCategory() == updatedCategory;
                    assert product.isVerified();
                });
    }

    @Test
    void deleteProduct_ReturnsTrue_WhenProductDeleted() {
        Long productId = 1L;
        when(productService.deleteProductById(productId)).thenReturn(true);

        String mutation = """
                mutation {
                  deleteProduct(id: 1)
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .path("deleteProduct")
                .entity(Boolean.class)
                .isEqualTo(true);
    }

    @Test
    void deleteProduct_ReturnsFalse_WhenProductNotFound() {
        Long productId = 999L;
        when(productService.deleteProductById(productId)).thenReturn(false);

        String mutation = """
                mutation {
                  deleteProduct(id: 999)
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .path("deleteProduct")
                .entity(Boolean.class)
                .isEqualTo(false);
    }

    @Test
    void ownProducts_ReturnsOwnProductsForProduct() {
        Long productId = 1L;
        Product product = new Product(productId, "Apple", ProductCategory.FRUIT, true);

        OwnProduct ownProduct1 = new OwnProduct();
        ownProduct1.setId(1L);
        ownProduct1.setProduct(product);
        ownProduct1.setQuantity(100);

        OwnProduct ownProduct2 = new OwnProduct();
        ownProduct2.setId(2L);
        ownProduct2.setProduct(product);
        ownProduct2.setQuantity(50);

        List<OwnProduct> ownProducts = Arrays.asList(ownProduct1, ownProduct2);
        product.setOwnProducts(ownProducts);

        when(productService.getProductById(productId)).thenReturn(Optional.of(product));

        String query = """
                query {
                  productById(id: 1) {
                    name
                    ownProducts {
                      id
                      quantity
                    }
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("productById.ownProducts")
                .entityList(OwnProduct.class)
                .hasSize(2);
    }

    @Test
    void ownProducts_ReturnsEmptyList_WhenNoOwnProductsExist() {
        Long productId = 2L;
        Product product = new Product(productId, "Banana", ProductCategory.FRUIT, true);
        product.setOwnProducts(Collections.emptyList());

        when(productService.getProductById(productId)).thenReturn(Optional.of(product));

        String query = """
                query {
                  productById(id: 2) {
                    name
                    ownProducts {
                      id
                    }
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("productById.ownProducts")
                .entityList(OwnProduct.class)
                .hasSize(0);
    }
}