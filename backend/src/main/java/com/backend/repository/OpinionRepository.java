package com.backend.repository;

import com.backend.model.Opinion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpinionRepository extends JpaRepository<Opinion, Long> {
    boolean existsByStoreIdAndUserId(Long store_id, Long user_id);
}
