package com.backend.service;

import com.backend.model.StoreReport;
import com.backend.repository.StoreReportRepository;
import com.backend.repository.StoreRepository;
import com.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StoreReportService {
    private final StoreReportRepository storeReportRepository;
    private final StoreRepository storeRepository;

    public StoreReportService(StoreReportRepository storeReportRepository, StoreRepository storeRepository, UserRepository userRepository) {
        this.storeReportRepository = storeReportRepository;
        this.storeRepository = storeRepository;
    }

    public StoreReport saveStoreReport(StoreReport storeReport) {
        if (storeReportRepository.existsByStoreIdAndUserId(storeReport.getStore().getId(), storeReport.getUser().getId())) {
            throw new IllegalArgumentException("Store report already exists for the given store and user.");
        }
        storeReportRepository.save(storeReport);

        if (storeReportRepository.countByStoreId(storeReport.getStore().getId()) >= 5 && !storeReport.getStore().isReported()) {
            storeReport.getStore().setReported(true);
            storeRepository.save(storeReport.getStore());
        }

        return storeReport;
    }

    public Optional<StoreReport> getStoreReportById(Long id) {
        return storeReportRepository.findById(id);
    }

    public boolean deleteStoreReportById(Long id) {
        if (getStoreReportById(id).isPresent()) {
            storeReportRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<StoreReport> getAllStoreReports() {
        return storeReportRepository.findAll();
    }
}
