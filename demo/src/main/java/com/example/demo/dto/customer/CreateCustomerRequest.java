package com.example.demo.dto.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 100)
        String name,

        @Pattern(
                regexp = "^[0-9]{10}$",
                message = "Phone number must contain exactly 10 digits"
        )
        String phone,

        @NotBlank(message = "Location code is required")
        @Size(max = 20)
        String locationCode,

        @Size(max = 500)
        String address
) {
}