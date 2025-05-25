package com.backend.ServiceTests;

import com.backend.model.EventType;
import com.backend.repository.DailyClickCountRepository;
import com.backend.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatisticsServiceTests {

    private DailyClickCountRepository repo;
    private StatisticsService service;

    @BeforeEach
    void setUp() {
        repo = mock(DailyClickCountRepository.class);
        service = new StatisticsService(repo);
    }

    @Test
    void recordStorePageEvent() {
        String slug = "shop-one";
        service.recordEvent(EventType.STORE_PAGE, slug);

        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(repo, times(1)).upsertStorePageClick(eq(slug), dateCaptor.capture());
        assertEquals(LocalDate.now(), dateCaptor.getValue());

        verify(repo, never()).upsertMapPinClick(anyString(), any());
    }

    @Test
    void recordMapPinEvent() {
        String slug = "pin-shop";
        service.recordEvent(EventType.MAP_PIN, slug);

        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(repo, times(1)).upsertMapPinClick(eq(slug), dateCaptor.capture());
        assertEquals(LocalDate.now(), dateCaptor.getValue());

        verify(repo, never()).upsertStorePageClick(anyString(), any());
    }
}