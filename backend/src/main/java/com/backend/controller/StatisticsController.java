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
     * Example endpoint to record an event.
     * POST /api/stats/event?storeId=123&type=STORE_PAGE
     */
    @PostMapping("/event")
    public ResponseEntity<Void> recordEvent(
            @RequestParam("storeId") Long storeId,
            @RequestParam("type") EventType type) {
        statisticsService.recordEvent(type, storeId);
        return ResponseEntity.ok().build();
    }
}