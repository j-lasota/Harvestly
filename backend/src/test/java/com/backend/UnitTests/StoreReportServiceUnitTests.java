import com.backend.model.Store;
import com.backend.model.StoreReport;
import com.backend.model.User;
import com.backend.repository.StoreReportRepository;
import com.backend.repository.StoreRepository;
import com.backend.repository.UserRepository;
import com.backend.service.StoreReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StoreReportServiceUnitTests {
    @Mock
    private StoreReportRepository storeReportRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private UserRepository userRepository;

    private StoreReportService storeReportService;

    @BeforeEach
    void setUp() {
        storeReportService = new StoreReportService(storeReportRepository, storeRepository, userRepository);
    }

    @Test
    void testSaveStoreReport_Success() {
        Store store = new Store();
        store.setId(1L);
        store.setReported(false);
        User user = new User();
        user.setId("user1");
        StoreReport report = new StoreReport(store, user);

        when(storeReportRepository.existsByStoreIdAndUserId(1L, "user1")).thenReturn(false);
        when(storeReportRepository.countByStoreId(1L)).thenReturn(4L);

        StoreReport result = storeReportService.saveStoreReport(report);

        verify(storeReportRepository).save(report);
        assertEquals(report, result);
        assertFalse(store.isReported());
    }

    @Test
    void testSaveStoreReport_ThrowsIfDuplicate() {
        Store store = new Store();
        store.setId(1L);
        User user = new User();
        user.setId("user1");
        StoreReport report = new StoreReport(store, user);

        when(storeReportRepository.existsByStoreIdAndUserId(1L, "user1")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> storeReportService.saveStoreReport(report));
        verify(storeReportRepository, never()).save(any());
    }

    @Test
    void testSaveStoreReport_SetsReportedIfThresholdReached() {
        Store store = new Store();
        store.setId(1L);
        store.setReported(false);
        User user = new User();
        user.setId("user1");
        StoreReport report = new StoreReport(store, user);

        when(storeReportRepository.existsByStoreIdAndUserId(1L, "user1")).thenReturn(false);
        when(storeReportRepository.countByStoreId(1L)).thenReturn(5L);

        storeReportService.saveStoreReport(report);

        assertTrue(store.isReported());
        verify(storeRepository).save(store);
    }

    @Test
    void testGetStoreReportById_Found() {
        StoreReport report = mock(StoreReport.class);
        when(storeReportRepository.findById(1L)).thenReturn(Optional.of(report));

        Optional<StoreReport> result = storeReportService.getStoreReportById(1L);

        assertTrue(result.isPresent());
        assertEquals(report, result.get());
    }

    @Test
    void testGetStoreReportById_NotFound() {
        when(storeReportRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<StoreReport> result = storeReportService.getStoreReportById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteStoreReportById_Success() {
        StoreReport report = mock(StoreReport.class);
        when(storeReportRepository.findById(1L)).thenReturn(Optional.of(report));

        boolean deleted = storeReportService.deleteStoreReportById(1L);

        assertTrue(deleted);
        verify(storeReportRepository).deleteById(1L);
    }

    @Test
    void testDeleteStoreReportById_NotFound() {
        when(storeReportRepository.findById(1L)).thenReturn(Optional.empty());

        boolean deleted = storeReportService.deleteStoreReportById(1L);

        assertFalse(deleted);
        verify(storeReportRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetAllStoreReports() {
        List<StoreReport> reports = List.of(mock(StoreReport.class));
        when(storeReportRepository.findAll()).thenReturn(reports);

        List<StoreReport> result = storeReportService.getAllStoreReports();

        assertEquals(reports, result);
    }
}