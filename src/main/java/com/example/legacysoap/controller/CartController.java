package com.example.legacysoap.controller;

import com.example.legacysoap.dto.AddItemRequest;
import com.example.legacysoap.dto.CartItem;
import com.example.legacysoap.dto.CartItemResponse;
import com.example.legacysoap.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for shopping cart operations using Java 17/Spring Boot 3.x
 * Refactored from SOAP ShoppingCartEndpoint.addItem method
 */
@RestController
@RequestMapping("/api/v1/cart")
@Validated
@Tag(name = "Shopping Cart", description = "Shopping cart management operations")
public class CartController {
    
    private static final Logger log = LoggerFactory.getLogger(CartController.class);
    
    private final CartService cartService;
    
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    
    /**
     * Add item to cart - Refactored from SOAP addItem method
     * Original SOAP: @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AddItemRequest")
     * New REST: @PostMapping("/items")
     */
    @PostMapping("/items")
    @Operation(
        summary = "Add item to cart", 
        description = "Adds a new item to the cart or increments quantity if item already exists"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Item added successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CartItemResponse> addItem(@Valid @RequestBody AddItemRequest request) {
        log.info("Adding item to cart: productId={}, quantity={}", request.productId(), request.quantity());
        
        try {
            CartItem item = cartService.addItem(request.productId(), request.quantity());
            
            log.info("Item added successfully: productId={}, newQuantity={}", 
                item.productId(), item.quantity());
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CartItemResponse(item.productId(), item.quantity(), true));
                
        } catch (IllegalArgumentException e) {
            log.warn("Invalid add item request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(new CartItemResponse(request.productId(), 0, false));
        } catch (Exception e) {
            log.error("Unexpected error adding item to cart", e);
            return ResponseEntity.internalServerError()
                .body(new CartItemResponse(request.productId(), 0, false));
        }
    }
}
