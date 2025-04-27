package com.backend.IntegrationTests;

import com.backend.model.OwnProduct;
import com.backend.model.Product;
import com.backend.model.ProductCategory;
import com.backend.model.Shop;
import com.backend.repository.OwnProductRepository;
import com.backend.repository.ProductRepository;
import com.backend.repository.ShopRepository;
import com.backend.service.OwnProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class OwnProductServiceTests {

    @Autowired
    private OwnProductService ownProductService;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OwnProductRepository ownProductRepository;

    private Shop shop;
    private Product product;

    @BeforeEach
    void setUp() {
        ownProductRepository.deleteAll();
        shopRepository.deleteAll();
        productRepository.deleteAll();

        shop = new Shop();
        shop.setName("Test Shop");
        shop.setCity("Test City");
        shop.setAddress("Test Address");
        shop.setLatitude(10.0);
        shop.setLongitude(20.0);
        shop.setDescription("Description");
        shop.setImageUrl("http://example.com/shop.jpg");
        shop = shopRepository.save(shop);

        product = new Product();
        product.setName("Test Product");
        product.setCategory(ProductCategory.FRUIT);
        product = productRepository.save(product);
    }

    @Test
    void testSaveOwnProduct() {
        OwnProduct ownProduct = new OwnProduct();
        ownProduct.setShop(shop);
        ownProduct.setProduct(product);
        ownProduct.setPrice(BigDecimal.valueOf(9.99));
        ownProduct.setQuantity(100);
        ownProduct.setImageUrl("http://example.com/product.jpg");

        OwnProduct saved = ownProductService.save(ownProduct);

        assertNotNull(saved.getId());
        assertEquals(shop.getId(), saved.getShop().getId());
        assertEquals(product.getId(), saved.getProduct().getId());
    }

    @Test
    void testGetOwnProductById_Found() {
        OwnProduct saved = ownProductService.save(createSampleOwnProduct());

        Optional<OwnProduct> found = ownProductService.getOwnProductById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void testGetOwnProductById_NotFound() {
        Optional<OwnProduct> found = ownProductService.getOwnProductById(999L);

        assertFalse(found.isPresent());
    }

    @Test
    void testGetAllOwnProducts() {
        ownProductService.save(createSampleOwnProduct());
        ownProductService.save(createSampleOwnProduct());

        List<OwnProduct> all = ownProductService.getAllOwnProducts();

        assertEquals(2, all.size());
    }

    @Test
    void testGetByProduct() {
        OwnProduct saved = ownProductService.save(createSampleOwnProduct());

        List<OwnProduct> found = ownProductService.getByProduct(product.getId());

        assertEquals(1, found.size());
        assertEquals(saved.getId(), found.get(0).getId());
    }

    @Test
    void testUpdateOwnProduct() {
        OwnProduct saved = ownProductService.save(createSampleOwnProduct());

        BigDecimal newPrice = BigDecimal.valueOf(15.99);
        Integer newQuantity = 50;
        String newImageUrl = "http://example.com/newimage.jpg";

        OwnProduct updated = ownProductService.updateOwnProduct(
                saved.getId(),
                shop.getId(),
                product.getId(),
                newPrice,
                newQuantity,
                newImageUrl
        );

        assertEquals(newPrice, updated.getPrice());
        assertEquals(newQuantity, updated.getQuantity());
        assertEquals(newImageUrl, updated.getImageUrl());
    }

    @Test
    void testDeleteOwnProductById_Exists() {
        OwnProduct saved = ownProductService.save(createSampleOwnProduct());

        Boolean deleted = ownProductService.deleteOwnProductById(saved.getId());

        assertTrue(deleted);
        assertFalse(ownProductRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void testDeleteOwnProductById_NotExists() {
        Boolean deleted = ownProductService.deleteOwnProductById(404L);

        assertFalse(deleted);
    }

    private OwnProduct createSampleOwnProduct() {
        OwnProduct ownProduct = new OwnProduct();
        ownProduct.setShop(shop);
        ownProduct.setProduct(product);
        ownProduct.setPrice(BigDecimal.valueOf(5.00));
        ownProduct.setQuantity(20);
        ownProduct.setImageUrl("http://example.com/sample.jpg");
        return ownProduct;
    }
}
