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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatisticsController.class)
@WithMockUser               // <-- add this line so requests aren’t redirected to login
class StatisticsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @Test
    void recordEvent_storePage_success() throws Exception {
        mockMvc.perform(post("/api/stats/event")
                        .with(csrf())
                        .param("slug", "shop-123")
                        .param("type", "STORE_PAGE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(statisticsService).recordEvent(EventType.STORE_PAGE, "shop-123");
    }

    @Test
    void recordEvent_mapPin_success() throws Exception {
        mockMvc.perform(post("/api/stats/event")
                        .with(csrf())
                        .param("slug", "pin-shop")
                        .param("type", "MAP_PIN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(statisticsService).recordEvent(EventType.MAP_PIN, "pin-shop");
    }

    @Test
    void recordEvent_missingSlug_badRequest() throws Exception {
        mockMvc.perform(post("/api/stats/event")
                        .with(csrf())
                        .param("type", "STORE_PAGE"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(statisticsService);
    }

    @Test
    void recordEvent_invalidType_badRequest() throws Exception {
        mockMvc.perform(post("/api/stats/event")
                        .with(csrf())
                        .param("slug", "x")
                        .param("type", "INVALID"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(statisticsService);
    }
}