package com.backend.controller;

import com.backend.model.User;
import com.backend.service.StoreService;
import com.backend.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class UserController {
    private final UserService userService;
    private final StoreService storeService;

    public UserController(UserService userService, StoreService storeService) {
        this.userService = userService;
        this.storeService = storeService;
    }

    @QueryMapping
    public Optional<User> userById(@Argument String id) {
        return userService.getUserById(id);
    }

    @QueryMapping
    public List<User> users() {
        return userService.getAllUsers();
    }

    @MutationMapping
    public User createUser(@Argument String id, @Argument String firstName, @Argument String lastName, @Argument String email,
                           @Argument String phoneNumber, @Argument String img) {
        return userService.saveUser(new User(id, firstName, lastName, email, phoneNumber, 0,img));
    }

    @MutationMapping
    public User updateUser(@Argument String id, @Argument String firstName, @Argument String lastName,
                           @Argument String email, @Argument String phoneNumber,
                           @Argument Integer tier, @Argument String img, @Argument String facebook_nickname) {
        return userService.updateUser(id, firstName, lastName, email, phoneNumber, tier, img, facebook_nickname);
    }

    @MutationMapping
    public Boolean deleteUser(@Argument String id) {
        return userService.deleteUserById(id);
    }

    @MutationMapping
    public User addFavoriteStore(@Argument String userId, @Argument Long storeId) {
        return userService.addFavoriteShop(userId, storeId);
    }

    @MutationMapping
    public User removeFavoriteStore(@Argument String userId, @Argument Long storeId) {
        return userService.removeFavoriteShop(userId, storeId);
    }

    @QueryMapping
    public Optional<User> userByEmail(@Argument String email) {
        return userService.getUserByEmail(email);
    }
}
