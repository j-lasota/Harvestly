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

    @QueryMapping
    public List<Verification> verifications() {
        return verificationService.getAllVerifications();
    }

    @QueryMapping
    public Optional<Verification> verificationById(@Argument Long id) {
        return verificationService.getVerificationById(id);
    }

    @MutationMapping
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

    @MutationMapping
    public Boolean deleteVerification(@Argument Long id) {
        return verificationService.deleteVerificationById(id);
    }
}
