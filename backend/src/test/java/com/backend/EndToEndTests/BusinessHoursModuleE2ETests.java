package com.backend.EndToEndTests;

import com.backend.model.BusinessHours;
import com.backend.model.DayOfWeek;
import com.backend.model.Store;
import com.backend.model.User;
import com.backend.repository.BusinessHoursRepository;
import com.backend.repository.StoreRepository;
import com.backend.repository.UserRepository;
import com.backend.service.BusinessHoursService;
import com.backend.service.StoreService;
import com.backend.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
public class BusinessHoursModuleE2ETests {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private BusinessHoursRepository businessHoursRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessHoursService businessHoursService;

    private Store testStore;
    private User storeOwnerUser;

    @BeforeEach
    public void setUp() {
        businessHoursRepository.deleteAll();
        storeRepository.deleteAll();
        userRepository.deleteAll();

        storeOwnerUser = new User(
                "2a6e8658-d6db-45d8-9131-e8f87b62ed75",
                "Store",
                "Owner",
                "storeowner@example.com",
                "123456789",
                0,
                "owner-img.jpg"
        );
        storeOwnerUser = userRepository.save(storeOwnerUser);

        testStore = new Store(
                storeOwnerUser,
                "Test Store",
                "A store for testing",
                45.0,
                45.0,
                "Test City",
                "123 Test Street",
                "store-img.jpg",
                "test-store"
        );
        testStore = storeRepository.save(testStore);
    }

