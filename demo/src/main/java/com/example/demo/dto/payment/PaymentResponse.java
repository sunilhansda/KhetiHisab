package com.example.demo.dto.payment;

import com.example.demo.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PaymentResponse(

        Long paymentId,

        Long customerId,

        String customerName,

        LocalDate paymentDate,

        BigDecimal amount,

        PaymentMethod paymentMethod,

        String notes,

        List<PaymentAllocationResponse> allocations,

        LocalDateTime createdAt
) {
}
