package com.example.legacysoap.dto;

/**
 * Response DTO for cart item operations using Java 17 record
 */
public record CartItemResponse(
    String productId,
    int quantity,
    boolean success
) {}
