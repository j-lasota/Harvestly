package com.backend.repository;


import com.backend.model.OpinionReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpinionReportRepository extends JpaRepository<OpinionReport, Long> {
    long countByOpinionId(Long opinionId);
    boolean existsByOpinionIdAndUserId(Long opinionId, String userId);
    void deleteByOpinionId(Long opinionId);
}
