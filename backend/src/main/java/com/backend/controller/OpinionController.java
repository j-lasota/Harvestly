package com.backend.controller;

import com.backend.model.Opinion;
import com.backend.model.Store;
import com.backend.model.User;
import com.backend.service.OpinionService;
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
public class OpinionController {
    private final OpinionService opinionService;
    private final StoreService storeService;
    private final UserService userService;

    public OpinionController(OpinionService opinionService, StoreService storeService, UserService userService) {
        this.opinionService = opinionService;
        this.storeService = storeService;
        this.userService = userService;
    }

    /**
     * Fetch all opinions.
     *
     * @return List of all opinions.
     */
    @QueryMapping
    public List<Opinion> opinions() {
        return opinionService.getAllOpinions();
    }

    /**
     * Fetch an opinion by its ID.
     *
     * @param id The ID of the opinion.
     * @return An Optional containing the opinion if found, or empty if not found.
     */
    @QueryMapping
    public Optional<Opinion> opinionById(@Argument Long id) {
        return opinionService.getOpinionById(id);
    }

    /**
     * Create a new opinion for a store by a user.
     *
     * @param storeId The ID of the store.
     * @param userId The ID of the user.
     * @param description The description of the opinion.
     * @param stars The rating in stars.
     * @return The created Opinion object.
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Opinion createOpinion(@Argument Long storeId, @Argument String userId, @Argument String description, @Argument Integer stars) {
        Optional<Store> shop = storeService.getStoreById(storeId);
        Optional<User> user = userService.getUserById(userId);
        if(shop.isEmpty()) {
            throw new IllegalArgumentException("Shop not found");
        }
        if(user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        return opinionService.saveOpinion(new Opinion(shop.get(), user.get(), description, stars));
    }

    /**
     * Fetch all opinions for a specific store by its ID.
     *
     * @param storeId The ID of the store.
     * @return List of opinions for the specified store.
     */
    @QueryMapping
    public List<Opinion> opinionsByStoreId(@Argument Long storeId) {
        Optional<Store> store = storeService.getStoreById(storeId);
        if (store.isEmpty()) {
            throw new IllegalArgumentException("Store not found");
        }

        return opinionService.getOpinionsByStoreId(storeId);
    }

    /**
     * Fetch all opinions that have been reported.
     *
     * @return List of reported opinions.
     */
    @PreAuthorize("hasAuthority('SCOPE_manage:all')")
    @QueryMapping
    public List<Opinion> opinionsReported() {
        return opinionService.getAllOpinionsReported();
    }

    /**
     * Delete an opinion by its ID.
     *
     * @param id The ID of the opinion to delete.
     * @return Boolean indicating whether the deletion was successful.
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated() and @opinionSecurity.isAuthor(authentication, #id) or hasAuthority('SCOPE_read:admin-dashboard')")
    public Boolean deleteOpinion(@Argument Long id) {
        return opinionService.deleteOpinionById(id);
    }

    /**
     * Update an existing opinion.
     *
     * @param id The ID of the opinion to update.
     * @param description The new description of the opinion.
     * @param stars The new rating in stars.
     * @param reported Whether the opinion is reported or not.
     * @return The updated Opinion object.
     */
    @MutationMapping
    @PreAuthorize("hasAuthority('SCOPE_read:admin-dashboard')")
    public Opinion updateOpinion(@Argument Long id, @Argument String description, @Argument Integer stars, @Argument Boolean reported) {
        return opinionService.updateOpinion(id, description, stars, reported);
    }

    /**
     * Update an existing opinion.
     *
     * @param id The ID of the opinion to update.
     * @param description The new description of the opinion.
     * @param stars The new rating in stars.
     * @return The updated Opinion object.
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated() and @opinionSecurity.isAuthor(authentication, #id)")
    public Opinion updateOpinionByOwner(@Argument Long id, @Argument String description, @Argument Integer stars) {
        return opinionService.updateOpinion(id, description, stars, null);
    }
}
