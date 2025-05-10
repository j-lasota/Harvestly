package com.backend.repository;

import com.backend.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Store getByName(String name);
    Store findBySlug(String slug);

}
