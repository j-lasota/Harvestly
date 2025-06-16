package com.backend.security;

import com.backend.model.OwnProduct;
import com.backend.repository.OwnProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("ownProductSecurity")
@RequiredArgsConstructor
public class OwnProductSecurity {

    private final OwnProductRepository ownProductRepository;
    private final StoreSecurity storeSecurity;

    public boolean isOwner(Long ownProductId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // 1. Znajdź OwnProduct na podstawie jego ID
        Optional<OwnProduct> ownProductOpt = ownProductRepository.findById(ownProductId);
        if (ownProductOpt.isEmpty()) {
            return true;
        }

        Long storeId = ownProductOpt.get().getStore().getId();

        return storeSecurity.isOwner(authentication, storeId);
    }
}