package com.example.legacysoap.rest.controller;

import com.example.legacysoap.rest.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// NOTE: For Java 17/Spring Boot 3.x, these would be:
// import org.springframework.validation.annotation.Validated;
// import jakarta.validation.Valid;
// import jakarta.validation.constraints.NotBlank;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Controller for Shopping Cart operations.
 * 
 * This is a sample implementation demonstrating how to migrate
 * from SOAP (ShoppingCartEndpoint) to REST architecture.
 * 
 * Key improvements over SOAP version:
 * - Thread-safe using ConcurrentHashMap
 * - Proper HTTP status codes
 * - Input validation with Bean Validation (when using Spring Boot 3.x)
 * - RESTful endpoint design
 * - JSON request/response instead of XML
 * 
 * NOTE: This version compiles with Java 8/Spring Boot 2.x
 * For production use with Java 17/Spring Boot 3.x, enable @Validated annotation
 */
@RestController
@RequestMapping("/api/cart")
// @Validated // Enable in Spring Boot 3.x
public class ShoppingCartController {
    
    // Thread-safe storage - addresses the concurrency issue in the SOAP version
    private final Map<String, Integer> cart = new ConcurrentHashMap<>();
    
    /**
     * Add item to cart
     * Replaces: PayloadRoot "AddItemRequest" in SOAP endpoint
     */
    @PostMapping("/items")
    public ResponseEntity<ApiResponse> addItem(/* @Valid */ @RequestBody AddItemRequest request) {
        try {
            // Thread-safe operation using ConcurrentHashMap
            cart.merge(request.getProductId(), request.getQuantity(), Integer::sum);
            
            return ResponseEntity.ok(
                ApiResponse.success("Added " + request.getQuantity() + " units of " + request.getProductId())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("Failed to add item: " + e.getMessage()));
        }
    }
    
    /**
     * Get cart contents
     * Replaces: PayloadRoot "GetCartRequest" in SOAP endpoint
     */
    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        try {
            List<CartItem> items = cart.entrySet().stream()
                .map(entry -> new CartItem(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(new CartResponse(items));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CartResponse());
        }
    }
    
    /**
     * Remove item from cart
     * Replaces: PayloadRoot "RemoveItemRequest" in SOAP endpoint
     */
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse> removeItem(
            @PathVariable /* @NotBlank(message = "Product ID is required") */ String productId) {
        try {
            Integer removedQuantity = cart.remove(productId);
            
            if (removedQuantity != null) {
                return ResponseEntity.ok(
                    ApiResponse.success("Removed " + productId + " from cart")
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure("Product not found in cart"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("Failed to remove item: " + e.getMessage()));
        }
    }
    
    /**
     * Update item quantity
     * Replaces: PayloadRoot "UpdateQuantityRequest" in SOAP endpoint
     */
    @PutMapping("/items/{productId}")
    public ResponseEntity<ApiResponse> updateQuantity(
            @PathVariable /* @NotBlank(message = "Product ID is required") */ String productId,
            /* @Valid */ @RequestBody UpdateQuantityRequest request) {
        try {
            if (request.getQuantity() == 0) {
                // If quantity is 0, remove the item
                return removeItem(productId);
            }
            
            if (cart.containsKey(productId)) {
                cart.put(productId, request.getQuantity());
                return ResponseEntity.ok(
                    ApiResponse.success("Updated quantity for " + productId + " to " + request.getQuantity())
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure("Product not found in cart"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("Failed to update quantity: " + e.getMessage()));
        }
    }
    
    /**
     * Clear entire cart
     * Replaces: PayloadRoot "ClearCartRequest" in SOAP endpoint
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse> clearCart() {
        try {
            int itemCount = cart.size();
            cart.clear();
            
            return ResponseEntity.ok(
                ApiResponse.success("Cleared cart (" + itemCount + " items removed)")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("Failed to clear cart: " + e.getMessage()));
        }
    }
    
    /**
     * Checkout cart
     * Implements the missing checkout functionality from SOAP endpoint
     */
    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout() {
        try {
            if (cart.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CheckoutResponse.failure("Cart is empty"));
            }
            
            // Calculate total (assuming $1.00 per item for demo)
            double total = cart.values().stream().mapToInt(Integer::intValue).sum() * 1.00;
            
            // Generate order ID
            String orderId = "ORDER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            
            // Clear cart after successful checkout
            cart.clear();
            
            return ResponseEntity.ok(CheckoutResponse.success(total, orderId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CheckoutResponse.failure("Checkout failed: " + e.getMessage()));
        }
    }
    
    /**
     * Health check endpoint for monitoring
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse> health() {
        return ResponseEntity.ok(ApiResponse.success("Cart service is healthy"));
    }
}