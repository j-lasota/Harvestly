package com.backend.controller;

import com.backend.model.OpinionReport;
import com.backend.service.OpinionReportService;
import com.backend.service.OpinionService;
import com.backend.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class OpinionReportController {
    private final OpinionReportService opinionReportService;
    private final UserService userService;
    private final OpinionService opinionService;

    public OpinionReportController(OpinionReportService opinionReportService, UserService userService, OpinionService opinionService) {
        this.opinionReportService = opinionReportService;
        this.userService = userService;
        this.opinionService = opinionService;
    }

    @QueryMapping
    public List<OpinionReport> opinionReports() {
        return opinionReportService.getAllOpinionReports();
    }

    @QueryMapping
    public OpinionReport opinionReportById(@Argument Long id) {
        return opinionReportService.getOpinionReportById(id)
                .orElseThrow(() -> new IllegalArgumentException("Opinion report not found with ID: " + id));
    }

    @MutationMapping
    public OpinionReport reportOpinion(@Argument Long opinionId, @Argument String userId) {
        var opinionOpt = opinionService.getOpinionById(opinionId);
        var userOpt = userService.getUserById(userId);

        if (opinionOpt.isEmpty()) {
            throw new IllegalArgumentException("Opinion not found with ID: " + opinionId);
        }
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        return opinionReportService.saveOpinionReport(new OpinionReport(opinionOpt.get(), userOpt.get()));
    }

    @MutationMapping
    public boolean deleteOpinionReport(@Argument Long id) {
        return opinionReportService.deleteOpinionReportById(id);
    }
}
