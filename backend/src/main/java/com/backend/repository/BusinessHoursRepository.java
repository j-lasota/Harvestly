package com.backend.repository;

import com.backend.model.BusinessHours;
import com.backend.model.DayOfWeek;
import com.backend.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessHoursRepository extends JpaRepository<BusinessHours, Long> {
    boolean existsByShopAndDayOfWeek(Shop shop, DayOfWeek dayOfWeek);
}
