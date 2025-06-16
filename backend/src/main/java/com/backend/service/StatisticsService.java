package com.backend.service;

import com.backend.model.EventType;
import com.backend.repository.DailyClickCountRepository;
import com.backend.repository.OpinionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final DailyClickCountRepository clickRepo;
    private final OpinionRepository opinionRepo;

    @Transactional
    public void recordEvent(EventType type, String slug) {
        LocalDate today = LocalDate.now();
        if (type == EventType.STORE_PAGE) {
            clickRepo.upsertStorePageClick(slug, today);
        } else {
            clickRepo.upsertMapPinClick(slug, today);
        }
    }

    public double getClickRatio(String slug) {
        long pin = clickRepo.totalMapPinClicks(slug);
        long page = clickRepo.totalStorePageClicks(slug);
        if (page == 0) {
            return 0.0;
        }
        return (double) pin / page;
    }

    public double getClickRatio(String slug, int days) {
        LocalDate from = LocalDate.now().minusDays(days);
        long pin = clickRepo.mapPinClicksSince(slug, from);
        long page = clickRepo.storePageClicksSince(slug, from);
        if (page == 0) {
            return 0.0;
        }
        return (double) pin / page;
    }
    public double getAverageRating(String slug) {
        Double avg = opinionRepo.findAverageStarsByStoreSlug(slug);
        return (avg != null ? avg : 0.0);
    }


}