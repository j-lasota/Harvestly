package com.backend.controller;

import com.backend.model.OwnProduct;
import com.backend.model.Product;
import com.backend.model.ProductCategory;
import com.backend.service.ProductService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Fetch all products.
     *
     * @return List of all products.
     */
    @QueryMapping
    public List<Product> products() {
        return productService.getAllProducts();
    }

    /**
     * Fetch a product by its ID.
     *
     * @param id The ID of the product.
     * @return An Optional containing the product if found, otherwise empty.
     */
    @QueryMapping
    public Optional<Product> productById(@Argument Long id) {
        return productService.getProductById(id);
    }

    /**
     * Create a new product.
     *
     * @param name The name of the product.
     * @param category The category of the product.
     * @return The created product.
     */

    @PreAuthorize("isAuthenticated()")
    @MutationMapping
    public Product createProduct(@Argument String name, @Argument ProductCategory category) {
        return productService.saveProduct(new Product(name, category));
    }

    /**
     * Update an existing product.
     *
     * @param id The ID of the product to update.
     * @param name The new name of the product.
     * @param category The new category of the product.
     * @param verified Whether the product is verified.
     * @return The updated product.
     */
    @PreAuthorize("hasAuthority('SCOPE_manage:all')")
    @MutationMapping
    public Product updateProduct(@Argument Long id, @Argument String name, @Argument ProductCategory category,
                                 @Argument Boolean verified) {
        return productService.updateProduct(id, name, category, verified);
    }

    /**
     * Delete a product by its ID.
     *
     * @param id The ID of the product to delete.
     * @return True if the product was deleted, false otherwise.
     */
    @PreAuthorize("hasAuthority('SCOPE_manage:all')")
    @MutationMapping
    public Boolean deleteProduct(@Argument Long id) {
        return productService.deleteProductById(id);
    }

    /**
     * Fetch all unverified products.
     *
     * @return List of unverified products.
     */
    @PreAuthorize("hasAuthority('SCOPE_manage:all')")
    @QueryMapping
    public List<Product> unverifiedProducts() {
        return productService.getUnverifiedProducts();
    }

    /**
     * Fetch all verified products.
     *
     * @return List of verified products.
     */
    @QueryMapping
    public List<Product> verifiedProducts() {
        return productService.getVerifiedProducts();
    }
}
