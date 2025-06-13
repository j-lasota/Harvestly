package com.backend.controller;

import com.backend.model.EventType;
import com.backend.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @PostMapping("/event")
    public ResponseEntity<Void> recordEvent(
            @RequestParam String slug,
            @RequestParam EventType type) {

        statisticsService.recordEvent(type, slug);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /api/stats/ratio?slug=store-xyz
     */
    @GetMapping("/ratio")
    public ResponseEntity<Double> getOverallRatio(@RequestParam String slug) {
        double ratio = statisticsService.getClickRatio(slug);
        return ResponseEntity.ok(ratio);
    }

    /**
     * GET /api/stats/ratio/period?slug=store-xyz&days=7
     */
    @GetMapping("/ratio/period")
    public ResponseEntity<Double> getRatioForPeriod(
            @RequestParam String slug,
            @RequestParam int days) {

        double ratio = statisticsService.getClickRatio(slug, days);
        return ResponseEntity.ok(ratio);
    }
    @GetMapping("/average-rating")
    public ResponseEntity<Double> getAverageRating(@RequestParam String slug) {
        double avg = statisticsService.getAverageRating(slug);
        return ResponseEntity.ok(avg);
    }


}