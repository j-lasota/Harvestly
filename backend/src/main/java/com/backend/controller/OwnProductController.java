package com.backend.controller;

import com.backend.model.OwnProduct;
import com.backend.model.Product;
import com.backend.model.Shop;
import com.backend.service.ProductService;
import com.backend.service.ShopService;
import com.backend.service.ownProductService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
public class OwnProductController {
    private final ownProductService ownProductService;
    private final ShopService shopService;
    private final ProductService productService;
    
    public OwnProductController(ownProductService ownProductService, ShopService shopService, ProductService productService) {
        this.ownProductService = ownProductService;
        this.shopService = shopService;
        this.productService = productService;
    }

//    @SchemaMapping(typeName="Product", field="ownProducts")
//    public List<OwnProduct> getOwnProducts(Long id) {
//        return ownProductService.getByProduct(id);
//    }

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

    @QueryMapping
    public Optional<OwnProduct> ownProductById(Long id) {
        return ownProductService.getOwnProductById(id);
    }

    @QueryMapping
    public List<OwnProduct> ownProducts() {
        return ownProductService.getAllOwnProducts();
    }
}
