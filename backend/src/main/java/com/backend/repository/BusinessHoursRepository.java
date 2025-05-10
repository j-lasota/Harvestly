package com.backend.repository;

import com.backend.model.BusinessHours;
import com.backend.model.DayOfWeek;
import com.backend.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessHoursRepository extends JpaRepository<BusinessHours, Long> {
    boolean existsByStoreAndDayOfWeek(Store store, DayOfWeek dayOfWeek);
}
