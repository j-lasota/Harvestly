package com.backend.service;

import com.backend.model.Shop;
import com.backend.repository.ShopRepository;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public Shop saveShop(Shop shop) {
        return shopRepository.save(shop);
    }

    public Shop updateShop(Long id, String name, String description, Double latitude, Double longitude, String city,
                           String address, String imageUrl) {
        Shop shop = shopRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("Shop not found"));
        if (name != null && !name.isBlank()) {
            shop.setName(name);
        }
        if (description != null && !description.isBlank()) {
            shop.setDescription(description);
        }
        if (latitude != null && latitude != 0) {
            shop.setLatitude(latitude);
        }
        if (longitude != null && longitude != 0) {
            shop.setLongitude(longitude);
        }
        if (city != null && !city.isBlank()) {
            shop.setCity(city);
        }
        if (address != null && !address.isBlank()) {
            shop.setAddress(address);
        }
        if (imageUrl != null && !imageUrl.isBlank()) {
            shop.setImageUrl(imageUrl);
        }
        return shopRepository.save(shop);
    }

    public Boolean deleteShopById(Long id) {
        if (getShopById(id).isPresent()) {
            shopRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }
}
