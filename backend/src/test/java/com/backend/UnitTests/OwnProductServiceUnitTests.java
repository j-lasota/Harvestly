package com.backend.UnitTests;

import com.backend.model.OwnProduct;
import com.backend.model.Product;
import com.backend.model.ProductCategory;
import com.backend.model.Store;
import com.backend.repository.OwnProductRepository;
import com.backend.repository.ProductRepository;
import com.backend.repository.StoreRepository;
import com.backend.service.OwnProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnProductServiceUnitTests {

    @Mock
    private OwnProductRepository ownProductRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private ProductRepository productRepository;

    private OwnProductService ownProductService;

    @BeforeEach
    void setUp() {
        ownProductService = new OwnProductService(ownProductRepository, storeRepository, productRepository);
    }

    @Test
    void testGetByProduct() {
        Long productId = 1L;
        List<OwnProduct> expectedProducts = List.of(new OwnProduct());

        when(ownProductRepository.findByProductId(productId)).thenReturn(expectedProducts);

        List<OwnProduct> result = ownProductService.getByProduct(productId);

        assertEquals(expectedProducts, result);
        verify(ownProductRepository, times(1)).findByProductId(productId);
    }

    @Test
    void testGetOwnProductById_Found() {
        Long id = 1L;
        OwnProduct ownProduct = new OwnProduct();
        ownProduct.setId(id);

        when(ownProductRepository.findById(id)).thenReturn(Optional.of(ownProduct));

        Optional<OwnProduct> result = ownProductService.getOwnProductById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(ownProductRepository, times(1)).findById(id);
    }

    @Test
    void testGetOwnProductById_NotFound() {
        when(ownProductRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<OwnProduct> result = ownProductService.getOwnProductById(2L);

        assertFalse(result.isPresent());
        verify(ownProductRepository, times(1)).findById(2L);
    }

    @Test
    @Transactional
    void testSaveOwnProduct() {
        OwnProduct ownProduct = new OwnProduct();

        when(ownProductRepository.save(ownProduct)).thenReturn(ownProduct);

        OwnProduct saved = ownProductService.save(ownProduct);

        assertNotNull(saved);
        verify(ownProductRepository, times(1)).save(ownProduct);
    }

    @Test
    void testGetAllOwnProducts() {
        List<OwnProduct> ownProducts = List.of(new OwnProduct(), new OwnProduct());

        when(ownProductRepository.findAll()).thenReturn(ownProducts);

        List<OwnProduct> result = ownProductService.getAllOwnProducts();

        assertEquals(2, result.size());
        verify(ownProductRepository, times(1)).findAll();
    }

    @Test
    void testUpdateOwnProduct_Success() {
        Long id = 1L;
        Long shopId = 2L;
        Long productId = 3L;
        BigDecimal price = new BigDecimal("19.99");
        Integer quantity = 5;
        String imageUrl = "image.jpg";

        OwnProduct existingOwnProduct = new OwnProduct();
        existingOwnProduct.setId(id);

        Store store = new Store("ShopName", "desc", 10.0, 10.0, "City", "Address", "img.jpg");
        Product product = new Product("Apple", ProductCategory.FRUIT);

        when(ownProductRepository.findById(id)).thenReturn(Optional.of(existingOwnProduct));
        when(storeRepository.findById(shopId)).thenReturn(Optional.of(store));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(ownProductRepository.save(any(OwnProduct.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OwnProduct updated = ownProductService.updateOwnProduct(id, shopId, productId, price, quantity, imageUrl, 0);

        assertNotNull(updated);
        assertEquals(store, updated.getStore());
        assertEquals(product, updated.getProduct());
        assertEquals(price, updated.getPrice());
        assertEquals(quantity, updated.getQuantity());
        assertEquals(imageUrl, updated.getImageUrl());

        verify(ownProductRepository, times(1)).findById(id);
        verify(storeRepository, times(1)).findById(shopId);
        verify(productRepository, times(1)).findById(productId);
        verify(ownProductRepository, times(1)).save(any(OwnProduct.class));
    }

    @Test
    void testUpdateOwnProduct_NotFound() {
        Long id = 1L;

        when(ownProductRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                ownProductService.updateOwnProduct(id, null, null, null, null, null, 0));

        assertEquals("Product not found", exception.getMessage());
        verify(ownProductRepository, times(1)).findById(id);
    }

    @Test
    void testDeleteOwnProductById_Success() {
        Long id = 1L;
        OwnProduct ownProduct = new OwnProduct();
        ownProduct.setId(id);

        when(ownProductRepository.findById(id)).thenReturn(Optional.of(ownProduct));

        Boolean deleted = ownProductService.deleteOwnProductById(id);

        assertTrue(deleted);
        verify(ownProductRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteOwnProductById_NotFound() {
        Long id = 2L;

        when(ownProductRepository.findById(id)).thenReturn(Optional.empty());

        Boolean deleted = ownProductService.deleteOwnProductById(id);

        assertFalse(deleted);
        verify(ownProductRepository, never()).deleteById(any());
    }
}