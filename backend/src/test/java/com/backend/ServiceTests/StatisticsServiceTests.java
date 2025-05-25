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
        Long storeId = 1L;
        service.recordEvent(EventType.STORE_PAGE, storeId);

        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(repo, times(1)).upsertStorePageClick(eq(storeId), dateCaptor.capture());
        assertEquals(LocalDate.now(), dateCaptor.getValue());

        // ensure we never call the other path
        verify(repo, never()).upsertMapPinClick(anyLong(), any());
    }

    @Test
    void recordMapPinEvent() {
        Long storeId = 2L;
        service.recordEvent(EventType.MAP_PIN, storeId);

        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(repo, times(1)).upsertMapPinClick(eq(storeId), dateCaptor.capture());
        assertEquals(LocalDate.now(), dateCaptor.getValue());

        verify(repo, never()).upsertStorePageClick(anyLong(), any());
    }
}