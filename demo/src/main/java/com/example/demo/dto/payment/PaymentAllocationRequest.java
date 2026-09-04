package com.example.demo.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentAllocationRequest(

        @Schema(description = "Cultivation job to which payment is allocated", example = "101")
        @NotNull(message = "Job ID is required")
        Long jobId,

        @Schema(description = "Amount allocated to this job", example = "2000.00")
        @NotNull(message = "Allocation amount is required")
        @DecimalMin(value = "0.01", message = "Allocation amount must be greater than zero")
        BigDecimal amount
) {
}