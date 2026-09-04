package com.example.demo.dto.payment;

import java.math.BigDecimal;

public record CustomerBalanceResponse(

        Long customerId,

        String customerName,

        BigDecimal totalAmount,

        BigDecimal paidAmount,

        BigDecimal dueAmount
) {
}
