package com.backend.controller;

import com.backend.model.User;
import com.backend.service.StoreService;
import com.backend.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Fetch a user by its ID.
     *
     * @param id the ID of the user to fetch
     * @return an Optional containing the user if found, or empty if not found
     */
    @QueryMapping
    public Optional<User> userById(@Argument String id) {
        return userService.getUserById(id);
    }

    /**
     * Fetch all users.
     *
     * @return a list of all users.
     */
    @QueryMapping
    public List<User> users() {
        return userService.getAllUsers();
    }

    /**
     * Create a new user.
     *
     * @param id the ID of the user to create
     * @param firstName the first name of the user
     * @param lastName the last name of the user
     * @param email the email of the user
     * @param phoneNumber the phone number of the user
     * @param img the image URL of the user
     * @return the created user
     */
    @MutationMapping
    public User createUser(@Argument String id, @Argument String firstName, @Argument String lastName, @Argument String email,
                           @Argument String phoneNumber, @Argument String img) {
        return userService.saveUser(new User(id, firstName, lastName, email, phoneNumber, 0,img));
    }

    /**
     * Update an existing user.
     *
     * @param id the ID of the user to update
     * @param firstName the new first name of the user
     * @param lastName the new last name of the user
     * @param email the new email of the user
     * @param phoneNumber the new phone number of the user
     * @param tier the new tier of the user
     * @param img the new image URL of the user
     * @param facebook_nickname the new Facebook nickname of the user
     * @return the updated user
     */
    @PreAuthorize("#id == authentication.name or hasAuthority('SCOPE_manage:all')")
    @MutationMapping
    public User updateUser(@Argument String id, @Argument String firstName, @Argument String lastName,
                           @Argument String email, @Argument String phoneNumber,
                           @Argument Integer tier, @Argument String img, @Argument String facebook_nickname) {
        return userService.updateUser(id, firstName, lastName, email, phoneNumber, tier, img, facebook_nickname);
    }

    /**
     * Delete a user by its ID.
     *
     * @param id the ID of the user to delete
     * @return true if the user was deleted, false otherwise
     */
    @PreAuthorize("hasAuthority('SCOPE_manage:all')")
    @MutationMapping
    public Boolean deleteUser(@Argument String id) {
        return userService.deleteUserById(id);
    }

    /**
     * Add a favorite store to a user.
     *
     * @param userId the ID of the user
     * @param storeId the ID of the store to add as a favorite
     * @return the updated user with the favorite store added
     */
    @PreAuthorize("#userId == authentication.name")
    @MutationMapping
    public User addFavoriteStore(@Argument String userId, @Argument Long storeId) {
        return userService.addFavoriteShop(userId, storeId);
    }

    /**
     * Remove a favorite store from a user.
     *
     * @param userId the ID of the user
     * @param storeId the ID of the store to remove from favorites
     * @return the updated user with the favorite store removed
     */
    @PreAuthorize("#userId == authentication.name")
    @MutationMapping
    public User removeFavoriteStore(@Argument String userId, @Argument Long storeId) {
        return userService.removeFavoriteShop(userId, storeId);
    }

    /**
     * Fetch a user by their email.
     *
     * @param email the email of the user to fetch
     * @return an Optional containing the user if found, or empty if not found
     */
    @QueryMapping
    public Optional<User> userByEmail(@Argument String email) {
        return userService.getUserByEmail(email);
    }
}
