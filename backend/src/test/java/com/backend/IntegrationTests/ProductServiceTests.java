package com.backend.IntegrationTests;

import com.backend.model.Product;
import com.backend.model.ProductCategory;
import com.backend.repository.ProductRepository;
import com.backend.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ProductServiceTests {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll(); // Clean up before every test
    }

    @Test
    void testGetAllProducts() {
        productRepository.save(new Product("Apple", ProductCategory.FRUIT));
        productRepository.save(new Product("Carrot", ProductCategory.VEGETABLE));

        List<Product> products = productService.getAllProducts();

        assertEquals(2, products.size());
    }

    @Test
    void testGetProductById_ProductExists() {
        Product product = productRepository.save(new Product("Banana", ProductCategory.FRUIT));

        Optional<Product> result = productService.getProductById(product.getId());

        assertTrue(result.isPresent());
        assertEquals("Banana", result.get().getName());
    }

    @Test
    void testGetProductById_ProductDoesNotExist() {
        Optional<Product> result = productService.getProductById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void testSaveProduct_Success() {
        Product product = new Product("Tomato", ProductCategory.VEGETABLE);

        Product savedProduct = productService.saveProduct(product);

        assertNotNull(savedProduct.getId());
        assertEquals("Tomato", savedProduct.getName());
    }

    @Test
    void testSaveProduct_ProductAlreadyExists() {
        productRepository.save(new Product("Cucumber", ProductCategory.VEGETABLE));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.saveProduct(new Product("cucumber", ProductCategory.VEGETABLE));
        });

        assertEquals("Product with the same name and category already exists.", exception.getMessage());
    }

    @Test
    void testUpdateProduct_Success() {
        Product product = productRepository.save(new Product("Orange", ProductCategory.FRUIT));

        Product updatedProduct = productService.updateProduct(product.getId(), "Mandarin", ProductCategory.FRUIT, true);

        assertEquals("Mandarin", updatedProduct.getName());
        assertTrue(updatedProduct.isVerified());
        assertEquals(ProductCategory.FRUIT, updatedProduct.getCategory());
    }

    @Test
    void testUpdateProduct_ProductNotFound() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.updateProduct(123L, "Broccoli", ProductCategory.VEGETABLE, false);
        });

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    void testDeleteProductById_ProductExists() {
        Product product = productRepository.save(new Product("Lettuce", ProductCategory.VEGETABLE));
        Long id = product.getId();

        Boolean result = productService.deleteProductById(id);

        assertTrue(result);
        assertFalse(productRepository.findById(id).isPresent());
    }

    @Test
    void testDeleteProductById_ProductDoesNotExist() {
        Boolean result = productService.deleteProductById(404L);

        assertFalse(result);
    }
}
