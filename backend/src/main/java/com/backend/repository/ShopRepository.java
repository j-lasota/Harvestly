package com.backend.repository;

import com.backend.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    Shop getByName(String name);
    Shop findBySlug(String slug);

}
