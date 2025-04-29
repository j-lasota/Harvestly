package com.backend.controller;

import com.backend.model.OwnProduct;
import com.backend.model.Product;
import com.backend.model.Shop;
import com.backend.service.ProductService;
import com.backend.service.ShopService;
import com.backend.service.OwnProductService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
public class OwnProductController {
    private final OwnProductService ownProductService;
    private final ShopService shopService;
    private final ProductService productService;
    
    public OwnProductController(OwnProductService ownProductService, ShopService shopService, ProductService productService) {
        this.ownProductService = ownProductService;
        this.shopService = shopService;
        this.productService = productService;
    }

    @MutationMapping
    public OwnProduct createOwnProduct(@Argument Long shopId, @Argument Long productId, @Argument BigDecimal price,
                                  @Argument int quantity, @Argument String imageUrl) {
        Optional<Shop> shop = shopService.getShopById(shopId);
        Optional<Product> product = productService.getProductById(productId);
        if(shop.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Shop not found");
        }
        if(product.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        return ownProductService.save(new OwnProduct(shop.get(), product.get(), price, quantity, imageUrl));
    }

    @MutationMapping
    public OwnProduct updateOwnProduct(@Argument Long id, @Argument Long shopId, @Argument Long productId,
                                       @Argument BigDecimal price, @Argument Integer quantity, @Argument String imageUrl) {
        Optional<OwnProduct> ownProduct = ownProductService.getOwnProductById(id);
        if(ownProduct.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        return ownProductService.updateOwnProduct(id, shopId, productId, price, quantity, imageUrl);
    }

    @MutationMapping
    public Boolean deleteOwnProduct(@Argument Long id) {
        return ownProductService.deleteOwnProductById(id);
    }

    @QueryMapping
    public Optional<OwnProduct> ownProductById(@Argument Long id) {
        return ownProductService.getOwnProductById(id);
    }

    @QueryMapping
    public List<OwnProduct> ownProducts() {
        return ownProductService.getAllOwnProducts();
    }
}
