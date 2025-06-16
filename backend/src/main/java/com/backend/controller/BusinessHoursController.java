package com.backend.controller;

import com.backend.model.BusinessHours;
import com.backend.model.BusinessHoursInput;
import com.backend.model.DayOfWeek;
import com.backend.model.Store;
import com.backend.service.BusinessHoursService;
import com.backend.service.StoreService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
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

    /**
     * Fetch all business hours.
     *
     * @return a list of all business hours
     */
    @QueryMapping
    public List<BusinessHours> businessHours() {
        return businessHoursService.getAllBusinessHours();
    }

    /**
     * Fetch business hours by its ID.
     *
     * @param id the ID of the business hours
     * @return an Optional containing the BusinessHours if found, or empty if not found
     */
    @QueryMapping
    public Optional<BusinessHours> businessHoursById(@Argument Long id) {
        return businessHoursService.getBusinessHoursById(id);
    }

    /**
     * Create new business hours.
     *
     * @param storeId the ID of the store to associate with the business hours
     * @param dayOfWeek the day of the week for the business hours
     * @param openingTime the opening time for the business hours
     * @param closingTime the closing time for the business hours
     * @return the created BusinessHours object
     * @throws IllegalArgumentException if the store is not found
     */

    @MutationMapping
    @PreAuthorize("hasAuthority('SCOPE_manage:all') or @businessHoursSecurity.isStoreOwner(authentication, #storeId)")
    public BusinessHours createBusinessHours(@Argument Long storeId, @Argument DayOfWeek dayOfWeek,
                                             @Argument LocalTime openingTime, @Argument LocalTime closingTime) {
        Store store = storeService.getStoreById(storeId).orElseThrow(() -> new IllegalArgumentException("Shop not found"));
        return businessHoursService.saveBusinessHours(new BusinessHours(store, dayOfWeek, openingTime, closingTime));
    }

    /**
     * Create multiple business hours for a store.
     *
     * @param storeId the ID of the store to associate with the business hours
     * @param businessHoursList a list of BusinessHoursInput objects containing the details of the business hours
     * @return a list of created BusinessHours objects
     * @throws IllegalArgumentException if the store is not found
     */
    @MutationMapping
    @PreAuthorize("hasAuthority('SCOPE_manage:all') or @businessHoursSecurity.isStoreOwner(authentication, #storeId)")
    public List<BusinessHours> createMultipleBusinessHours(@Argument Long storeId, @Argument List<BusinessHoursInput> businessHoursList) {
        Store store = storeService.getStoreById(storeId).orElseThrow(() -> new IllegalArgumentException("Store not found"));
        return businessHoursService.saveMultipleBusinessHours(store, businessHoursList);
    }

    /**
     * Update existing business hours.
     *
     * @param id the ID of the business hours to update
     * @param dayOfWeek the new day of the week for the business hours
     * @param openingTime the new opening time for the business hours
     * @param closingTime the new closing time for the business hours
     * @return the updated BusinessHours object
     */
    @MutationMapping
    @PreAuthorize("hasAuthority('SCOPE_manage:all') or @businessHoursSecurity.isStoreOwnerByBHId(authentication, #id)")
    public BusinessHours updateBusinessHours(@Argument Long id, @Argument DayOfWeek dayOfWeek,
                                             @Argument LocalTime openingTime, @Argument LocalTime closingTime) {
        return businessHoursService.updateBusinessHours(id, dayOfWeek, openingTime, closingTime);
    }

    /**
     * Delete business hours by its ID.
     *
     * @param id the ID of the business hours to delete
     * @return true if the deletion was successful, false otherwise
     */
    @MutationMapping
    @PreAuthorize("hasAuthority('SCOPE_manage:all') or @businessHoursSecurity.isStoreOwnerByBHId(authentication, #id)")
    public Boolean deleteBusinessHours(@Argument Long id) {
        return businessHoursService.deleteBusinessHours(id);
    }
}
