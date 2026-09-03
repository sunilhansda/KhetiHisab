package com.example.demo.dto.job;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateCultivationJobRequest(

        @NotNull(message = "Customer ID is required")
        Long customerId,

        @NotNull(message = "Driver ID is required")
        Long driverId,

        @NotNull(message = "Job date is required")
        LocalDate jobDate,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01")
        BigDecimal amount,

        @Size(max = 500)
        String notes
) {
}