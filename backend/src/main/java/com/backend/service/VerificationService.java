package com.backend.service;

import com.backend.model.Shop;
import com.backend.model.Verification;
import com.backend.repository.ShopRepository;
import com.backend.repository.VerificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VerificationService {
    private final VerificationRepository verificationRepository;
    private final ShopRepository shopRepository;

    public VerificationService(VerificationRepository verificationRepository, ShopRepository shopRepository) {
        this.verificationRepository = verificationRepository;
        this.shopRepository = shopRepository;
    }

    public Verification saveVerification(Verification verification) {
        if (verificationRepository.existsByShopIdAndUserId(verification.getShop().getId(), verification.getUser().getId())) {
            throw new IllegalArgumentException("Verification already exists for the given shop and user.");
        }
        verificationRepository.save(verification);

        if (verificationRepository.countByShopId(verification.getShop().getId()) >= 5 && !verification.getShop().isVerified()) {
            verification.getShop().setVerified(true);
            shopRepository.save(verification.getShop());
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
