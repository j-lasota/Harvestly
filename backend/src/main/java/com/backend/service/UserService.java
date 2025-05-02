package com.backend.service;

import com.backend.model.User;
import com.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    public User updateUser(Long id, String firstName, String lastName, String email, String password, String phoneNumber, Integer tier, String img) {
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

    public Boolean deleteUserById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
