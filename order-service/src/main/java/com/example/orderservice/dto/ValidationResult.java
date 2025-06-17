package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationResult {
    private String orderId;
    private boolean success;

    public static ValidationResult success(String orderId) {
        return new ValidationResult(orderId, true);
    }
    public static ValidationResult failure(String orderId) {
        return new ValidationResult(orderId, false);
    }
}