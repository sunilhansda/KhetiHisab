package com.example.demo.dto.customer;

import java.math.BigDecimal;

public record JobPaymentSummary(
        Long jobId,
        BigDecimal paidAmount
) {
}