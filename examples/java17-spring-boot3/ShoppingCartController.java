// Example REST Controller for Java 17/Spring Boot 3.x
package com.example.legacysoap.rest;

import com.example.legacysoap.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller equivalent of the SOAP ShoppingCartEndpoint
 * Demonstrates modern Spring Boot 3.x patterns and Java 17 features
 */
@RestController
@RequestMapping("/api/v1/cart")
@CrossOrigin(origins = "*") // Configure as needed for your frontend
public class ShoppingCartController {
    
    // In-memory cart storage (same as SOAP version for consistency)
    private final Map<String, Integer> cart = new HashMap<>();
    
    /**
     * Add item to cart
     * SOAP equivalent: @PayloadRoot(localPart = "AddItemRequest")
     */
    @PostMapping("/items")
    public ResponseEntity<AddItemResponse> addItem(@RequestBody @Valid AddItemRequest request) {
        var currentQuantity = cart.getOrDefault(request.productId(), 0);
        cart.put(request.productId(), currentQuantity + request.quantity());
        
        return ResponseEntity.ok(new AddItemResponse(true, null));
    }
    
    /**
     * Get cart contents
     * SOAP equivalent: @PayloadRoot(localPart = "GetCartRequest")
     */
    @GetMapping
    public ResponseEntity<GetCartResponse> getCart() {
        var cartItems = cart.entrySet().stream()
            .map(entry -> new CartItem(entry.getKey(), entry.getValue()))
            .toList();
        
        return ResponseEntity.ok(new GetCartResponse(cartItems));
    }
    
    /**
     * Remove item from cart using path variable (RESTful approach)
     * SOAP equivalent: @PayloadRoot(localPart = "RemoveItemRequest")
     */
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<RemoveItemResponse> removeItem(
            @PathVariable @NotBlank String productId) {
        
        if (cart.containsKey(productId)) {
            cart.remove(productId);
            return ResponseEntity.ok(new RemoveItemResponse(true, null));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Update item quantity using path variable and request body
     * SOAP equivalent: @PayloadRoot(localPart = "UpdateQuantityRequest")
     */
    @PutMapping("/items/{productId}")
    public ResponseEntity<UpdateQuantityResponse> updateQuantity(
            @PathVariable @NotBlank String productId,
            @RequestBody @Valid UpdateQuantityRequest request) {
        
        if (cart.containsKey(productId)) {
            cart.put(productId, request.quantity());
            return ResponseEntity.ok(new UpdateQuantityResponse(true, null));
        } else {
            return ResponseEntity.notFound()
                .body(new UpdateQuantityResponse(false, "Product not found in cart"));
        }
    }
    
    /**
     * Clear entire cart
     * SOAP equivalent: @PayloadRoot(localPart = "ClearCartRequest")
     */
    @DeleteMapping
    public ResponseEntity<ClearCartResponse> clearCart() {
        cart.clear();
        return ResponseEntity.ok(new ClearCartResponse(true, null));
    }
    
    /**
     * Additional REST-specific endpoint: Get specific item quantity
     * This demonstrates RESTful resource access not available in SOAP
     */
    @GetMapping("/items/{productId}")
    public ResponseEntity<CartItem> getItem(@PathVariable @NotBlank String productId) {
        var quantity = cart.get(productId);
        if (quantity != null) {
            return ResponseEntity.ok(new CartItem(productId, quantity));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}