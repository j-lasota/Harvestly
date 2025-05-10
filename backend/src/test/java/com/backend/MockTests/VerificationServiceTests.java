package com.backend.MockTests;

import com.backend.model.Store;
import com.backend.model.User;
import com.backend.model.Verification;
import com.backend.repository.VerificationRepository;
import com.backend.service.VerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VerificationServiceTests {

    @Mock
    private VerificationRepository verificationRepository;

    @InjectMocks
    private VerificationService verificationService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveVerification() {
        Store store = new Store("Shop1", "Description1", 10.0, 10.0, "City1", "Address1", "img1.jpg");
        User user = new User();
        Verification verification = new Verification(store, user);

        when(verificationRepository.save(verification)).thenReturn(verification);

        Verification savedVerification = verificationService.saveVerification(verification);

        assertNotNull(savedVerification);
        assertEquals(store, savedVerification.getStore());
        assertEquals(user, savedVerification.getUser());
        verify(verificationRepository, times(1)).save(verification);
    }

    @Test
    void testGetVerificationById_VerificationExists() {
        Verification verification = new Verification();
        verification.setId(1L);

        when(verificationRepository.findById(1L)).thenReturn(Optional.of(verification));

        Optional<Verification> result = verificationService.getVerificationById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(verificationRepository, times(1)).findById(1L);
    }

    @Test
    void testGetVerificationById_VerificationDoesNotExist() {
        when(verificationRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Verification> result = verificationService.getVerificationById(2L);

        assertFalse(result.isPresent());
        verify(verificationRepository, times(1)).findById(2L);
    }

    @Test
    void testGetAllVerifications() {
        List<Verification> verifications = List.of(
                new Verification(new Store("ShopA", "desc", 1.0, 1.0, "CityA", "AddrA", "imgA.jpg"), new User()),
                new Verification(new Store("ShopB", "desc", 2.0, 2.0, "CityB", "AddrB", "imgB.jpg"), new User())
        );

        when(verificationRepository.findAll()).thenReturn(verifications);

        List<Verification> result = verificationService.getAllVerifications();

        assertEquals(2, result.size());
        verify(verificationRepository, times(1)).findAll();
    }

    @Test
    void testDeleteVerificationById_VerificationExists() {
        Verification verification = new Verification();
        verification.setId(3L);

        when(verificationRepository.findById(3L)).thenReturn(Optional.of(verification));

        Boolean result = verificationService.deleteVerificationById(3L);

        assertTrue(result);
        verify(verificationRepository, times(1)).deleteById(3L);
    }

    @Test
    void testDeleteVerificationById_VerificationDoesNotExist() {
        when(verificationRepository.findById(4L)).thenReturn(Optional.empty());

        Boolean result = verificationService.deleteVerificationById(4L);

        assertFalse(result);
        verify(verificationRepository, never()).deleteById(any());
    }
}
