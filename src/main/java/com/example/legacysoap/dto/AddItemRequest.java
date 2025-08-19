package com.example.legacysoap.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for adding items to cart using Java 17 record
 */
public record AddItemRequest(
    @NotBlank(message = "Product ID is required")
    String productId,
    
    @Min(value = 1, message = "Quantity must be at least 1")
    int quantity
) {}
