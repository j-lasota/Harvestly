package com.backend.APITests;

import com.backend.model.BusinessHours;
import com.backend.model.Store;
import com.backend.service.BusinessHoursService;
import com.backend.service.StoreService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.graphql.test.tester.GraphQlTester;

import com.backend.model.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureGraphQlTester
class BusinessHoursAPITests {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private BusinessHoursService businessHoursService;

    @MockitoBean
    private StoreService storeService;

    @Test
    void businessHours_ReturnsAllBusinessHours() {
        Store store = new Store();
        store.setId(1L);
        store.setName("Test Store");

        List<BusinessHours> mockBusinessHours = Arrays.asList(
                new BusinessHours(store, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(16, 0)),
                new BusinessHours(store, DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(16, 0))
        );
        when(businessHoursService.getAllBusinessHours()).thenReturn(mockBusinessHours);

        String query = """
                query {
                  businessHours {
                    dayOfWeek
                    openingTime
                    closingTime
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("businessHours")
                .entityList(BusinessHours.class)
                .hasSize(2);
    }

    @Test
    void businessHoursById_ReturnsBusinessHours_WhenExists() {
        Long businessHoursId = 1L;
        Store store = new Store();
        store.setId(1L);
        store.setName("Test Store");

        BusinessHours mockBusinessHours = new BusinessHours(
                store, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0)
        );
        when(businessHoursService.getBusinessHoursById(businessHoursId)).thenReturn(Optional.of(mockBusinessHours));

        String query = """
                query {
                  businessHoursById(id: 1) {
                    dayOfWeek
                    openingTime
                    closingTime
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("businessHoursById")
                .entity(BusinessHours.class)
                .satisfies(businessHours -> {
                    assert businessHours.getDayOfWeek() == DayOfWeek.MONDAY;
                    assert businessHours.getOpeningTime().equals(LocalTime.of(9, 0));
                    assert businessHours.getClosingTime().equals(LocalTime.of(17, 0));
                });
    }

    @Test
    void createBusinessHours_ReturnsCreatedBusinessHours() {
        Long storeId = 1L;
        Store store = new Store();
        store.setId(storeId);
        store.setName("Test Store");

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

    @Test
    void updateBusinessHours_ReturnsUpdatedBusinessHours() {
        Store store = new Store();
        store.setId(1L);
        BusinessHours updatedBusinessHours = new BusinessHours(
                store, DayOfWeek.THURSDAY, LocalTime.of(9, 30), LocalTime.of(17, 30)
        );

        when(businessHoursService.updateBusinessHours(
                anyLong(), any(DayOfWeek.class), any(LocalTime.class), any(LocalTime.class))
        ).thenReturn(updatedBusinessHours);

        String mutation = """
                mutation {
                  updateBusinessHours(
                    id: 1
                    dayOfWeek: THURSDAY
                    openingTime: "09:30:00"
                    closingTime: "17:30:00"
                  ) {
                    dayOfWeek
                    openingTime
                    closingTime
                  }
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .path("updateBusinessHours")
                .entity(BusinessHours.class)
                .satisfies(businessHours -> {
                    assert businessHours.getDayOfWeek() == DayOfWeek.THURSDAY;
                    assert businessHours.getOpeningTime().equals(LocalTime.of(9, 30));
                    assert businessHours.getClosingTime().equals(LocalTime.of(17, 30));
                });
    }

    @Test
    void deleteBusinessHours_ReturnsTrue_WhenDeleted() {
        Long businessHoursId = 1L;
        when(businessHoursService.deleteBusinessHours(businessHoursId)).thenReturn(true);

        String mutation = """
                mutation {
                  deleteBusinessHours(id: 1)
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .path("deleteBusinessHours")
                .entity(Boolean.class)
                .isEqualTo(true);
    }

    @Test
    void businessHoursById_ReturnsNull_WhenNotExists() {
        Long businessHoursId = 999L;
        when(businessHoursService.getBusinessHoursById(businessHoursId)).thenReturn(Optional.empty());

        String query = """
                query {
                  businessHoursById(id: 999) {
                    dayOfWeek
                    openingTime
                    closingTime
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("businessHoursById")
                .valueIsNull();
    }

    @Test
    void deleteBusinessHours_ReturnsFalse_WhenNotFound() {
        Long businessHoursId = 999L;
        when(businessHoursService.deleteBusinessHours(businessHoursId)).thenReturn(false);

        String mutation = """
                mutation {
                  deleteBusinessHours(id: 999)
                }
                """;

        graphQlTester.document(mutation)
                .execute()
                .path("deleteBusinessHours")
                .entity(Boolean.class)
                .isEqualTo(false);
    }
}