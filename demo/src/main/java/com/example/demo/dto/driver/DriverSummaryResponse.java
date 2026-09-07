package com.example.demo.dto.driver;

import java.math.BigDecimal;

public record DriverSummaryResponse(

        Long driverId,

        String driverName,

        long totalJobs,

        long completedJobs,

        long pendingJobs,

        long inProgressJobs,

        long cancelledJobs,

        BigDecimal totalJobAmount
) {
}
