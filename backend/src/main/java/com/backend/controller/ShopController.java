package com.backend.controller;

import com.backend.model.Shop;
import com.backend.service.ShopService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class ShopController {
    private final ShopService shopService;
    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @QueryMapping
    public List<Shop> shops() {
        return shopService.getAllShops();
    }

    @QueryMapping
    public Optional<Shop> shopById(@Argument Long id) {
        return shopService.getShopById(id);
    }

    @MutationMapping
    public Shop createShop(@Argument String name, @Argument String description, @Argument double latitude,
                           @Argument double longitude, @Argument String city, @Argument String address,
                           @Argument String imageUrl) {
        return shopService.saveShop(new Shop(name, description, latitude, longitude, city, address, imageUrl));
    }

    @MutationMapping
    public Shop updateShop(@Argument Long id, @Argument String name, @Argument String description,
                           @Argument Double latitude, @Argument Double longitude, @Argument String city,
                           @Argument String address, @Argument String imageUrl) {
        return shopService.updateShop(id, name, description, latitude, longitude, city, address, imageUrl);
    }

    @MutationMapping
    public Boolean deleteShop(@Argument Long id) {
        return shopService.deleteShopById(id);
    }
}
