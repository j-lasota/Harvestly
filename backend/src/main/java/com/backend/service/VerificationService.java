package com.backend.service;

import com.backend.model.Verification;
import com.backend.repository.StoreRepository;
import com.backend.repository.UserRepository;
import com.backend.repository.VerificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VerificationService {
    private final VerificationRepository verificationRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    public VerificationService(VerificationRepository verificationRepository, StoreRepository storeRepository, UserRepository userRepository) {
        this.verificationRepository = verificationRepository;
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
    }

    public Verification saveVerification(Verification verification) {
        if (verificationRepository.existsByStoreIdAndUserId(verification.getStore().getId(), verification.getUser().getId())) {
            throw new IllegalArgumentException("Verification already exists for the given shop and user.");
        }
        verificationRepository.save(verification);

        if (verificationRepository.countByStoreId(verification.getStore().getId()) >= 5 && !verification.getStore().isVerified()) {
            verification.getStore().setVerified(true);
            verification.getStore().getUser().setTier(1);
            storeRepository.save(verification.getStore());
            userRepository.save(verification.getStore().getUser());
        }

        return verification;
    }

    public Optional<Verification> getVerificationById(Long id) {
        return verificationRepository.findById(id);
    }

    public List<Verification> getAllVerifications() {
        return verificationRepository.findAll();
    }

    public Boolean deleteVerificationById(Long id) {
        if (getVerificationById(id).isPresent()) {
            verificationRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
