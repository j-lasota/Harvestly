package com.backend.security;

import com.backend.model.Store;
import com.backend.repository.BusinessHoursRepository;
import com.backend.repository.StoreRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("businessHoursSecurity")
public class BusinessHoursSecurity {
    private final StoreRepository storeRepository;
    private final BusinessHoursRepository businessHoursRepository;

    public BusinessHoursSecurity(StoreRepository StoreRepository, BusinessHoursRepository businessHoursRepository) {
        this.storeRepository = StoreRepository;
        this.businessHoursRepository = businessHoursRepository;
    }

    public boolean isStoreOwner(Authentication authentication, Long storeId) {
        return storeRepository.findById(storeId).map(s -> {
            if (s.getUser().getId() == null) {
                return false;
            }
            return s.getUser().getId().equals(authentication.getName());
        }).orElse(false);
    }
    public boolean isStoreOwnerByBHId(Authentication authentication, Long BHId) {
        return businessHoursRepository.findById(BHId).map(bh -> {
            Store store = bh.getStore();
            if (store.getUser().getId() == null) {
                return false;
            }
            return store.getUser().getId().equals(authentication.getName());
        }).orElse(true);
    }
}
