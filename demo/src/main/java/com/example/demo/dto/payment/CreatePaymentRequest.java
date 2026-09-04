package com.example.demo.dto.payment;

import com.example.demo.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreatePaymentRequest(

        @NotNull(message = "Customer ID is required")
        Long customerId,

        @NotNull(message = "Payment date is required")
        LocalDate paymentDate,

        @NotNull(message = "Payment amount is required")
        @DecimalMin(
                value = "0.01",
                message = "Payment amount must be greater than zero"
        )
        BigDecimal amount,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        @Size(max = 500)
        String notes,

        @NotEmpty(message = "At least one payment allocation is required")
        @Valid
        List<PaymentAllocationRequest> allocations
) {
}
