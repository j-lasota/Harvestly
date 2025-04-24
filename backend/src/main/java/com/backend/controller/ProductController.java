package com.backend.controller;

import com.backend.model.OwnProduct;
import com.backend.model.Product;
import com.backend.model.ProductCategory;
import com.backend.service.ProductService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @QueryMapping
    public List<Product> products() {
        return productService.getAllProducts();
    }

    @QueryMapping
    public Optional<Product> productById(@Argument Long id) {
        return productService.getProductById(id);
    }

    @MutationMapping
    public Product createProduct(@Argument String name, @Argument ProductCategory category) {
        return productService.saveProduct(new Product(name, category));
    }

    @MutationMapping
    public Boolean deleteProduct(@Argument Long id) {
        return productService.deleteProductById(id);
    }

    @SchemaMapping
    public List<OwnProduct> ownProducts(Product product) {
        return productById(product.getId()).get().getOwnProducts();
    }
}
