package com.backend.service;

import com.backend.model.BusinessHours;
import com.backend.model.DayOfWeek;
import com.backend.repository.BusinessHoursRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class BusinessHoursService {
    private final BusinessHoursRepository businessHoursRepository;

    public BusinessHoursService(BusinessHoursRepository businessHoursRepository) {
        this.businessHoursRepository = businessHoursRepository;
    }

    public BusinessHours saveBusinessHours(BusinessHours businessHours) {
        if (businessHoursRepository.existsByShopAndDayOfWeek(businessHours.getShop(), businessHours.getDayOfWeek())) {
            throw new IllegalArgumentException("BusinessHours already exists for the given shop and day of week.");
        }
        if (!businessHours.getOpeningTime().isBefore(businessHours.getClosingTime())) {
            throw new IllegalArgumentException("Opening time must be before closing time.");
        }
        return businessHoursRepository.save(businessHours);
    }

    public BusinessHours updateBusinessHours(Long id, DayOfWeek dayOfWeek, LocalTime openingTime, LocalTime closingTime) {
        BusinessHours businessHours = businessHoursRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("BusinessHours not found"));
        if (dayOfWeek != null) {
            businessHours.setDayOfWeek(dayOfWeek);
        }
        if (openingTime != null) {
            businessHours.setOpeningTime(openingTime);
        }
        if (closingTime != null) {
            businessHours.setClosingTime(closingTime);
        }
        if (!businessHours.getOpeningTime().isBefore(businessHours.getClosingTime())) {
            throw new IllegalArgumentException("Opening time must be before closing time.");
        }
        return businessHoursRepository.save(businessHours);
    }

    public Boolean deleteBusinessHours(Long id) {
        if (getBusinessHoursById(id).isPresent()) {
            businessHoursRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<BusinessHours> getBusinessHoursById(Long id) {
        return businessHoursRepository.findById(id);
    }

    public List<BusinessHours> getAllBusinessHours() {
        return businessHoursRepository.findAll();
    }
}
