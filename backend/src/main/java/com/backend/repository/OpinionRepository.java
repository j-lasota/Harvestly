package com.backend.repository;

import com.backend.model.Opinion;
import com.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface OpinionRepository extends JpaRepository<Opinion, Long> {
    Boolean existsByStoreIdAndUserId(Long store_id, String user_id);
    List<Opinion> findByStoreId(Long storeId);
    List<Opinion> findAllByReportedTrue();
    @Query("SELECT AVG(o.stars) FROM Opinion o WHERE o.store.slug = :slug")
    Double findAverageStarsByStoreSlug(@Param("slug") String slug);

    List<Opinion> findByUserId(String userId);

    /**
     * @param users List of user objects
     * @return List of matching opinions
     */
    List<Opinion> findByUserIn(List<User> users);
}
