package com.backend.IntegrationTests;

import com.backend.model.BusinessHours;
import com.backend.model.DayOfWeek;
import com.backend.model.Shop;
import com.backend.model.User;
import com.backend.repository.BusinessHoursRepository;
import com.backend.repository.ShopRepository;
import com.backend.repository.UserRepository;
import com.backend.service.BusinessHoursService;
import com.backend.service.ShopService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BusinessHoursServiceTests {

    @Autowired
    private BusinessHoursService businessHoursService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private BusinessHoursRepository businessHoursRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserRepository userRepository;

    private Shop savedShop;

    private Shop createTestShop() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("johndoe@example.com");
        user.setPassword("hashedPassword");
        user.setPhoneNumber("+48123456789");
        user.setTier(1);
        user = userRepository.save(user);

        Shop shop = new Shop();
        shop.setName("Test Shop for Hours");
        shop.setUser(user);
        shop.setDescription("Hours Description");
        shop.setLatitude(12.34);
        shop.setLongitude(56.78);
        shop.setCity("TestCity");
        shop.setAddress("TestAddress 1");
        shop.setImageUrl("http://test.com/image.png");
        return shop;
    }

    @BeforeEach
    void setUp() {
        businessHoursRepository.deleteAll();
        shopRepository.deleteAll();
        userRepository.deleteAll();

        savedShop = shopService.saveShop(createTestShop());
    }

    @Test
    void testSaveBusinessHours() {
        BusinessHours businessHours = new BusinessHours(savedShop, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));

        BusinessHours saved = businessHoursService.saveBusinessHours(businessHours);

        assertNotNull(saved.getId());
        assertEquals(DayOfWeek.MONDAY, saved.getDayOfWeek());
    }

    @Test
    void testGetBusinessHoursById_Found() {
        BusinessHours businessHours = businessHoursRepository.save(
                new BusinessHours(savedShop, DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(18, 0))
        );

        Optional<BusinessHours> found = businessHoursService.getBusinessHoursById(businessHours.getId());

        assertTrue(found.isPresent());
        assertEquals(DayOfWeek.TUESDAY, found.get().getDayOfWeek());
    }

    @Test
    void testGetBusinessHoursById_NotFound() {
        Optional<BusinessHours> found = businessHoursService.getBusinessHoursById(999L);

        assertFalse(found.isPresent());
    }

    @Test
    void testGetAllBusinessHours() {
        businessHoursRepository.save(new BusinessHours(savedShop, DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(16, 0)));
        businessHoursRepository.save(new BusinessHours(savedShop, DayOfWeek.THURSDAY, LocalTime.of(9, 0), LocalTime.of(17, 0)));

        List<BusinessHours> all = businessHoursService.getAllBusinessHours();

        assertEquals(2, all.size());
    }

    @Test
    void testUpdateBusinessHours_Success() {
        BusinessHours businessHours = businessHoursRepository.save(
                new BusinessHours(savedShop, DayOfWeek.FRIDAY, LocalTime.of(8, 30), LocalTime.of(15, 30))
        );

        BusinessHours updated = businessHoursService.updateBusinessHours(
                businessHours.getId(),
                DayOfWeek.SATURDAY,
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)
        );

        assertEquals(DayOfWeek.SATURDAY, updated.getDayOfWeek());
        assertEquals(LocalTime.of(10, 0), updated.getOpeningTime());
        assertEquals(LocalTime.of(18, 0), updated.getClosingTime());
    }

    @Test
    void testUpdateBusinessHours_NotFound() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            businessHoursService.updateBusinessHours(999L, DayOfWeek.SUNDAY, LocalTime.of(10, 0), LocalTime.of(16, 0));
        });

        assertEquals("BusinessHours not found", exception.getMessage());
    }

    @Test
    void testDeleteBusinessHours_Exists() {
        BusinessHours businessHours = businessHoursRepository.save(
                new BusinessHours(savedShop, DayOfWeek.SUNDAY, LocalTime.of(12, 0), LocalTime.of(20, 0))
        );

        Boolean result = businessHoursService.deleteBusinessHours(businessHours.getId());

        assertTrue(result);
        assertFalse(businessHoursRepository.findById(businessHours.getId()).isPresent());
    }

    @Test
    void testDeleteBusinessHours_NotExists() {
        Boolean result = businessHoursService.deleteBusinessHours(404L);

        assertFalse(result);
    }
}
