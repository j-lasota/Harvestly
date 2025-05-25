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

    @Test
    void testGetClickRatio_pageZeroReturnsZero() {
        String slug = "any-shop";
        when(repo.totalMapPinClicks(slug)).thenReturn(5L);
        when(repo.totalStorePageClicks(slug)).thenReturn(0L);

        double ratio = service.getClickRatio(slug);

        assertEquals(0.0, ratio);
        verify(repo, times(1)).totalMapPinClicks(slug);
        verify(repo, times(1)).totalStorePageClicks(slug);
    }

    @Test
    void testGetClickRatio_computesCorrectRatio() {
        String slug = "ratio-shop";
        when(repo.totalMapPinClicks(slug)).thenReturn(4L);
        when(repo.totalStorePageClicks(slug)).thenReturn(2L);

        double ratio = service.getClickRatio(slug);

        assertEquals(2.0, ratio);
        verify(repo).totalMapPinClicks(slug);
        verify(repo).totalStorePageClicks(slug);
    }

    @Test
    void testGetClickRatioWithDays_pageZeroReturnsZero() {
        String slug = "paged-zero";
        int days = 7;
        LocalDate fromDate = LocalDate.now().minusDays(days);

        when(repo.mapPinClicksSince(eq(slug), any(LocalDate.class))).thenReturn(10L);
        when(repo.storePageClicksSince(eq(slug), any(LocalDate.class))).thenReturn(0L);

        double ratio = service.getClickRatio(slug, days);

        assertEquals(0.0, ratio);

        ArgumentCaptor<LocalDate> captor = ArgumentCaptor.forClass(LocalDate.class);
        verify(repo).mapPinClicksSince(eq(slug), captor.capture());
        verify(repo).storePageClicksSince(eq(slug), captor.capture());

        // both calls should use the same from-date
        assertEquals(fromDate, captor.getAllValues().get(0));
        assertEquals(fromDate, captor.getAllValues().get(1));
    }

    @Test
    void testGetClickRatioWithDays_computesCorrectRatio() {
        String slug = "historic-shop";
        int days = 3;
        LocalDate fromDate = LocalDate.now().minusDays(days);

        when(repo.mapPinClicksSince(eq(slug), any(LocalDate.class))).thenReturn(6L);
        when(repo.storePageClicksSince(eq(slug), any(LocalDate.class))).thenReturn(3L);

        double ratio = service.getClickRatio(slug, days);

        assertEquals(2.0, ratio, 0.0001);

        ArgumentCaptor<LocalDate> captor = ArgumentCaptor.forClass(LocalDate.class);
        verify(repo).mapPinClicksSince(eq(slug), captor.capture());
        verify(repo).storePageClicksSince(eq(slug), captor.capture());

        assertEquals(fromDate, captor.getAllValues().get(0));
        assertEquals(fromDate, captor.getAllValues().get(1));
    }
}