package com.backend.service;

import com.backend.model.OpinionReport;
import com.backend.repository.OpinionReportRepository;
import com.backend.repository.OpinionRepository;
import com.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OpinionReportService {
    private final OpinionReportRepository opinionReportRepository;
    private final OpinionRepository opinionRepository;

    public OpinionReportService(OpinionReportRepository opinionReportRepository, OpinionRepository opinionRepository, UserRepository userRepository) {
        this.opinionReportRepository = opinionReportRepository;
        this.opinionRepository = opinionRepository;
    }

    public OpinionReport saveOpinionReport(OpinionReport opinionReport) {
        if (opinionReportRepository.existsByOpinionIdAndUserId(opinionReport.getOpinion().getId(), opinionReport.getUser().getId())) {
            throw new IllegalArgumentException("Opinion report already exists for the given opinion and user.");
        }
        opinionReportRepository.save(opinionReport);

        if (opinionReportRepository.countByOpinionId(opinionReport.getOpinion().getId()) >= 5 && !opinionReport.getOpinion().isReported()) {
            opinionReport.getOpinion().setReported(true);
            opinionRepository.save(opinionReport.getOpinion());
        }

        return opinionReport;
    }

    public Optional<OpinionReport> getOpinionReportById(Long id) {
        return opinionReportRepository.findById(id);
    }

    public boolean deleteOpinionReportById(Long id) {
        if (getOpinionReportById(id).isPresent()) {
            opinionReportRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<OpinionReport> getAllOpinionReports() {
        return opinionReportRepository.findAll();
    }
}
