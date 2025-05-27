package com.backend.service;

import com.backend.model.Store;
import com.backend.model.User;
import com.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final StoreService storeService;

    public UserService(UserRepository userRepository, StoreService storeService) {
        this.userRepository = userRepository;
        this.storeService = storeService;
    }

    public User saveUser(User user) {
        if(userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with the same email already exists.");
        }
        if(userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new IllegalArgumentException("User with the same phone number already exists.");
        }
        return userRepository.save(user);
    }

    public User updateUser(String id, String firstName, String lastName, String email, String phoneNumber, Integer tier, String img) {
        User user = userRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (firstName != null) {
            user.setFirstName(firstName);
        }
        if (lastName != null) {
            user.setLastName(lastName);
        }
        if (email != null) {
            user.setEmail(email);
        }
        if (phoneNumber != null) {
            user.setPhoneNumber(phoneNumber);
        }
        if (tier != null) {
            user.setTier(tier);
        }
        if (img != null) {
            user.setImg(img);
        }
        return userRepository.save(user);
    }

    public Boolean deleteUserById(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User addFavoriteShop(String userId, Long shopId) {
        Optional<User> user = getUserById(userId);
        Optional<Store> shop = storeService.getStoreById(shopId);
        if(user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        if(shop.isEmpty()) {
            throw new IllegalArgumentException("Shop not found");
        }
        user.get().getFavoriteStores().add(shop.get());
        return userRepository.save(user.get());
    }

    public User removeFavoriteShop(String userId, Long shopId) {
        Optional<User> user = getUserById(userId);
        Optional<Store> shop = storeService.getStoreById(shopId);
        if(user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        if(shop.isEmpty()) {
            throw new IllegalArgumentException("Shop not found");
        }
        user.get().getFavoriteStores().remove(shop.get());
        return userRepository.save(user.get());
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
