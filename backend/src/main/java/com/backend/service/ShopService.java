package com.backend.service;

import com.backend.model.Shop;
import com.backend.repository.ShopRepository;
import com.github.slugify.Slugify;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ShopService {
    private static final Slugify SLUGIFY = Slugify.builder().build();
    private final ShopRepository shopRepository;

    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public Optional<Shop> getShopById(Long id) {
        return shopRepository.findById(id);
    }

    public Shop saveShop(Shop shop) {
        if (shop.getUser().getTier() == 0) {
            if (!shop.getUser().getShops().isEmpty()) {
                throw new IllegalArgumentException("User with tier 0 already has a shop which is not verified.");
            }
        }
        else {
            if (shop.getUser().getShops().size() >= 3) {
                throw new IllegalArgumentException("User can have only 3 shops.");
            }
        }
        return shopRepository.save(shop);
    }

    public Shop updateShop(Long id, String name, String description, Double latitude, Double longitude, String city,
                           String address, String imageUrl) {
        Shop shop = shopRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("Shop not found"));
        if (name != null && !name.isBlank()) {
            shop.setName(name);
            shop.setSlug(generateUniqeSlug(name));
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
    public String generateUniqeSlug(String name) {
        String baseSlug = SLUGIFY.slugify(name);
        int i = 1;
        while (shopRepository.findBySlug(baseSlug + "-" + i) != null) {
            i++;
        }
        return baseSlug + "-" + i;
    }

    public Shop getShopBySlug(String slug) {
        return shopRepository.findBySlug(slug);
    }
}
