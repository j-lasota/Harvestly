package com.backend.controller;

import com.backend.model.Shop;
import com.backend.model.Verification;
import com.backend.service.ShopService;
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
    private final ShopService shopService;
//    private final UserService userService;

    public VerificationController(VerificationService verificationService, ShopService shopService) {
        this.verificationService = verificationService;
        this.shopService = shopService;
    }

    @QueryMapping
    public List<Verification> verifications() {
        return verificationService.getAllVerifications();
    }

    @QueryMapping
    public Optional<Verification> verificationById(Long id) {
        return verificationService.getVerificationById(id);
    }
//TODO: Brakuje UserService

//    @MutationMapping
//    public Verification createVerification(@Argument Long shopId, @Argument Long userId) {
//        Optional<Shop> shop = shopService.getShopById(shopId);
//        if(shop.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Shop not found");
//        }
//
//
//    }

    @MutationMapping
    public Boolean deleteVerification(@Argument Long id) {
        return verificationService.deleteVerificationById(id);
    }
}
