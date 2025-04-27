package com.backend.service;

import com.backend.model.OwnProduct;
import com.backend.model.Product;
import com.backend.model.Shop;
import com.backend.repository.OwnProductRepository;
import com.backend.repository.ProductRepository;
import com.backend.repository.ShopRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OwnProductService {
    private final OwnProductRepository ownProductRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;

    public OwnProductService(OwnProductRepository ownProductRepository, ShopRepository shopRepository, ProductRepository productRepository) {
        this.ownProductRepository = ownProductRepository;
        this.shopRepository = shopRepository;
        this.productRepository = productRepository;
    }

    public List<OwnProduct> getByProduct(Long id) {
        return ownProductRepository.findByProductId(id);
    }

    public Optional<OwnProduct> getOwnProductById(Long id) {
        return ownProductRepository.findById(id);
    }

    public OwnProduct save(OwnProduct ownProduct) {
        return ownProductRepository.save(ownProduct);
    }

    public List<OwnProduct> getAllOwnProducts() {
        return ownProductRepository.findAll();
    }

    public OwnProduct updateOwnProduct(Long id, Long shopId, Long productId, BigDecimal price, Integer quantity, String imageUrl) {
        OwnProduct ownProduct = ownProductRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (shopId != null) {
            Shop shop = shopRepository.findById(shopId)
                    .orElseThrow(() -> new IllegalArgumentException("Shop not found"));
            ownProduct.setShop(shop);
        }

        if (productId != null) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));
            ownProduct.setProduct(product);
        }
        if (price != null && price.compareTo(BigDecimal.ZERO) >= 0) {
            ownProduct.setPrice(price);
        }

        if (quantity != null && quantity >= 0) {
            ownProduct.setQuantity(quantity);
        }
        if (imageUrl != null) {
            ownProduct.setImageUrl(imageUrl);
        }
        return ownProductRepository.save(ownProduct);
    }

    public Boolean deleteOwnProductById(Long id) {
        if (getOwnProductById(id).isPresent()) {
            ownProductRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
