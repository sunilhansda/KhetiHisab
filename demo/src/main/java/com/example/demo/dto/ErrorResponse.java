package com.example.demo.dto;

public record ErrorResponse(
        String code,
        String message
) {
}