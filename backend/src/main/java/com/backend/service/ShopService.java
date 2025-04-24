package com.backend.service;

import com.backend.model.Shop;
import com.backend.repository.ShopRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ShopService {
    private final ShopRepository shopRepository;

    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public Optional<Shop> getShopById(Long id) {
        return shopRepository.findById(id);
    }
}
