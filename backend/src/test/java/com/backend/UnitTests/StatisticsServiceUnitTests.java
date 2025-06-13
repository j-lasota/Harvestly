package com.backend.UnitTests;

import com.backend.model.EventType;
import com.backend.repository.DailyClickCountRepository;
import com.backend.repository.OpinionRepository;
import com.backend.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceUnitTests {

    @Mock
    private DailyClickCountRepository clickRepository;

    @Mock
    private OpinionRepository opinionRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    @Captor
    private ArgumentCaptor<String> slugCaptor;

    @Captor
    private ArgumentCaptor<LocalDate> dateCaptor;

    private final String TEST_SLUG = "test-store";


    @Test
    void testRecordStorePageEvent() {
        statisticsService.recordEvent(EventType.STORE_PAGE, TEST_SLUG);

        verify(clickRepository, times(1)).upsertStorePageClick(slugCaptor.capture(), dateCaptor.capture());
        assertEquals(TEST_SLUG, slugCaptor.getValue());
        assertEquals(LocalDate.now(), dateCaptor.getValue());
    }

    @Test
    void testRecordMapPinEvent() {
        statisticsService.recordEvent(EventType.MAP_PIN, TEST_SLUG);

        verify(clickRepository, times(1)).upsertMapPinClick(slugCaptor.capture(), dateCaptor.capture());
        assertEquals(TEST_SLUG, slugCaptor.getValue());
        assertEquals(LocalDate.now(), dateCaptor.getValue());
    }

    @Test
    void testGetClickRatio() {
        when(clickRepository.totalMapPinClicks(TEST_SLUG)).thenReturn(150L);
        when(clickRepository.totalStorePageClicks(TEST_SLUG)).thenReturn(300L);

        double ratio = statisticsService.getClickRatio(TEST_SLUG);

        assertEquals(0.5, ratio, 0.001);
        verify(clickRepository, times(1)).totalMapPinClicks(TEST_SLUG);
        verify(clickRepository, times(1)).totalStorePageClicks(TEST_SLUG);
    }

    @Test
    void testGetClickRatioWithZeroPageClicks() {
        when(clickRepository.totalMapPinClicks(TEST_SLUG)).thenReturn(150L);
        when(clickRepository.totalStorePageClicks(TEST_SLUG)).thenReturn(0L);

        double ratio = statisticsService.getClickRatio(TEST_SLUG);

        assertEquals(0.0, ratio);
        verify(clickRepository, times(1)).totalMapPinClicks(TEST_SLUG);
        verify(clickRepository, times(1)).totalStorePageClicks(TEST_SLUG);
    }

    @Test
    void testGetClickRatioWithTimeFrame() {
        int days = 30;
        LocalDate expectedDate = LocalDate.now().minusDays(days);
        when(clickRepository.mapPinClicksSince(eq(TEST_SLUG), any(LocalDate.class))).thenReturn(75L);
        when(clickRepository.storePageClicksSince(eq(TEST_SLUG), any(LocalDate.class))).thenReturn(100L);

        double ratio = statisticsService.getClickRatio(TEST_SLUG, days);

        assertEquals(0.75, ratio, 0.001);
        verify(clickRepository, times(1)).mapPinClicksSince(eq(TEST_SLUG), argThat(date ->
                date.isEqual(expectedDate)));
        verify(clickRepository, times(1)).storePageClicksSince(eq(TEST_SLUG), argThat(date ->
                date.isEqual(expectedDate)));
    }

    @Test
    void testGetClickRatioWithTimeFrameAndZeroPageClicks() {
        int days = 30;
        when(clickRepository.mapPinClicksSince(eq(TEST_SLUG), any(LocalDate.class))).thenReturn(75L);
        when(clickRepository.storePageClicksSince(eq(TEST_SLUG), any(LocalDate.class))).thenReturn(0L);

        double ratio = statisticsService.getClickRatio(TEST_SLUG, days);

        assertEquals(0.0, ratio);
    }

    @Test
    void testGetAverageRating() {
        when(opinionRepository.findAverageStarsByStoreSlug(TEST_SLUG)).thenReturn(4.5);

        double avgRating = statisticsService.getAverageRating(TEST_SLUG);

        assertEquals(4.5, avgRating, 0.001);
        verify(opinionRepository, times(1)).findAverageStarsByStoreSlug(TEST_SLUG);
    }

    @Test
    void testGetAverageRatingWithNoRatings() {
        when(opinionRepository.findAverageStarsByStoreSlug(TEST_SLUG)).thenReturn(null);

        double avgRating = statisticsService.getAverageRating(TEST_SLUG);

        assertEquals(0.0, avgRating);
        verify(opinionRepository, times(1)).findAverageStarsByStoreSlug(TEST_SLUG);
    }
}