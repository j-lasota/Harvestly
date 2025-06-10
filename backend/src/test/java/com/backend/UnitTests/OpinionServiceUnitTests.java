package com.backend.UnitTests;

import com.backend.model.Opinion;
import com.backend.model.Store;
import com.backend.model.User;
import com.backend.repository.OpinionRepository;
import com.backend.service.OpinionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpinionServiceUnitTests {

    @Mock
    private OpinionRepository opinionRepository;

    @InjectMocks
    private OpinionService opinionService;

    private Opinion testOpinion;
    private Store testStore;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("user123", "John", "Doe", "john@example.com", "123456789", 0, "user.jpg");
        testStore = new Store();
        testStore.setId(1L);

        testOpinion = new Opinion(testStore, testUser, "Great store!", 5);
        testOpinion.setId(1L);
    }

    @Test
    void getAllOpinions_ShouldReturnAllOpinions() {
        Opinion secondOpinion = new Opinion(testStore, testUser, "Another opinion", 4);
        when(opinionRepository.findAll()).thenReturn(Arrays.asList(testOpinion, secondOpinion));

        List<Opinion> opinions = opinionService.getAllOpinions();

        assertEquals(2, opinions.size());
        verify(opinionRepository, times(1)).findAll();
    }

    @Test
    void getOpinionById_WhenOpinionExists_ShouldReturnOpinion() {
        when(opinionRepository.findById(1L)).thenReturn(Optional.of(testOpinion));

        Optional<Opinion> result = opinionService.getOpinionById(1L);

        assertTrue(result.isPresent());
        assertEquals(testOpinion, result.get());
        verify(opinionRepository, times(1)).findById(1L);
    }

    @Test
    void getOpinionById_WhenOpinionDoesNotExist_ShouldReturnEmpty() {
        when(opinionRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Opinion> result = opinionService.getOpinionById(99L);

        assertFalse(result.isPresent());
        verify(opinionRepository, times(1)).findById(99L);
    }

    @Test
    void saveOpinion_WhenOpinionDoesNotExist_ShouldSaveAndReturnOpinion() {
        when(opinionRepository.existsByStoreIdAndUserId(testStore.getId(), testUser.getId())).thenReturn(false);
        when(opinionRepository.save(testOpinion)).thenReturn(testOpinion);

        Opinion savedOpinion = opinionService.saveOpinion(testOpinion);

        assertEquals(testOpinion, savedOpinion);
        verify(opinionRepository, times(1)).existsByStoreIdAndUserId(testStore.getId(), testUser.getId());
        verify(opinionRepository, times(1)).save(testOpinion);
    }

    @Test
    void saveOpinion_WhenOpinionAlreadyExists_ShouldThrowException() {
        when(opinionRepository.existsByStoreIdAndUserId(testStore.getId(), testUser.getId())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> opinionService.saveOpinion(testOpinion)
        );

        assertEquals("Opinion already exists for the given shop and user.", exception.getMessage());
        verify(opinionRepository, times(1)).existsByStoreIdAndUserId(testStore.getId(), testUser.getId());
        verify(opinionRepository, never()).save(any(Opinion.class));
    }

    @Test
    void deleteOpinionById_WhenOpinionExists_ShouldReturnTrue() {
        when(opinionRepository.findById(1L)).thenReturn(Optional.of(testOpinion));
        doNothing().when(opinionRepository).deleteById(1L);

        boolean result = opinionService.deleteOpinionById(1L);

        assertTrue(result);
        verify(opinionRepository, times(1)).findById(1L);
        verify(opinionRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteOpinionById_WhenOpinionDoesNotExist_ShouldReturnFalse() {
        when(opinionRepository.findById(99L)).thenReturn(Optional.empty());

        boolean result = opinionService.deleteOpinionById(99L);

        assertFalse(result);
        verify(opinionRepository, times(1)).findById(99L);
        verify(opinionRepository, never()).deleteById(anyLong());
    }

    @Test
    void getOpinionsByStoreId_ShouldReturnOpinionsForStore() {
        Long storeId = 1L;
        Opinion secondOpinion = new Opinion(testStore, testUser, "Another opinion", 4);
        when(opinionRepository.findByStoreId(storeId)).thenReturn(Arrays.asList(testOpinion, secondOpinion));

        List<Opinion> opinions = opinionService.getOpinionsByStoreId(storeId);

        assertEquals(2, opinions.size());
        verify(opinionRepository, times(1)).findByStoreId(storeId);
    }

    @Test
    void updateOpinion_WhenOpinionExists_ShouldUpdateAndReturnOpinion() {
        when(opinionRepository.findById(1L)).thenReturn(Optional.of(testOpinion));
        when(opinionRepository.save(testOpinion)).thenReturn(testOpinion);

        Opinion updatedOpinion = opinionService.updateOpinion(1L, "Updated description", 4,null);

        assertEquals("Updated description", updatedOpinion.getDescription());
        assertEquals(4, updatedOpinion.getStars());
        verify(opinionRepository, times(1)).findById(1L);
        verify(opinionRepository, times(1)).save(testOpinion);
    }

    @Test
    void updateOpinion_WhenOpinionDoesNotExist_ShouldThrowException() {
        when(opinionRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> opinionService.updateOpinion(99L, "Updated description", 4, null)
        );

        assertEquals("Opinion not found", exception.getMessage());
        verify(opinionRepository, times(1)).findById(99L);
        verify(opinionRepository, never()).save(any(Opinion.class));
    }

    @Test
    void updateOpinion_WithNullValues_ShouldKeepExistingValues() {
        when(opinionRepository.findById(1L)).thenReturn(Optional.of(testOpinion));
        when(opinionRepository.save(testOpinion)).thenReturn(testOpinion);
        String originalDescription = testOpinion.getDescription();
        int originalStars = testOpinion.getStars();

        Opinion updatedOpinion = opinionService.updateOpinion(1L, null, null, null);

        assertEquals(originalDescription, updatedOpinion.getDescription());
        assertEquals(originalStars, updatedOpinion.getStars());
        verify(opinionRepository, times(1)).findById(1L);
        verify(opinionRepository, times(1)).save(testOpinion);
    }

    @Test
    void updateOpinion_WithInvalidStars_ShouldThrowException() {
        when(opinionRepository.findById(1L)).thenReturn(Optional.of(testOpinion));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> opinionService.updateOpinion(1L, "Valid description", 6, null)
        );

        assertEquals("Stars must be between 0 and 5.", exception.getMessage());
        verify(opinionRepository, times(1)).findById(1L);
        verify(opinionRepository, never()).save(any(Opinion.class));
    }

    @Test
    void updateOpinion_WithNegativeStars_ShouldThrowException() {
        when(opinionRepository.findById(1L)).thenReturn(Optional.of(testOpinion));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> opinionService.updateOpinion(1L, "Valid description", -1, null)
        );

        assertEquals("Stars must be between 0 and 5.", exception.getMessage());
        verify(opinionRepository, times(1)).findById(1L);
        verify(opinionRepository, never()).save(any(Opinion.class));
    }
}