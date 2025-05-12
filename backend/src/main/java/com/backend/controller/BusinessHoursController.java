package com.backend.controller;

import com.backend.model.BusinessHours;
import com.backend.model.DayOfWeek;
import com.backend.model.Store;
import com.backend.service.BusinessHoursService;
import com.backend.service.StoreService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
public class BusinessHoursController {
    private final BusinessHoursService businessHoursService;
    private final StoreService storeService;

    public BusinessHoursController(BusinessHoursService businessHoursService, StoreService storeService) {
        this.businessHoursService = businessHoursService;
        this.storeService = storeService;
    }

    @QueryMapping
    public List<BusinessHours> businessHours() {
        return businessHoursService.getAllBusinessHours();
    }

    @QueryMapping
    public Optional<BusinessHours> businessHoursById(@Argument Long id) {
        return businessHoursService.getBusinessHoursById(id);
    }

    @MutationMapping
    public BusinessHours createBusinessHours(@Argument Long storeId, @Argument DayOfWeek dayOfWeek,
                                             @Argument LocalTime openingTime, @Argument LocalTime closingTime) {
        Store store = storeService.getStoreById(storeId).orElseThrow(() -> new IllegalArgumentException("Shop not found"));
        return businessHoursService.saveBusinessHours(new BusinessHours(store, dayOfWeek, openingTime, closingTime));
    }

    @MutationMapping
    public BusinessHours updateBusinessHours(@Argument Long id, @Argument DayOfWeek dayOfWeek,
                                             @Argument LocalTime openingTime, @Argument LocalTime closingTime) {
        return businessHoursService.updateBusinessHours(id, dayOfWeek, openingTime, closingTime);
    }

    @MutationMapping
    public Boolean deleteBusinessHours(@Argument Long id) {
        return businessHoursService.deleteBusinessHours(id);
    }
}
