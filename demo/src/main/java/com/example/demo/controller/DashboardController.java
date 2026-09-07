package com.example.demo.controller;

import com.example.demo.dto.dashboard.DashboardResponse;
import com.example.demo.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(
        name = "Dashboard",
        description = "Business overview and dashboard APIs"
)
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(
            summary = "Get dashboard",
            description = """
                    Returns business summary for the specified period.
                    If no dates are supplied, the dashboard returns
                    lifetime job/payment activity.
                    Current outstanding amount is always calculated
                    across all jobs and payments.
                    """
    )
    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(

            @Parameter(
                    description = "Start date of the reporting period",
                    example = "2026-09-01"
            )
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @Parameter(
                    description = "End date of the reporting period",
                    example = "2026-09-30"
            )
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate) {

        return ResponseEntity.ok(
                dashboardService.getDashboard(
                        fromDate,
                        toDate
                )
        );
    }
}