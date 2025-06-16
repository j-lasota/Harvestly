package com.backend.repository;

import com.backend.model.Store;
import com.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Store getByName(String name);
    Store findBySlug(String slug);
    List<Store> findAllByReportedTrue();

    List<Store> findByUserId(String userId);
    /**
     * Finds all stores of users from list
     *
     * @param users List of user objects
     * @return List of matching stores.
     */
    List<Store> findByUserIn(List<User> users);
}
