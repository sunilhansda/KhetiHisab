package com.example.demo.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DashboardResponse(

        LocalDate fromDate,

        LocalDate toDate,

        long totalCustomers,

        long totalDrivers,

        long totalJobs,

        long completedJobs,

        long pendingJobs,

        long inProgressJobs,

        long cancelledJobs,

        BigDecimal periodCultivationAmount,

        BigDecimal periodReceivedAmount,

        BigDecimal totalOutstandingAmount
) {
}