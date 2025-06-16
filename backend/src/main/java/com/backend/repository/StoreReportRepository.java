package com.backend.repository;

import com.backend.model.StoreReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreReportRepository extends JpaRepository<StoreReport, Long> {
    long countByStoreId(Long storeId);
    boolean existsByStoreIdAndUserId(Long storeId, String userId);
}
