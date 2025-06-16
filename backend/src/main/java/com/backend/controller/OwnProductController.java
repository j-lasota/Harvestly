package com.backend.controller;

import com.backend.model.OwnProduct;
import com.backend.model.Product;
import com.backend.model.Store;
import com.backend.service.ProductService;
import com.backend.service.StoreService;
import com.backend.service.OwnProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
public class OwnProductController {
    private final OwnProductService ownProductService;
    private final StoreService storeService;
    private final ProductService productService;

    private static final Logger log = LoggerFactory.getLogger(OwnProductController.class);

    public OwnProductController(OwnProductService ownProductService, StoreService storeService, ProductService productService) {
        this.ownProductService = ownProductService;
        this.storeService = storeService;
        this.productService = productService;
    }

    /**
     * Creates a new own product.
     *
     * @param storeId the ID of the store
     * @param productId the ID of the product
     * @param price the price of the product
     * @param quantity the quantity of the product
     * @param imageUrl the URL of the product image
     * @return the created OwnProduct object
     * @throws ResponseStatusException if the store or product is not found,
     *                                 or if the product already exists in the store
     */

    @MutationMapping
    @PreAuthorize("isAuthenticated() and (@storeSecurity.isOwner(authentication, #storeId) or hasAuthority('SCOPE_manage:all'))")
    public OwnProduct createOwnProduct(@Argument Long storeId, @Argument Long productId, @Argument BigDecimal price,
                                       @Argument int quantity, @Argument(name = "imageUrl") String imageUrl) { // Jawnie nazwij argument, żeby uniknąć pomyłek

        log.info("Attempting to create OwnProduct for storeId: {}, productId: {}", storeId, productId);

        try {
            Optional<Store> shop = storeService.getStoreById(storeId);
            if(shop.isEmpty()) {
                log.error("Store with ID {} not found.", storeId);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Shop not found");
            }
            log.info("Store found: {}", shop.get().getName());

            Optional<Product> product = productService.getProductById(productId);
            if(product.isEmpty()) {
                log.error("Product with ID {} not found.", productId);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
            }
            log.info("Product found: {}", product.get().getName());

            if (ownProductService.existsByStoreIdAndProductId(storeId, productId)) {
                log.error("Product {} already exists in store {}.", productId, storeId);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product already exists in this store");
            }
            log.info("Product does not exist in store yet. Proceeding to save.");

            OwnProduct newOwnProduct = new OwnProduct(shop.get(), product.get(), price, quantity, imageUrl);
            log.info("Created OwnProduct entity. Calling save service...");

            OwnProduct savedProduct = ownProductService.save(newOwnProduct);
            log.info("Successfully saved OwnProduct with new ID: {}", savedProduct.getId());
            return savedProduct;

        } catch (Exception e) {
            // Złap WSZYSTKIE wyjątki i zaloguj je, aby zobaczyć, co się dzieje
            log.error("An unexpected error occurred during createOwnProduct execution", e);
            // Rzuć wyjątek dalej, aby GraphQL mógł go obsłużyć
            throw e;
        }
    }

    /**
     * Updates an existing own product.
     *
     * @param id the ID of the own product to update
     * @param storeId the ID of the store
     * @param productId the ID of the product
     * @param price the new price of the product
     * @param quantity the new quantity of the product
     * @param imageUrl the new URL of the product image
     * @return the updated OwnProduct object
     * @throws ResponseStatusException if the own product is not found
     */
    @PreAuthorize("isAuthenticated() and (@ownProductSecurity.isOwner(#id, authentication) or hasAuthority('SCOPE_manage:all'))")
    @MutationMapping
    public OwnProduct updateOwnProduct(@Argument Long id, @Argument Long storeId, @Argument Long productId,
                                       @Argument BigDecimal price, @Argument Integer quantity, @Argument String imageUrl) {
        Optional<OwnProduct> ownProduct = ownProductService.getOwnProductById(id);

        if(ownProduct.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        return ownProductService.updateOwnProduct(id, storeId, productId, price, quantity, imageUrl);
    }

    /**
     * Deletes an own product by its ID.
     *
     * @param id the ID of the own product
     * @return true if the product was successfully deleted, false otherwise
     */
    @PreAuthorize("isAuthenticated() and (@ownProductSecurity.isOwner(#id, authentication) or hasAuthority('SCOPE_manage:all'))")
    @MutationMapping
    public Boolean deleteOwnProduct(@Argument Long id) {
        return ownProductService.deleteOwnProductById(id);
    }

    /**
     * Retrieves an own product by its ID.
     *
     * @param id the ID of the own product
     * @return an Optional containing the OwnProduct if found, or empty if not found
     */
    @QueryMapping
    public Optional<OwnProduct> ownProductById(@Argument Long id) {
        return ownProductService.getOwnProductById(id);
    }

    /**
     * Retrieves all own products.
     *
     * @return a list of all OwnProduct objects
     */
    @QueryMapping
    public List<OwnProduct> ownProducts() {
        return ownProductService.getAllOwnProducts();
    }

    /**
     * Retrieves all own products for a specific store.
     *
     * @param storeId the ID of the store
     * @return a list of OwnProduct objects for the specified store
     */
    @QueryMapping
    public List<OwnProduct> ownProductsByStore(@Argument Long storeId) {
        return ownProductService.getByStore(storeId);
    }

}
