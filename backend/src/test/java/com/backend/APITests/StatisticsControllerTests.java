package com.backend.APITests;

import com.backend.controller.StatisticsController;
import com.backend.model.EventType;
import com.backend.service.StatisticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatisticsController.class)
class StatisticsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @Test
    void recordEvent_storePage_success() throws Exception {
        mockMvc.perform(post("/api/stats/event")
                        .param("storeId", "10")
                        .param("type", "STORE_PAGE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(statisticsService).recordEvent(EventType.STORE_PAGE, 10L);
    }

    @Test
    void recordEvent_mapPin_success() throws Exception {
        mockMvc.perform(post("/api/stats/event")
                        .param("storeId", "20")
                        .param("type", "MAP_PIN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(statisticsService).recordEvent(EventType.MAP_PIN, 20L);
    }

    @Test
    void recordEvent_missingStoreId_badRequest() throws Exception {
        mockMvc.perform(post("/api/stats/event")
                        .param("type", "STORE_PAGE"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(statisticsService);
    }

    @Test
    void recordEvent_invalidType_badRequest() throws Exception {
        mockMvc.perform(post("/api/stats/event")
                        .param("storeId", "1")
                        .param("type", "INVALID"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(statisticsService);
    }
}