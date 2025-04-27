package com.backend.controller;

import com.backend.model.BusinessHours;
import com.backend.model.DayOfWeek;
import com.backend.model.Shop;
import com.backend.service.BusinessHoursService;
import com.backend.service.ShopService;
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
    private final ShopService shopService;

    public BusinessHoursController(BusinessHoursService businessHoursService, ShopService shopService) {
        this.businessHoursService = businessHoursService;
        this.shopService = shopService;
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
    public BusinessHours createBusinessHours(@Argument Long shopId, @Argument DayOfWeek dayOfWeek,
                                             @Argument LocalTime openingTime, @Argument LocalTime closingTime) {
        Shop shop = shopService.getShopById(shopId).orElseThrow(() -> new IllegalArgumentException("Shop not found"));
        return businessHoursService.saveBusinessHours(new BusinessHours(shop, dayOfWeek, openingTime, closingTime));
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
