package com.example.demo.dto.dashboard;

import java.math.BigDecimal;

public record DashboardJobSummary(

        long totalJobs,

        long completedJobs,

        long pendingJobs,

        long inProgressJobs,

        long cancelledJobs,

        BigDecimal totalAmount
) {
}