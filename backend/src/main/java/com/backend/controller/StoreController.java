package com.backend.controller;

import com.backend.model.Store;
import com.backend.model.User;
import com.backend.service.StoreService;
import com.backend.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
// TODO: REMOVE TRANSACTIONAL
@Controller
public class StoreController {
    private final StoreService storeService;
    private final UserService userService;

    public StoreController(StoreService storeService, UserService userService) {
        this.storeService = storeService;
        this.userService = userService;
    }
    @Transactional
    @QueryMapping
    public List<Store> stores() {
        return storeService.getAllStores();
    }

    @Transactional
    @QueryMapping
    public Optional<Store> storeById(@Argument Long id) {
        return storeService.getStoreById(id);
    }

    @Transactional
    @MutationMapping
    public Store createStore(@Argument Long userId, @Argument String name, @Argument String description, @Argument double latitude,
                            @Argument double longitude, @Argument String city, @Argument String address,
                            @Argument String imageUrl) {
        Optional<User> user = userService.getUserById(userId);
        if(user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        return storeService.saveStore(new Store(user.get(), name, description, latitude, longitude, city, address, imageUrl, storeService.generateUniqueSlug(name)));
    }

    @Transactional
    @MutationMapping
    public Store updateStore(@Argument Long id, @Argument String name, @Argument String description,
                            @Argument Double latitude, @Argument Double longitude, @Argument String city,
                            @Argument String address, @Argument String imageUrl) {
        return  storeService.updateStore(id, name, description, latitude, longitude, city, address, imageUrl);
    }
    @Transactional
    @MutationMapping
    public Boolean deleteStore(@Argument Long id) {
        return storeService.deleteStoreById(id);
    }
    @Transactional
    @QueryMapping
    public Store storeBySlug(@Argument String slug) {
        return storeService.getStoreBySlug(slug);
    }
}
