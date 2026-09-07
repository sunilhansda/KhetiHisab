package com.example.demo.dto.driver;

import com.example.demo.enums.JobStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DriverJobResponse(
        Long jobId,
        Long customerId,
        String customerName,
        String addressCode,
        LocalDate jobDate,
        BigDecimal amount,
        JobStatus status
) {
}