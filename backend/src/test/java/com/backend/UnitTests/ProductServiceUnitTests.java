package com.backend.UnitTests;

import com.backend.model.Product;
import com.backend.model.ProductCategory;
import com.backend.repository.ProductRepository;
import com.backend.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceUnitTests {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }

    @Test
    void testGetAllProducts() {
        List<Product> products = List.of(
                new Product("Apple", ProductCategory.FRUIT),
                new Product("Carrot", ProductCategory.VEGETABLE)
        );
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetProductById_ProductExists() {
        Product product = new Product("Banana", ProductCategory.FRUIT);
        product.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductById_ProductDoesNotExist() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById(2L);

        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(2L);
    }

    @Test
    void testSaveProduct_Success() {
        Product product = new Product("Tomato", ProductCategory.VEGETABLE);

        when(productRepository.findAll()).thenReturn(List.of());
        when(productRepository.save(product)).thenReturn(product);

        Product savedProduct = productService.saveProduct(product);

        assertNotNull(savedProduct);
        assertEquals("Tomato", savedProduct.getName());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testSaveProduct_ProductAlreadyExists() {
        Product existingProduct = new Product("Cucumber", ProductCategory.VEGETABLE);
        Product newProduct = new Product("cucumber", ProductCategory.VEGETABLE);

        when(productRepository.findAll()).thenReturn(List.of(existingProduct));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.saveProduct(newProduct);
        });

        assertEquals("Product with the same name and category already exists.", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_Success() {
        Product existingProduct = new Product("Orange", ProductCategory.FRUIT);
        existingProduct.setId(3L);

        when(productRepository.findById(3L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product updatedProduct = productService.updateProduct(3L, "Mandarin", ProductCategory.FRUIT, true);

        assertEquals("Mandarin", updatedProduct.getName());
        assertTrue(updatedProduct.isVerified());
        assertEquals(ProductCategory.FRUIT, updatedProduct.getCategory());
        verify(productRepository, times(1)).findById(3L);
        verify(productRepository, times(1)).save(existingProduct);
    }

    @Test
    void testUpdateProduct_ProductNotFound() {
        when(productRepository.findById(4L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.updateProduct(4L, "Broccoli", ProductCategory.VEGETABLE, false);
        });

        assertEquals("Product not found", exception.getMessage());
        verify(productRepository, times(1)).findById(4L);
        verify(productRepository, times(0)).save(any());
    }

    @Test
    void testDeleteProductById_ProductExists() {
        Product product = new Product("Lettuce", ProductCategory.VEGETABLE);
        product.setId(5L);

        when(productRepository.findById(5L)).thenReturn(Optional.of(product));

        Boolean result = productService.deleteProductById(5L);

        assertTrue(result);
        verify(productRepository, times(1)).deleteById(5L);
    }

    @Test
    void testDeleteProductById_ProductDoesNotExist() {
        when(productRepository.findById(6L)).thenReturn(Optional.empty());

        Boolean result = productService.deleteProductById(6L);

        assertFalse(result);
        verify(productRepository, never()).deleteById(any());
    }
}