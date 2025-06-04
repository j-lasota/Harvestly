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

    @Autowired
    private StoreService storeService;

    @Autowired
    private UserService userService;

    private Store testStore;
    private User storeOwnerUser;

    @BeforeEach
    public void setUp() {
        // Clean up existing data
        businessHoursRepository.deleteAll();
        storeRepository.deleteAll();
        userRepository.deleteAll();

        // Create test store owner
        storeOwnerUser = new User(
                UUID.randomUUID().toString(),
                "Store",
                "Owner",
                "storeowner@example.com",
                "123456789",
                0,
                "owner-img.jpg"
        );
        storeOwnerUser = userRepository.save(storeOwnerUser);

        // Create test store
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
        // Clean up all test data
        businessHoursRepository.deleteAll();
        storeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    public void testCompleteBusinessHoursLifecycle() {
        // 1. Create new business hours through GraphQL
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

        // Execute the mutation and verify the response
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

        // 2. Verify business hours exist in database
        Optional<BusinessHours> savedBusinessHours = businessHoursService.getBusinessHoursById(businessHoursId);
        assertTrue(savedBusinessHours.isPresent());
        assertEquals(DayOfWeek.MONDAY, savedBusinessHours.get().getDayOfWeek());
        assertEquals(LocalTime.of(9, 0), savedBusinessHours.get().getOpeningTime());
        assertEquals(LocalTime.of(17, 0), savedBusinessHours.get().getClosingTime());

        // 3. Get the business hours by ID using GraphQL
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

        // 4. Create another set of business hours for a different day
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

        // 5. Get all business hours
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

        // 6. Update the business hours
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

        // 7. Verify update in the database
        BusinessHours updatedBusinessHours = businessHoursService.getBusinessHoursById(businessHoursId).orElseThrow();
        assertEquals(LocalTime.of(10, 0), updatedBusinessHours.getOpeningTime());
        assertEquals(LocalTime.of(18, 0), updatedBusinessHours.getClosingTime());

        // 8. Delete the business hours
        String deleteBusinessHoursMutation = """
            mutation {
                deleteBusinessHours(id: %d)
            }
            """.formatted(businessHoursId);

        graphQlTester
                .document(deleteBusinessHoursMutation)
                .execute()
                .path("deleteBusinessHours").entity(Boolean.class).isEqualTo(true);

        // 9. Verify business hours were deleted
        Optional<BusinessHours> deletedBusinessHours = businessHoursService.getBusinessHoursById(businessHoursId);
        assertTrue(deletedBusinessHours.isEmpty());
    }

    @Test
    public void testPartialBusinessHoursUpdate() {
        // First create business hours
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

        // Update only the opening time
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

        // Update only the closing time
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

        // Update only the day of week
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
    public void testDuplicateBusinessHours() {
        // Create initial business hours
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

        // Try to create duplicate business hours for the same day and store
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
    public void testInvalidBusinessHoursTime() {
        // Try to create business hours with closing time before opening time
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

        // Create valid business hours
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

        // Try to update with invalid time (closing before opening)
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
    public void testInvalidBusinessHoursOperations() {
        // Try to get non-existent business hours by ID
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

        // Try to update non-existent business hours
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

        // Try to delete non-existent business hours
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
        // Try to create business hours with non-existent store
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
    public void testCreateAndGetMultipleBusinessHoursForStore() {
        // Create business hours for multiple days
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

        // Verify all business hours for the store are returned
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