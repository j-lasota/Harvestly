package com.backend.service;

import com.backend.model.Store;
import com.backend.model.User;
import com.backend.repository.StoreReportRepository;
import com.backend.repository.StoreRepository;
import com.github.slugify.Slugify;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class StoreService {
    private static final Slugify SLUGIFY = Slugify.builder().build();
    private final StoreRepository storeRepository;
    private final StoreReportRepository storeReportRepository;

    public StoreService(StoreRepository storeRepository, StoreReportRepository storeReportRepository) {
        this.storeRepository = storeRepository;
        this.storeReportRepository = storeReportRepository;
    }

    public Optional<Store> getStoreById(Long id) {
        return storeRepository.findById(id);
    }

    public Store saveStore(Store store) {
        if (store.getUser().getTier() == 0) {
            if (!store.getUser().getStores().isEmpty()) {
                throw new IllegalArgumentException("User with tier 0 already has a store which is not verified.");
            }
        }
        else {
            if (store.getUser().getStores().size() >= 3) {
                throw new IllegalArgumentException("User can have only 3 stores.");
            }
        }
        return storeRepository.save(store);
    }

    public Store updateStore(Long id, String name, String description, Double latitude, Double longitude, String city,
                             String address, String imageUrl, Boolean reported) {
        Store store = storeRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("Store not found"));
        if (name != null && !name.isBlank()) {
            store.setName(name);
            store.setSlug(generateUniqueSlug(name));
        }
        if (description != null && !description.isBlank()) {
            store.setDescription(description);
        }
        if (latitude != null && latitude != 0) {
            store.setLatitude(latitude);
        }
        if (longitude != null && longitude != 0) {
            store.setLongitude(longitude);
        }
        if (city != null && !city.isBlank()) {
            store.setCity(city);
        }
        if (address != null && !address.isBlank()) {
            store.setAddress(address);
        }
        if (imageUrl != null && !imageUrl.isBlank()) {
            store.setImageUrl(imageUrl);
        }
        if (reported != null) {
            store.setReported(reported);
            if (!store.isReported()) {
                storeReportRepository.deleteByStoreId(store.getId());
            }
        }

        return storeRepository.save(store);
    }

    public Boolean deleteStoreById(Long id) {
        if (getStoreById(id).isPresent()) {
            storeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }
    public String generateUniqueSlug(String name) {
        String baseSlug = SLUGIFY.slugify(name);
        if (storeRepository.findBySlug(baseSlug) == null) {
            return baseSlug;
        }
        int i = 1;
        while (storeRepository.findBySlug(baseSlug + "-" + i) != null) {
            i++;
        }
        return baseSlug + "-" + i;
    }

    public Store getStoreBySlug(String slug) {
        return storeRepository.findBySlug(slug);
    }

    public List<Store> getAllReportedStores() {
        return storeRepository.findAllByReportedTrue();
    }

    public List<Store> getStoresByUserId(String userId) {
        return storeRepository.findByUserId(userId);
    }

    public List<Store> getStoresByUserIn(List<User> users) {
        return storeRepository.findByUserIn(users);
    }

    public List<Store> saveAllStores(List<Store> stores) {
        for (Store store : stores) {
            if (store.getUser().getTier() == 0) {
                if (!store.getUser().getStores().isEmpty()) {
                    throw new IllegalArgumentException("User with tier 0 already has a store which is not verified.");
                }
            } else {
                if (store.getUser().getStores().size() >= 3) {
                    throw new IllegalArgumentException("User can have only 3 stores.");
                }
            }
            store.setSlug(generateUniqueSlug(store.getName()));
        }
        return storeRepository.saveAll(stores);
    }
}
