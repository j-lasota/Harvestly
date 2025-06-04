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
        // Arrange
        BusinessHours secondBusinessHours = new BusinessHours(
                testStore,
                DayOfWeek.TUESDAY,
                LocalTime.of(9, 0),
                LocalTime.of(17, 0)
        );
        when(businessHoursRepository.findAll()).thenReturn(Arrays.asList(testBusinessHours, secondBusinessHours));

        // Act
        List<BusinessHours> result = businessHoursService.getAllBusinessHours();

        // Assert
        assertEquals(2, result.size());
        verify(businessHoursRepository, times(1)).findAll();
    }

    @Test
    void getBusinessHoursById_WhenExists_ShouldReturnBusinessHours() {
        // Arrange
        when(businessHoursRepository.findById(1L)).thenReturn(Optional.of(testBusinessHours));

        // Act
        Optional<BusinessHours> result = businessHoursService.getBusinessHoursById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testBusinessHours, result.get());
        verify(businessHoursRepository, times(1)).findById(1L);
    }

    @Test
    void getBusinessHoursById_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(businessHoursRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<BusinessHours> result = businessHoursService.getBusinessHoursById(99L);

        // Assert
        assertFalse(result.isPresent());
        verify(businessHoursRepository, times(1)).findById(99L);
    }

    @Test
    void saveBusinessHours_WhenValid_ShouldSaveAndReturn() {
        // Arrange
        when(businessHoursRepository.existsByStoreAndDayOfWeek(testStore, DayOfWeek.MONDAY)).thenReturn(false);
        when(businessHoursRepository.save(testBusinessHours)).thenReturn(testBusinessHours);

        // Act
        BusinessHours result = businessHoursService.saveBusinessHours(testBusinessHours);

        // Assert
        assertEquals(testBusinessHours, result);
        verify(businessHoursRepository, times(1)).existsByStoreAndDayOfWeek(testStore, DayOfWeek.MONDAY);
        verify(businessHoursRepository, times(1)).save(testBusinessHours);
    }

    @Test
    void saveBusinessHours_WhenDuplicate_ShouldThrowException() {
        // Arrange
        when(businessHoursRepository.existsByStoreAndDayOfWeek(testStore, DayOfWeek.MONDAY)).thenReturn(true);

        // Act & Assert
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
        // Arrange
        BusinessHours invalidBusinessHours = new BusinessHours(
                testStore,
                DayOfWeek.MONDAY,
                LocalTime.of(17, 0),  // Opening time after closing time
                LocalTime.of(9, 0)
        );
        when(businessHoursRepository.existsByStoreAndDayOfWeek(testStore, DayOfWeek.MONDAY)).thenReturn(false);

        // Act & Assert
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
        // Arrange
        BusinessHours invalidBusinessHours = new BusinessHours(
                testStore,
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(9, 0)  // Same as opening time
        );
        when(businessHoursRepository.existsByStoreAndDayOfWeek(testStore, DayOfWeek.MONDAY)).thenReturn(false);

        // Act & Assert
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
        // Arrange
        when(businessHoursRepository.findById(1L)).thenReturn(Optional.of(testBusinessHours));
        when(businessHoursRepository.save(testBusinessHours)).thenReturn(testBusinessHours);

        LocalTime newOpeningTime = LocalTime.of(10, 0);
        LocalTime newClosingTime = LocalTime.of(18, 0);

        // Act
        BusinessHours result = businessHoursService.updateBusinessHours(
                1L, DayOfWeek.TUESDAY, newOpeningTime, newClosingTime
        );

        // Assert
        assertEquals(DayOfWeek.TUESDAY, result.getDayOfWeek());
        assertEquals(newOpeningTime, result.getOpeningTime());
        assertEquals(newClosingTime, result.getClosingTime());
        verify(businessHoursRepository, times(1)).findById(1L);
        verify(businessHoursRepository, times(1)).save(testBusinessHours);
    }

    @Test
    void updateBusinessHours_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(businessHoursRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
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
        // Arrange
        when(businessHoursRepository.findById(1L)).thenReturn(Optional.of(testBusinessHours));
        when(businessHoursRepository.save(testBusinessHours)).thenReturn(testBusinessHours);

        DayOfWeek originalDay = testBusinessHours.getDayOfWeek();
        LocalTime originalOpeningTime = testBusinessHours.getOpeningTime();
        LocalTime originalClosingTime = testBusinessHours.getClosingTime();

        // Act
        BusinessHours result = businessHoursService.updateBusinessHours(1L, null, null, null);

        // Assert
        assertEquals(originalDay, result.getDayOfWeek());
        assertEquals(originalOpeningTime, result.getOpeningTime());
        assertEquals(originalClosingTime, result.getClosingTime());
        verify(businessHoursRepository, times(1)).findById(1L);
        verify(businessHoursRepository, times(1)).save(testBusinessHours);
    }

    @Test
    void updateBusinessHours_WithInvalidTimeRange_ShouldThrowException() {
        // Arrange
        when(businessHoursRepository.findById(1L)).thenReturn(Optional.of(testBusinessHours));

        // Act & Assert
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
        // Arrange
        when(businessHoursRepository.findById(1L)).thenReturn(Optional.of(testBusinessHours));
        doNothing().when(businessHoursRepository).deleteById(1L);

        // Act
        boolean result = businessHoursService.deleteBusinessHours(1L);

        // Assert
        assertTrue(result);
        verify(businessHoursRepository, times(1)).findById(1L);
        verify(businessHoursRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteBusinessHours_WhenNotExists_ShouldReturnFalse() {
        // Arrange
        when(businessHoursRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        boolean result = businessHoursService.deleteBusinessHours(99L);

        // Assert
        assertFalse(result);
        verify(businessHoursRepository, times(1)).findById(99L);
        verify(businessHoursRepository, never()).deleteById(anyLong());
    }
}