package com.backend.controller;

import com.backend.model.Shop;
import com.backend.model.User;
import com.backend.service.ShopService;
import com.backend.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class ShopController {
    private final ShopService shopService;
    private final UserService userService;

    public ShopController(ShopService shopService, UserService userService) {
        this.shopService = shopService;
        this.userService = userService;
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
    public Shop createShop(@Argument Long userId, @Argument String name, @Argument String description, @Argument double latitude,
                           @Argument double longitude, @Argument String city, @Argument String address,
                           @Argument String imageUrl) {
        Optional<User> user = userService.getUserById(userId);
        if(user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        return shopService.saveShop(new Shop(user.get(), name, description, latitude, longitude, city, address, imageUrl, shopService.generateUniqeSlug(name)));
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
