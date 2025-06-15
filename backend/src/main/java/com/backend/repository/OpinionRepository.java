package com.backend.repository;

import com.backend.model.Opinion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OpinionRepository extends JpaRepository<Opinion, Long> {
    Boolean existsByStoreIdAndUserId(Long store_id, String user_id);
    List<Opinion> findByStoreId(Long storeId);
    List<Opinion> findAllByReportedTrue();
    @Query("SELECT AVG(o.stars) FROM Opinion o WHERE o.store.slug = :slug")
    Double findAverageStarsByStoreSlug(@Param("slug") String slug);

    List<Opinion> findByUserId(String userId);
}
