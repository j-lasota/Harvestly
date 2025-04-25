package com.backend.service;

import com.backend.model.Product;
import com.backend.model.ProductCategory;
import com.backend.repository.ProductRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    //TODO: dodac sprawdzanie czy nie istnieje juz taki sam produkt
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }


    public Product updateProduct(Long id, String name, ProductCategory category, Boolean verified) {
        Product product = productRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (name != null) {
            product.setName(name);
        }

        if (category != null) {
            product.setCategory(category);
        }

        if (verified != null) {
            product.setVerified(verified);
        }
        return productRepository.save(product);
    }

    public Boolean deleteProductById(Long id) {
        if (getProductById(id).isPresent()) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
