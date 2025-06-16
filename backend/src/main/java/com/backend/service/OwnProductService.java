package com.backend.service;

import com.backend.model.OwnProduct;
import com.backend.model.Product;
import com.backend.model.Store;
import com.backend.repository.OwnProductRepository;
import com.backend.repository.ProductRepository;
import com.backend.repository.StoreRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OwnProductService {
    private final OwnProductRepository ownProductRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;

    public OwnProductService(OwnProductRepository ownProductRepository, StoreRepository storeRepository, ProductRepository productRepository) {
        this.ownProductRepository = ownProductRepository;
        this.storeRepository = storeRepository;
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

    public OwnProduct updateOwnProduct(Long id, Long shopId, Long productId, BigDecimal price, Integer quantity, String imageUrl, Integer discount) {

        OwnProduct ownProduct = ownProductRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (shopId != null) {
            Store store = storeRepository.findById(shopId)
                    .orElseThrow(() -> new IllegalArgumentException("Shop not found"));
            ownProduct.setStore(store);
        }

        if (productId != null) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));
            ownProduct.setProduct(product);
        }
        if (price != null) {
            ownProduct.setPrice(price);
            ownProduct.setBasePrice(price);
        }

        if (ownProduct.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }

        if (quantity != null) {
            ownProduct.setQuantity(quantity);
        }

        if(ownProduct.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        if (imageUrl != null) {
            ownProduct.setImageUrl(imageUrl);
        }

        if (discount != null) {
            ownProduct.setPriceAfterDiscount(discount);
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
    public List<OwnProduct> getByStore(Long storeId) {
        return ownProductRepository.findByStoreIdAndProductVerifiedTrue(storeId);
    }
    public Boolean existsByStoreIdAndProductId(Long storeId, Long productId) {
        return ownProductRepository.existsByStoreIdAndProductId(storeId, productId);
    }
}
