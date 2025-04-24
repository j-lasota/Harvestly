package com.backend.service;

import com.backend.model.OwnProduct;
import com.backend.repository.OwnProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ownProductService {
    private final OwnProductRepository ownProductRepository;

    public ownProductService(OwnProductRepository ownProductRepository) {
        this.ownProductRepository = ownProductRepository;
    }

    public List<OwnProduct> getByProduct(Long id) {
        return ownProductRepository.findByProductId(id);
    }

    public Optional<OwnProduct> getOwnProductById(Long id) {
        return ownProductRepository.findById(id);
    }

    public OwnProduct save(OwnProduct ownProduct) {
        return ownProductRepository.save(ownProduct);
    }
}
