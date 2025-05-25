package com.backend.APITests;

import com.backend.controller.StatisticsController;
import com.backend.model.EventType;
import com.backend.service.StatisticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatisticsController.class)
@WithMockUser
class StatisticsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @Test
    void recordEvent_whenStorePage_thenReturnsOkAndInvokesService() throws Exception {
        mockMvc.perform(post("/api/stats/event")
                        .with(csrf())
                        .param("slug", "test-store")
                        .param("type", "STORE_PAGE")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk());

        verify(statisticsService).recordEvent(EventType.STORE_PAGE, "test-store");
        verifyNoMoreInteractions(statisticsService);
    }

    @Test
    void recordEvent_whenMapPin_thenReturnsOkAndInvokesService() throws Exception {
        mockMvc.perform(post("/api/stats/event")
                        .with(csrf())
                        .param("slug", "test-pin")
                        .param("type", "MAP_PIN")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk());

        verify(statisticsService).recordEvent(EventType.MAP_PIN, "test-pin");
        verifyNoMoreInteractions(statisticsService);
    }

    @Test
    void recordEvent_missingSlug_thenBadRequest() throws Exception {
        mockMvc.perform(post("/api/stats/event")
                        .with(csrf())
                        .param("type", "STORE_PAGE")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(statisticsService);
    }

    @Test
    void recordEvent_invalidType_thenBadRequest() throws Exception {
        mockMvc.perform(post("/api/stats/event")
                        .with(csrf())
                        .param("slug", "x")
                        .param("type", "UNKNOWN")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(statisticsService);
    }

    @Test
    void getOverallRatio_withValidSlug_thenReturnsRatio() throws Exception {
        when(statisticsService.getClickRatio("store-abc")).thenReturn(1.25);

        mockMvc.perform(get("/api/stats/ratio")
                        .param("slug", "store-abc"))
                .andExpect(status().isOk())
                .andExpect(content().string("1.25"));

        verify(statisticsService).getClickRatio("store-abc");
        verifyNoMoreInteractions(statisticsService);
    }

    @Test
    void getOverallRatio_missingSlug_thenBadRequest() throws Exception {
        mockMvc.perform(get("/api/stats/ratio"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(statisticsService);
    }

    @Test
    void getRatioForPeriod_withValidParams_thenReturnsRatio() throws Exception {
        when(statisticsService.getClickRatio("store-xyz", 7)).thenReturn(0.5);

        mockMvc.perform(get("/api/stats/ratio/period")
                        .param("slug", "store-xyz")
                        .param("days", "7"))
                .andExpect(status().isOk())
                .andExpect(content().string("0.5"));

        verify(statisticsService).getClickRatio("store-xyz", 7);
        verifyNoMoreInteractions(statisticsService);
    }

    @Test
    void getRatioForPeriod_missingParams_thenBadRequest() throws Exception {
        // missing days
        mockMvc.perform(get("/api/stats/ratio/period")
                        .param("slug", "store-xyz"))
                .andExpect(status().isBadRequest());

        // missing slug
        mockMvc.perform(get("/api/stats/ratio/period")
                        .param("days", "7"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(statisticsService);
    }
}