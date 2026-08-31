package com.example.demo.dto.customer;

import java.time.LocalDateTime;

public record CustomerResponse(
        Long customerId,
        String name,
        String phone,
        String address,
        LocalDateTime createdAt
) {
}
