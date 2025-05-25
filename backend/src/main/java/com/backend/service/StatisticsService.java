package com.backend.service;

import com.backend.model.EventType;
import com.backend.repository.DailyClickCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final DailyClickCountRepository repo;

    @Transactional
    public void recordEvent(EventType type, String slug) {
        LocalDate today = LocalDate.now();
        if (type == EventType.STORE_PAGE) {
            repo.upsertStorePageClick(slug, today);
        } else {
            repo.upsertMapPinClick(slug, today);
        }
    }
}