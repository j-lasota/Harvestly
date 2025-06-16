package com.backend.security;

import com.backend.repository.StoreRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("storeSecurity")
public class StoreSecurity {
    private final StoreRepository stores;

    public StoreSecurity(StoreRepository stores) {
        this.stores = stores;
    }

    public boolean isOwner(Authentication authentication, Long storeId) {
        return stores.findById(storeId)
                .map(s -> s.getUser().getId().equals(authentication.getName()))
                .orElse(true);
    }
}
