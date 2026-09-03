package com.example.demo.dto.driver;

import java.time.LocalDateTime;

public record DriverResponse(
        Long driverId,
        String name,
        String phone,
        String address,
        Boolean active,
        LocalDateTime createdAt
) {
}
