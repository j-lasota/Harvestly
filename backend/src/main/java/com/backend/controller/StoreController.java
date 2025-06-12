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

    /** Fetch all stores
     *
     * @return List of all stores
     */
    @QueryMapping
    public List<Store> stores() {
        return storeService.getAllStores();
    }

    /** Fetch a store by its ID
     *
     * @param id ID of the store
     * @return Optional containing the store if found, otherwise empty
     */

    @QueryMapping
    public Optional<Store> storeById(@Argument Long id) {
        return storeService.getStoreById(id);
    }

    /** Create a new store
     *
     * @param userId ID of the user creating the store
     * @param name Name of the store
     * @param description Description of the store
     * @param latitude Latitude of the store's location
     * @param longitude Longitude of the store's location
     * @param city City where the store is located
     * @param address Address of the store
     * @param imageUrl URL of the store's image
     * @return The created store
     */

    @MutationMapping
    public Store createStore(@Argument String userId, @Argument String name, @Argument String description, @Argument double latitude,
                            @Argument double longitude, @Argument String city, @Argument String address,
                            @Argument String imageUrl) {
        Optional<User> user = userService.getUserById(userId);
        if(user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        return storeService.saveStore(new Store(user.get(), name, description, latitude, longitude, city, address, imageUrl, storeService.generateUniqueSlug(name)));
    }

    /** Update an existing store
     *
     * @param id ID of the store to update
     * @param name New name of the store
     * @param description New description of the store
     * @param latitude New latitude of the store's location
     * @param longitude New longitude of the store's location
     * @param city New city where the store is located
     * @param address New address of the store
     * @param imageUrl New URL of the store's image
     * @return The updated store
     */

    @MutationMapping
    public Store updateStore(@Argument Long id, @Argument String name, @Argument String description,
                            @Argument Double latitude, @Argument Double longitude, @Argument String city,
                            @Argument String address, @Argument String imageUrl) {
        return  storeService.updateStore(id, name, description, latitude, longitude, city, address, imageUrl);
    }

    /** Delete a store by its ID
     *
     * @param id ID of the store to delete
     * @return True if the store was deleted successfully, otherwise false
     */
    @MutationMapping
    public Boolean deleteStore(@Argument Long id) {
        return storeService.deleteStoreById(id);
    }

    /** Fetch a store by its slug
     *
     * @param slug Slug of the store
     * @return The store with the given slug
     */

    @QueryMapping
    public Store storeBySlug(@Argument String slug) {
        return storeService.getStoreBySlug(slug);
    }
}