    @AfterEach
    public void tearDown() {
        businessHoursRepository.deleteAll();
        storeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed75")
    public void testCompleteBusinessHoursLifecycle() {
        String createBusinessHoursMutation = """
            mutation {
                createBusinessHours(
                    storeId: %d,
                    dayOfWeek: MONDAY,
                    openingTime: "09:00:00",
                    closingTime: "17:00:00"
                ) {
                    id
                    dayOfWeek
                    openingTime
                    closingTime
                    store {
                        id
                        name
                    }
                }
            }
            """.formatted(testStore.getId());

        GraphQlTester.Response createResponse = graphQlTester
                .document(createBusinessHoursMutation)
                .execute();

        Long businessHoursId = createResponse
                .path("createBusinessHours.id")
                .entity(Long.class)
                .get();

        createResponse
                .path("createBusinessHours.dayOfWeek").entity(String.class).isEqualTo("MONDAY")
                .path("createBusinessHours.openingTime").entity(String.class).isEqualTo("09:00:00")
                .path("createBusinessHours.closingTime").entity(String.class).isEqualTo("17:00:00")
                .path("createBusinessHours.store.id").entity(String.class).isEqualTo(testStore.getId().toString());

        Optional<BusinessHours> savedBusinessHours = businessHoursService.getBusinessHoursById(businessHoursId);
        assertTrue(savedBusinessHours.isPresent());
        assertEquals(DayOfWeek.MONDAY, savedBusinessHours.get().getDayOfWeek());
        assertEquals(LocalTime.of(9, 0), savedBusinessHours.get().getOpeningTime());
        assertEquals(LocalTime.of(17, 0), savedBusinessHours.get().getClosingTime());

        String getBusinessHoursQuery = """
            query {
                businessHoursById(id: %d) {
                    id
                    dayOfWeek
                    openingTime
                    closingTime
                    store {
                        id
                        name
                    }
                }
            }
            """.formatted(businessHoursId);

        graphQlTester
                .document(getBusinessHoursQuery)
                .execute()
                .path("businessHoursById.id").entity(String.class).isEqualTo(businessHoursId.toString())
                .path("businessHoursById.dayOfWeek").entity(String.class).isEqualTo("MONDAY")
                .path("businessHoursById.openingTime").entity(String.class).isEqualTo("09:00:00")
                .path("businessHoursById.closingTime").entity(String.class).isEqualTo("17:00:00");

        String createAnotherBusinessHoursMutation = """
            mutation {
                createBusinessHours(
                    storeId: %d,
                    dayOfWeek: TUESDAY,
                    openingTime: "08:30:00",
                    closingTime: "16:30:00"
                ) {
                    id
                    dayOfWeek
                }
            }
            """.formatted(testStore.getId());

        graphQlTester
                .document(createAnotherBusinessHoursMutation)
                .execute()
                .path("createBusinessHours.dayOfWeek").entity(String.class).isEqualTo("TUESDAY");

        String getAllBusinessHoursQuery = """
            query {
                businessHours {
                    id
                    dayOfWeek
                    openingTime
                    closingTime
                    store {
                        id
                        name
                    }
                }
            }
            """;

        List<BusinessHours> allBusinessHours = businessHoursService.getAllBusinessHours();
        assertEquals(2, allBusinessHours.size());

        graphQlTester
                .document(getAllBusinessHoursQuery)
                .execute()
                .path("businessHours").entityList(BusinessHours.class).hasSize(2);

        String updateBusinessHoursMutation = """
            mutation {
                updateBusinessHours(
                    id: %d,
                    dayOfWeek: MONDAY,
                    openingTime: "10:00:00",
                    closingTime: "18:00:00"
                ) {
                    id
                    dayOfWeek
                    openingTime
                    closingTime
                }
            }
            """.formatted(businessHoursId);

        graphQlTester
                .document(updateBusinessHoursMutation)
                .execute()
                .path("updateBusinessHours.id").entity(String.class).isEqualTo(businessHoursId.toString())
                .path("updateBusinessHours.openingTime").entity(String.class).isEqualTo("10:00:00")
                .path("updateBusinessHours.closingTime").entity(String.class).isEqualTo("18:00:00");

        BusinessHours updatedBusinessHours = businessHoursService.getBusinessHoursById(businessHoursId).orElseThrow();
        assertEquals(LocalTime.of(10, 0), updatedBusinessHours.getOpeningTime());
        assertEquals(LocalTime.of(18, 0), updatedBusinessHours.getClosingTime());

        String deleteBusinessHoursMutation = """
            mutation {
                deleteBusinessHours(id: %d)
            }
            """.formatted(businessHoursId);

        graphQlTester
                .document(deleteBusinessHoursMutation)
                .execute()
                .path("deleteBusinessHours").entity(Boolean.class).isEqualTo(true);

        Optional<BusinessHours> deletedBusinessHours = businessHoursService.getBusinessHoursById(businessHoursId);
        assertTrue(deletedBusinessHours.isEmpty());
    }

    @Test
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed75")
    public void testPartialBusinessHoursUpdate() {
        String createBusinessHoursMutation = """
            mutation {
                createBusinessHours(
                    storeId: %d,
                    dayOfWeek: WEDNESDAY,
                    openingTime: "09:00:00",
                    closingTime: "17:00:00"
                ) {
                    id
                }
            }
            """.formatted(testStore.getId());

        Long businessHoursId = graphQlTester
                .document(createBusinessHoursMutation)
                .execute()
                .path("createBusinessHours.id")
                .entity(Long.class)
                .get();

        String updateOpeningTimeMutation = """
            mutation {
                updateBusinessHours(
                    id: %d,
                    openingTime: "10:30:00"
                ) {
                    id
                    dayOfWeek
                    openingTime
                    closingTime
                }
            }
            """.formatted(businessHoursId);

        graphQlTester
                .document(updateOpeningTimeMutation)
                .execute()
                .path("updateBusinessHours.dayOfWeek").entity(String.class).isEqualTo("WEDNESDAY")
                .path("updateBusinessHours.openingTime").entity(String.class).isEqualTo("10:30:00")
                .path("updateBusinessHours.closingTime").entity(String.class).isEqualTo("17:00:00");

        String updateClosingTimeMutation = """
            mutation {
                updateBusinessHours(
                    id: %d,
                    closingTime: "19:30:00"
                ) {
                    id
                    dayOfWeek
                    openingTime
                    closingTime
                }
            }
            """.formatted(businessHoursId);

        graphQlTester
                .document(updateClosingTimeMutation)
                .execute()
                .path("updateBusinessHours.openingTime").entity(String.class).isEqualTo("10:30:00")
                .path("updateBusinessHours.closingTime").entity(String.class).isEqualTo("19:30:00");

        String updateDayOfWeekMutation = """
            mutation {
                updateBusinessHours(
                    id: %d,
                    dayOfWeek: THURSDAY
                ) {
                    id
                    dayOfWeek
                    openingTime
                    closingTime
                }
            }
            """.formatted(businessHoursId);

        graphQlTester
                .document(updateDayOfWeekMutation)
                .execute()
                .path("updateBusinessHours.dayOfWeek").entity(String.class).isEqualTo("THURSDAY")
                .path("updateBusinessHours.openingTime").entity(String.class).isEqualTo("10:30:00")
                .path("updateBusinessHours.closingTime").entity(String.class).isEqualTo("19:30:00");
    }
    @Test
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed76")
    public void testCreateBusinessHoursWithDifferentUserId() {
        String createBusinessHoursMutation = """
            mutation {
                createBusinessHours(
                    storeId: %d,
                    dayOfWeek: THURSDAY,
                    openingTime: "10:00:00",
                    closingTime: "18:00:00"
                ) {
                    id
                }
            }
            """.formatted(testStore.getId());

        graphQlTester.document(createBusinessHoursMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });
    }
    @Test
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed75")
    public void testDuplicateBusinessHours() {
        String createFirstBusinessHoursMutation = """
            mutation {
                createBusinessHours(
                    storeId: %d,
                    dayOfWeek: FRIDAY,
                    openingTime: "09:00:00",
                    closingTime: "17:00:00"
                ) {
                    id
                }
            }
            """.formatted(testStore.getId());

        graphQlTester
                .document(createFirstBusinessHoursMutation)
                .execute()
                .path("createBusinessHours.id").entity(Long.class).isNotEqualTo(null);

        String createDuplicateBusinessHoursMutation = """
            mutation {
                createBusinessHours(
                    storeId: %d,
                    dayOfWeek: FRIDAY,
                    openingTime: "10:00:00",
                    closingTime: "18:00:00"
                ) {
                    id
                }
            }
            """.formatted(testStore.getId());

        graphQlTester.document(createDuplicateBusinessHoursMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });
    }

    @Test
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed75")
    public void testInvalidBusinessHoursTime() {
        String createInvalidTimeMutation = """
            mutation {
                createBusinessHours(
                    storeId: %d,
                    dayOfWeek: SATURDAY,
                    openingTime: "18:00:00",
                    closingTime: "09:00:00"
                ) {
                    id
                }
            }
            """.formatted(testStore.getId());

        graphQlTester.document(createInvalidTimeMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });

        String createValidBusinessHoursMutation = """
            mutation {
                createBusinessHours(
                    storeId: %d,
                    dayOfWeek: SATURDAY,
                    openingTime: "09:00:00",
                    closingTime: "18:00:00"
                ) {
                    id
                }
            }
            """.formatted(testStore.getId());

        Long businessHoursId = graphQlTester
                .document(createValidBusinessHoursMutation)
                .execute()
                .path("createBusinessHours.id")
                .entity(Long.class)
                .get();

        String updateInvalidTimeMutation = """
            mutation {
                updateBusinessHours(
                    id: %d,
                    openingTime: "19:00:00",
                    closingTime: "18:00:00"
                ) {
                    id
                }
            }
            """.formatted(businessHoursId);

        graphQlTester.document(updateInvalidTimeMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });
    }

    @Test
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed75")
    public void testInvalidBusinessHoursOperations() {
        String getNonExistentBusinessHoursQuery = """
            query {
                businessHoursById(id: 999999) {
                    id
                }
            }
            """;

        graphQlTester
                .document(getNonExistentBusinessHoursQuery)
                .execute()
                .path("businessHoursById")
                .valueIsNull();

        String updateNonExistentBusinessHoursMutation = """
            mutation {
                updateBusinessHours(
                    id: 999999,
                    dayOfWeek: SUNDAY,
                    openingTime: "10:00:00",
                    closingTime: "15:00:00"
                ) {
                    id
                }
            }
            """;

        graphQlTester.document(updateNonExistentBusinessHoursMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });

        String deleteNonExistentBusinessHoursMutation = """
            mutation {
                deleteBusinessHours(id: 999999)
            }
            """;

        graphQlTester
                .document(deleteNonExistentBusinessHoursMutation)
                .execute()
                .path("deleteBusinessHours").entity(Boolean.class).isEqualTo(false);
    }

    @Test
    public void testCreateBusinessHoursWithInvalidStore() {
        String createWithInvalidStoreMutation = """
            mutation {
                createBusinessHours(
                    storeId: 999999,
                    dayOfWeek: SUNDAY,
                    openingTime: "10:00:00",
                    closingTime: "16:00:00"
                ) {
                    id
                }
            }
            """;

        graphQlTester.document(createWithInvalidStoreMutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assert !errors.isEmpty();
                });
    }

    @Test
    @WithMockUser(username = "2a6e8658-d6db-45d8-9131-e8f87b62ed75")
    public void testCreateAndGetMultipleBusinessHoursForStore() {
        for (DayOfWeek day : new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY}) {
            String createBusinessHoursMutation = """
                mutation {
                    createBusinessHours(
                        storeId: %d,
                        dayOfWeek: %s,
                        openingTime: "09:00:00",
                        closingTime: "17:00:00"
                    ) {
                        id
                        dayOfWeek
                    }
                }
                """.formatted(testStore.getId(), day);

            graphQlTester
                    .document(createBusinessHoursMutation)
                    .execute()
                    .path("createBusinessHours.dayOfWeek").entity(String.class).isEqualTo(day.toString());
        }

        List<BusinessHours> storeBusinessHours = businessHoursService.getAllBusinessHours();
        assertEquals(3, storeBusinessHours.size());

        String getAllBusinessHoursQuery = """
            query {
                businessHours {
                    id
                    dayOfWeek
                    store {
                        id
                    }
                }
            }
            """;

        graphQlTester
                .document(getAllBusinessHoursQuery)
                .execute()
                .path("businessHours").entityList(BusinessHours.class).hasSize(3);
    }
}