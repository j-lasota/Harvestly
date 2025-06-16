package com.backend.APITests;

import com.backend.model.BusinessHours;
import com.backend.model.DayOfWeek;
import com.backend.model.Store;
import com.backend.model.User;
import com.backend.repository.UserRepository;
import com.backend.service.BusinessHoursService;
import com.backend.service.StoreService;
import com.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = "app.method-security.enabled=false")
class BusinessHoursAPINoSecurityTests {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private BusinessHoursService businessHoursService;

    @MockitoBean
    private StoreService storeService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void createBusinessHours_ReturnsCreatedBusinessHours() {
        Long storeId = 1L;
        Store store = new Store();
        store.setId(storeId);
        store.setName("Test Store");
        User user = new User();
        String userId = "2a6e8658-d6db-45d8-9131-e8f87b62ed75";
        user.setId(userId);
        store.setUser(user);

        BusinessHours createdBusinessHours = new BusinessHours(
                store, DayOfWeek.WEDNESDAY, LocalTime.of(10, 0), LocalTime.of(18, 0)
        );

        when(storeService.getStoreById(storeId)).thenReturn(Optional.of(store));
        when(businessHoursService.saveBusinessHours(any(BusinessHours.class))).thenReturn(createdBusinessHours);

        String mutation = """
            mutation {
              createBusinessHours(
                storeId: 1
                dayOfWeek: WEDNESDAY
                openingTime: "10:00:00"
                closingTime: "18:00:00"
              ) {
                dayOfWeek
                openingTime
                closingTime
              }
            }
            """;

        graphQlTester.document(mutation)
                .execute()
                .path("createBusinessHours")
                .entity(BusinessHours.class)
                .satisfies(businessHours -> {
                    assert businessHours.getDayOfWeek() == DayOfWeek.WEDNESDAY;
                    assert businessHours.getOpeningTime().equals(LocalTime.of(10, 0));
                    assert businessHours.getClosingTime().equals(LocalTime.of(18, 0));
                });
    }
}