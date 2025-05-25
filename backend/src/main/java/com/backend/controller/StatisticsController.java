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

    /**
     * Example:
     *   POST /api/stats/event?slug=my-store&type=STORE_PAGE
     */
    @PostMapping("/event")
    public ResponseEntity<Void> recordEvent(
            @RequestParam("slug") String slug,
            @RequestParam("type") EventType type) {

        statisticsService.recordEvent(type, slug);
        return ResponseEntity.ok().build();
    }
}