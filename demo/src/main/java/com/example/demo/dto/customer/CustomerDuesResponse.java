package com.example.demo.dto.customer;

import java.math.BigDecimal;
import java.util.List;

public record CustomerDuesResponse(

        Long customerId,

        String customerName,

        BigDecimal totalAmount,

        BigDecimal paidAmount,

        BigDecimal dueAmount,

        List<CustomerJobDueResponse> jobs
) {
}
