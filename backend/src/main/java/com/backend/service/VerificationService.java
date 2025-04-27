package com.backend.service;

import com.backend.model.Verification;
import com.backend.repository.VerificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VerificationService {
    private final VerificationRepository verificationRepository;

    public VerificationService(VerificationRepository verificationRepository) {
        this.verificationRepository = verificationRepository;
    }

    public Verification saveVerification(Verification verification) {
        return verificationRepository.save(verification);
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
