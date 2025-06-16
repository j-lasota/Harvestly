package com.backend.controller;

import com.backend.model.Store;
import com.backend.model.User;
import com.backend.model.Verification;
import com.backend.service.StoreService;
import com.backend.service.UserService;
import com.backend.service.VerificationService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Controller
public class VerificationController {
    private final VerificationService verificationService;
    private final StoreService storeService;
    private final UserService userService;

    public VerificationController(VerificationService verificationService, StoreService storeService, UserService userService) {
        this.verificationService = verificationService;
        this.storeService = storeService;
        this.userService = userService;
    }

    /**
     * Fetch all verifications.
     *
     * @return a list of all verifications
     */
    @QueryMapping
    public List<Verification> verifications() {
        return verificationService.getAllVerifications();
    }

    /**
     * Fetch verification by its ID.
     *
     * @param id the ID of the verification
     * @return an Optional containing the Verification if found, or empty if not found
     */
    @QueryMapping
    public Optional<Verification> verificationById(@Argument Long id) {
        return verificationService.getVerificationById(id);
    }

    /**
     * Create new verification.
     *
     * @param storeId the ID of the store to associate with the verification
     * @param userId the ID of the user to associate with the verification
     * @return the created Verification object
     * @throws ResponseStatusException if the store or user is not found
     */
    @MutationMapping
    @PreAuthorize("@verificationSecurity.isTheSameUser(authentication, #userId)")
    public Verification createVerification(@Argument Long storeId, @Argument String userId) {
        Optional<Store> shop = storeService.getStoreById(storeId);
        Optional<User> user = userService.getUserById(userId);
        if(shop.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Shop not found");
        }
        if(user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return verificationService.saveVerification(new Verification(shop.get(), user.get()));
    }

    /**
     * Delete verification by its ID.
     *
     * @param id the ID of the verification to delete
     * @return true if the verification was successfully deleted, false otherwise
     */
    @MutationMapping
    @PreAuthorize("@verificationSecurity.isTheSameUserAsInVerification(authentication, #id) or hasAuthority('SCOPE_manage:all')")
    public Boolean deleteVerification(@Argument Long id) {
        return verificationService.deleteVerificationById(id);
    }
}
