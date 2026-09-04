package com.example.demo.dto.payment;

import com.example.demo.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreatePaymentRequest(

        @Schema(description = "Customer receiving the payment", example = "1")
        @NotNull(message = "Customer ID is required")
        Long customerId,

        @Schema(description = "Date on which payment was received", example = "2026-09-04")
        @NotNull(message = "Payment date is required")
        LocalDate paymentDate,

        @Schema(description = "Total payment received", example = "3000.00")
        @NotNull(message = "Payment amount is required")
        @DecimalMin(value = "0.01", message = "Payment amount must be greater than zero")
        BigDecimal amount,

        @Schema(description = "Method used to make the payment", example = "CASH")
        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        @Schema(description = "Optional payment notes", example = "Payment received from Guru")
        @Size(max = 500)
        String notes,

        @NotEmpty(message = "At least one payment allocation is required")
        @Valid
        List<PaymentAllocationRequest> allocations
) {
}
