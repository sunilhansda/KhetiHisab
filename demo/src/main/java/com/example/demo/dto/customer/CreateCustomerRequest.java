package com.example.demo.dto.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(

        @Schema(description = "Customer's full name", example = "Guru")
        @NotBlank(message = "Name is required")
        @Size(max = 100)
        String name,

        @Schema(description = "Customer phone number", example = "9876543210")
        @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must contain exactly 10 digits")
        String phone,

        @Schema(description = "Address code used to identify the customer's area", example = "ND")
        @NotBlank(message = "Location code is required")
        @Size(max = 20)
        String locationCode,

        @Size(max = 500)
        String address
) {
}