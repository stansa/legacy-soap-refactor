// Modern Java 17 DTOs using Records for the REST API
package com.example.legacysoap.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Request to add an item to the cart
 * Modern Java 17 record equivalent of SOAP AddItemRequest
 */
public record AddItemRequest(
    @NotBlank(message = "Product ID cannot be blank")
    String productId,
    
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity
) {}

/**
 * Response for add item operation
 * Modern Java 17 record equivalent of SOAP AddItemResponse
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AddItemResponse(
    boolean success,
    String errorMessage
) {}

/**
 * Request to update quantity of an item
 * Modern Java 17 record equivalent of SOAP UpdateQuantityRequest
 */
public record UpdateQuantityRequest(
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity
) {}

/**
 * Response for update quantity operation
 * Modern Java 17 record equivalent of SOAP UpdateQuantityResponse
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateQuantityResponse(
    boolean success,
    String errorMessage
) {}

/**
 * Response for remove item operation
 * Modern Java 17 record equivalent of SOAP RemoveItemResponse
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RemoveItemResponse(
    boolean success,
    String errorMessage
) {}

/**
 * Response for clear cart operation
 * Modern Java 17 record equivalent of SOAP ClearCartResponse
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ClearCartResponse(
    boolean success,
    String errorMessage
) {}

/**
 * Cart item representation
 * Modern Java 17 record equivalent of SOAP CartItems
 */
public record CartItem(
    @NotBlank String productId,
    @Min(1) Integer quantity
) {}

/**
 * Response for get cart operation
 * Modern Java 17 record equivalent of SOAP GetCartResponse
 */
public record GetCartResponse(
    List<CartItem> cartItems
) {}

/**
 * Error response for REST API
 * Provides more detailed error information than SOAP equivalents
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    String error,
    String message,
    int status,
    String path,
    long timestamp
) {
    public static ErrorResponse of(String error, String message, int status, String path) {
        return new ErrorResponse(error, message, status, path, System.currentTimeMillis());
    }
}