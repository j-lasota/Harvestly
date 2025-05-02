package com.backend.repository;

import com.backend.model.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VerificationRepository extends JpaRepository<Verification, Long> {
    long countByShopId(Long shopId);
    boolean existsByShopIdAndUserId(Long shop_id, Long user_id);
}
