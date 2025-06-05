package com.backend.UnitTests;

import com.backend.model.BusinessHours;
import com.backend.model.DayOfWeek;
import com.backend.model.Store;
import com.backend.repository.BusinessHoursRepository;
import com.backend.service.BusinessHoursService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusinessHoursServiceUnitTests {

    @Mock
    private BusinessHoursRepository businessHoursRepository;

    @InjectMocks
    private BusinessHoursService businessHoursService;

    private Store testStore;
    private BusinessHours testBusinessHours;

    @BeforeEach
    void setUp() {
        testStore = new Store();
        testStore.setId(1L);
        testStore.setName("Test Store");

        testBusinessHours = new BusinessHours(
                testStore,
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(17, 0)
        );
        testBusinessHours.setId(1L);
    }

    @Test
    void getAllBusinessHours_ShouldReturnAllBusinessHours() {
        BusinessHours secondBusinessHours = new BusinessHours(
                testStore,
                DayOfWeek.TUESDAY,
                LocalTime.of(9, 0),
                LocalTime.of(17, 0)
        );
        when(businessHoursRepository.findAll()).thenReturn(Arrays.asList(testBusinessHours, secondBusinessHours));

        List<BusinessHours> result = businessHoursService.getAllBusinessHours();

        assertEquals(2, result.size());
        verify(businessHoursRepository, times(1)).findAll();
    }

    @Test
    void getBusinessHoursById_WhenExists_ShouldReturnBusinessHours() {
        when(businessHoursRepository.findById(1L)).thenReturn(Optional.of(testBusinessHours));

        Optional<BusinessHours> result = businessHoursService.getBusinessHoursById(1L);

        assertTrue(result.isPresent());
        assertEquals(testBusinessHours, result.get());
        verify(businessHoursRepository, times(1)).findById(1L);
    }

    @Test
    void getBusinessHoursById_WhenNotExists_ShouldReturnEmpty() {
        when(businessHoursRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<BusinessHours> result = businessHoursService.getBusinessHoursById(99L);

        assertFalse(result.isPresent());
        verify(businessHoursRepository, times(1)).findById(99L);
    }

    @Test
    void saveBusinessHours_WhenValid_ShouldSaveAndReturn() {
        when(businessHoursRepository.existsByStoreAndDayOfWeek(testStore, DayOfWeek.MONDAY)).thenReturn(false);
        when(businessHoursRepository.save(testBusinessHours)).thenReturn(testBusinessHours);

        BusinessHours result = businessHoursService.saveBusinessHours(testBusinessHours);

        assertEquals(testBusinessHours, result);
        verify(businessHoursRepository, times(1)).existsByStoreAndDayOfWeek(testStore, DayOfWeek.MONDAY);
        verify(businessHoursRepository, times(1)).save(testBusinessHours);
    }

    @Test
    void saveBusinessHours_WhenDuplicate_ShouldThrowException() {
        when(businessHoursRepository.existsByStoreAndDayOfWeek(testStore, DayOfWeek.MONDAY)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> businessHoursService.saveBusinessHours(testBusinessHours)
        );

        assertEquals("BusinessHours already exists for the given shop and day of week.", exception.getMessage());
        verify(businessHoursRepository, times(1)).existsByStoreAndDayOfWeek(testStore, DayOfWeek.MONDAY);
        verify(businessHoursRepository, never()).save(any(BusinessHours.class));
    }

    @Test
    void saveBusinessHours_WhenInvalidTimeRange_ShouldThrowException() {
        BusinessHours invalidBusinessHours = new BusinessHours(
                testStore,
                DayOfWeek.MONDAY,
                LocalTime.of(17, 0),
                LocalTime.of(9, 0)
        );
        when(businessHoursRepository.existsByStoreAndDayOfWeek(testStore, DayOfWeek.MONDAY)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> businessHoursService.saveBusinessHours(invalidBusinessHours)
        );

        assertEquals("Opening time must be before closing time.", exception.getMessage());
        verify(businessHoursRepository, times(1)).existsByStoreAndDayOfWeek(testStore, DayOfWeek.MONDAY);
        verify(businessHoursRepository, never()).save(any(BusinessHours.class));
    }

    @Test
    void saveBusinessHours_WhenEqualTimes_ShouldThrowException() {
        BusinessHours invalidBusinessHours = new BusinessHours(
                testStore,
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(9, 0)
        );
        when(businessHoursRepository.existsByStoreAndDayOfWeek(testStore, DayOfWeek.MONDAY)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> businessHoursService.saveBusinessHours(invalidBusinessHours)
        );

        assertEquals("Opening time must be before closing time.", exception.getMessage());
        verify(businessHoursRepository, times(1)).existsByStoreAndDayOfWeek(testStore, DayOfWeek.MONDAY);
        verify(businessHoursRepository, never()).save(any(BusinessHours.class));
    }

    @Test
    void updateBusinessHours_WhenExists_ShouldUpdateAndReturn() {
        when(businessHoursRepository.findById(1L)).thenReturn(Optional.of(testBusinessHours));
        when(businessHoursRepository.save(testBusinessHours)).thenReturn(testBusinessHours);

        LocalTime newOpeningTime = LocalTime.of(10, 0);
        LocalTime newClosingTime = LocalTime.of(18, 0);

        BusinessHours result = businessHoursService.updateBusinessHours(
                1L, DayOfWeek.TUESDAY, newOpeningTime, newClosingTime
        );

        assertEquals(DayOfWeek.TUESDAY, result.getDayOfWeek());
        assertEquals(newOpeningTime, result.getOpeningTime());
        assertEquals(newClosingTime, result.getClosingTime());
        verify(businessHoursRepository, times(1)).findById(1L);
        verify(businessHoursRepository, times(1)).save(testBusinessHours);
    }

    @Test
    void updateBusinessHours_WhenNotExists_ShouldThrowException() {
        when(businessHoursRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> businessHoursService.updateBusinessHours(
                        99L, DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(18, 0)
                )
        );

        assertEquals("BusinessHours not found", exception.getMessage());
        verify(businessHoursRepository, times(1)).findById(99L);
        verify(businessHoursRepository, never()).save(any(BusinessHours.class));
    }

    @Test
    void updateBusinessHours_WithNullValues_ShouldKeepExistingValues() {
        when(businessHoursRepository.findById(1L)).thenReturn(Optional.of(testBusinessHours));
        when(businessHoursRepository.save(testBusinessHours)).thenReturn(testBusinessHours);

        DayOfWeek originalDay = testBusinessHours.getDayOfWeek();
        LocalTime originalOpeningTime = testBusinessHours.getOpeningTime();
        LocalTime originalClosingTime = testBusinessHours.getClosingTime();

        BusinessHours result = businessHoursService.updateBusinessHours(1L, null, null, null);

        assertEquals(originalDay, result.getDayOfWeek());
        assertEquals(originalOpeningTime, result.getOpeningTime());
        assertEquals(originalClosingTime, result.getClosingTime());
        verify(businessHoursRepository, times(1)).findById(1L);
        verify(businessHoursRepository, times(1)).save(testBusinessHours);
    }

    @Test
    void updateBusinessHours_WithInvalidTimeRange_ShouldThrowException() {
        when(businessHoursRepository.findById(1L)).thenReturn(Optional.of(testBusinessHours));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> businessHoursService.updateBusinessHours(
                        1L, null, LocalTime.of(18, 0), LocalTime.of(9, 0)
                )
        );

        assertEquals("Opening time must be before closing time.", exception.getMessage());
        verify(businessHoursRepository, times(1)).findById(1L);
        verify(businessHoursRepository, never()).save(any(BusinessHours.class));
    }

    @Test
    void deleteBusinessHours_WhenExists_ShouldReturnTrue() {
        when(businessHoursRepository.findById(1L)).thenReturn(Optional.of(testBusinessHours));
        doNothing().when(businessHoursRepository).deleteById(1L);

        boolean result = businessHoursService.deleteBusinessHours(1L);

        assertTrue(result);
        verify(businessHoursRepository, times(1)).findById(1L);
        verify(businessHoursRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteBusinessHours_WhenNotExists_ShouldReturnFalse() {
        when(businessHoursRepository.findById(99L)).thenReturn(Optional.empty());

        boolean result = businessHoursService.deleteBusinessHours(99L);

        assertFalse(result);
        verify(businessHoursRepository, times(1)).findById(99L);
        verify(businessHoursRepository, never()).deleteById(anyLong());
    }
}