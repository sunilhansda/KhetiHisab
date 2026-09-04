package com.example.demo.dto.job;

import com.example.demo.enums.JobStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CultivationJobResponse(

        Long jobId,

        Long customerId,
        String customerName,

        Long driverId,
        String driverName,

        LocalDate jobDate,

        BigDecimal amount,

        JobStatus status,

        String notes,

        LocalDateTime createdAt
) {
}
