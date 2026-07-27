package com.techstore.controller;

import com.techstore.dto.reponse.DashboardStatsResponse;
import com.techstore.service.DashboardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public DashboardStatsResponse getStats() {
        return dashboardService.getDashboardStats();
    }
}
