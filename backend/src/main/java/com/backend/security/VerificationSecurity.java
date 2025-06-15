package com.backend.security;

import com.backend.repository.VerificationRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("verificationSecurity")
public class VerificationSecurity {
    private final VerificationRepository verificationRepository;
    public VerificationSecurity(VerificationRepository verificationRepository) {
        this.verificationRepository = verificationRepository;
    }
    public boolean isTheSameUser(Authentication authentication, String userId) {
        return authentication != null && authentication.getName().equals(userId);
    }
}
