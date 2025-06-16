package com.backend.UnitTests;
import com.backend.model.Opinion;
import com.backend.model.OpinionReport;
import com.backend.model.User;
import com.backend.repository.OpinionReportRepository;
import com.backend.repository.OpinionRepository;
import com.backend.repository.UserRepository;
import com.backend.service.OpinionReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OpinionReportServiceUnitTests {

    @Mock
    private OpinionReportRepository opinionReportRepository;
    @Mock
    private OpinionRepository opinionRepository;
    @Mock
    private UserRepository userRepository;

    private OpinionReportService opinionReportService;

    @BeforeEach
    void setUp() {
        opinionReportService = new OpinionReportService(opinionReportRepository, opinionRepository, userRepository);
    }

    @Test
    void testSaveOpinionReport_Success() {
        Opinion opinion = new Opinion();
        opinion.setId(1L);
        opinion.setReported(false);
        User user = new User();
        user.setId("user1");
        OpinionReport report = new OpinionReport(opinion, user);

        when(opinionReportRepository.existsByOpinionIdAndUserId(1L, "user1")).thenReturn(false);
        when(opinionReportRepository.countByOpinionId(1L)).thenReturn(4L);

        OpinionReport result = opinionReportService.saveOpinionReport(report);

        verify(opinionReportRepository).save(report);
        assertEquals(report, result);
        assertFalse(opinion.isReported());
    }

    @Test
    void testSaveOpinionReport_ThrowsIfDuplicate() {
        Opinion opinion = new Opinion();
        opinion.setId(1L);
        User user = new User();
        user.setId("user1");
        OpinionReport report = new OpinionReport(opinion, user);

        when(opinionReportRepository.existsByOpinionIdAndUserId(1L, "user1")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> opinionReportService.saveOpinionReport(report));
        verify(opinionReportRepository, never()).save(any());
    }

    @Test
    void testSaveOpinionReport_SetsReportedIfThresholdReached() {
        Opinion opinion = new Opinion();
        opinion.setId(1L);
        opinion.setReported(false);
        User user = new User();
        user.setId("user1");
        OpinionReport report = new OpinionReport(opinion, user);

        when(opinionReportRepository.existsByOpinionIdAndUserId(1L, "user1")).thenReturn(false);
        when(opinionReportRepository.countByOpinionId(1L)).thenReturn(5L);

        opinionReportService.saveOpinionReport(report);

        assertTrue(opinion.isReported());
        verify(opinionRepository).save(opinion);
    }

    @Test
    void testGetOpinionReportById_Found() {
        OpinionReport report = mock(OpinionReport.class);
        when(opinionReportRepository.findById(1L)).thenReturn(Optional.of(report));

        Optional<OpinionReport> result = opinionReportService.getOpinionReportById(1L);

        assertTrue(result.isPresent());
        assertEquals(report, result.get());
    }

    @Test
    void testGetOpinionReportById_NotFound() {
        when(opinionReportRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<OpinionReport> result = opinionReportService.getOpinionReportById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteOpinionReportById_Success() {
        OpinionReport report = mock(OpinionReport.class);
        when(opinionReportRepository.findById(1L)).thenReturn(Optional.of(report));

        boolean deleted = opinionReportService.deleteOpinionReportById(1L);

        assertTrue(deleted);
        verify(opinionReportRepository).deleteById(1L);
    }

    @Test
    void testDeleteOpinionReportById_NotFound() {
        when(opinionReportRepository.findById(1L)).thenReturn(Optional.empty());

        boolean deleted = opinionReportService.deleteOpinionReportById(1L);

        assertFalse(deleted);
        verify(opinionReportRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetAllOpinionReports() {
        List<OpinionReport> reports = List.of(mock(OpinionReport.class));
        when(opinionReportRepository.findAll()).thenReturn(reports);

        List<OpinionReport> result = opinionReportService.getAllOpinionReports();

        assertEquals(reports, result);
    }
}