package com.example.legacysoap.dto;

/**
 * Cart item domain object using Java 17 record
 */
public record CartItem(
    String productId,
    int quantity
) {}
