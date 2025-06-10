package com.backend.service;

import com.backend.model.Opinion;
import com.backend.repository.OpinionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OpinionService {
    private final OpinionRepository opinionRepository;

    public OpinionService(OpinionRepository opinionRepository) {
        this.opinionRepository = opinionRepository;
    }

    public List<Opinion> getAllOpinions() {
        return opinionRepository.findAll();
    }

    public List<Opinion> getAllOpinionsReported() {
        return opinionRepository.findAllByReportedTrue();
    }

    public Optional<Opinion> getOpinionById(Long id) {
        return opinionRepository.findById(id);
    }

    public Opinion saveOpinion(Opinion opinion) {
        if (opinionRepository.existsByStoreIdAndUserId(opinion.getStore().getId(), opinion.getUser().getId())) {
            throw new IllegalArgumentException("Opinion already exists for the given shop and user.");
        }
        if (opinion.getStars() < 0 || opinion.getStars() > 5) {
            throw new IllegalArgumentException("Stars must be between 0 and 5.");
        }
        return opinionRepository.save(opinion);
    }

    public Boolean deleteOpinionById(Long id) {
        if (getOpinionById(id).isPresent()) {
            opinionRepository.deleteById(id);
            return true;
        }
        return false;
    }
    public List<Opinion> getOpinionsByStoreId(Long storeId) {
        return opinionRepository.findByStoreId(storeId);
    }

    public Opinion updateOpinion(Long id, String description, Integer stars, Boolean reported) {
        Opinion opinion = opinionRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("Opinion not found"));
        if(description != null) {
            opinion.setDescription(description);
        }
        if(stars != null) {
            opinion.setStars(stars);
        }
        if(opinion.getStars() < 0 || opinion.getStars() > 5) {
            throw new IllegalArgumentException("Stars must be between 0 and 5.");
        }
        if(reported != null) {
            opinion.setReported(reported);
        }
        return opinionRepository.save(opinion);
    }
}
