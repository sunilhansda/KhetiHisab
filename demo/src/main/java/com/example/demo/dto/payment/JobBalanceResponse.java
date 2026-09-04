package com.example.demo.dto.payment;

import java.math.BigDecimal;

public record JobBalanceResponse(

        Long jobId,

        BigDecimal totalAmount,

        BigDecimal paidAmount,

        BigDecimal dueAmount
) {
}
