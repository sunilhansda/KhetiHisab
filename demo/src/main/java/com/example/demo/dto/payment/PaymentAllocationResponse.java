package com.example.demo.dto.payment;

import java.math.BigDecimal;

public record PaymentAllocationResponse(

        Long allocationId,

        Long jobId,

        BigDecimal amount
) {
}