package com.example.demo.dto.customer;

import java.math.BigDecimal;

public record CustomerJobDueResponse(

        Long jobId,

        BigDecimal jobAmount,

        BigDecimal paidAmount,

        BigDecimal dueAmount
) {
}
