package com.backend.controller;

import com.backend.model.Store;
import com.backend.model.StoreReport;
import com.backend.model.User;
import com.backend.service.StoreReportService;
import com.backend.service.StoreService;
import com.backend.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class StoreReportController {
    private final StoreReportService storeReportService;
    private final StoreService storeService;
    private final UserService userService;

    public StoreReportController(StoreReportService storeReportService, StoreService storeService, UserService userService) {
        this.storeReportService = storeReportService;
        this.storeService = storeService;
        this.userService = userService;
    }

    @QueryMapping
    public List<StoreReport> storeReports() {
        return storeReportService.getAllStoreReports();
    }

    @QueryMapping
    public StoreReport storeReportById(@Argument Long id) {
        return storeReportService.getStoreReportById(id)
                .orElseThrow(() -> new IllegalArgumentException("Store report not found with ID: " + id));
    }

    @MutationMapping
    public StoreReport reportStore(@Argument Long storeId, @Argument String userId) {
        Optional<Store> storeOpt = storeService.getStoreById(storeId);
        Optional<User> userOpt = userService.getUserById(userId);
        if (storeOpt.isEmpty()) {
            throw new IllegalArgumentException("Store not found with ID: " + storeId);
        }
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        return storeReportService.saveStoreReport(new StoreReport(storeOpt.get(), userOpt.get()));
    }

    @MutationMapping
    public boolean deleteStoreReport(@Argument Long id) {
        return storeReportService.deleteStoreReportById(id);
    }
}
