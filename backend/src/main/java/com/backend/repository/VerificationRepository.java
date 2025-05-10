package com.backend.repository;

import com.backend.model.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationRepository extends JpaRepository<Verification, Long> {
    long countByStoreId(Long shopId);
    boolean existsByStoreIdAndUserId(Long store_id, Long user_id);
}
