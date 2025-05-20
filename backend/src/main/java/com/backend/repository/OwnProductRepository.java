package com.backend.repository;

import com.backend.model.OwnProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OwnProductRepository extends JpaRepository<OwnProduct, Long> {
    List<OwnProduct> findByProductId(Long productId);
    List<OwnProduct> findByStoreId(Long storeId);

}
