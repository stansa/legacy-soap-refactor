package com.example.legacysoap.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {
    
    private Map<String, Integer> cart = new HashMap<>(); // In-memory cart

    @PostMapping("/items")
    public ResponseEntity<AddItemResponse> addItem(@RequestBody AddItemRequest request) {
        cart.put(request.getProductId(), cart.getOrDefault(request.getProductId(), 0) + request.getQuantity());
        AddItemResponse response = new AddItemResponse();
        response.setSuccess(true);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<GetCartResponse> getCart() {
        GetCartResponse response = new GetCartResponse();
        cart.forEach((productId, quantity) -> {
            GetCartResponse.CartItem item = new GetCartResponse.CartItem();
            item.setProductId(productId);
            item.setQuantity(quantity);
            response.getCartItems().add(item);
        });
        return ResponseEntity.ok(response);
    }

    // Test helper method to clear cart
    @PostMapping("/clear")
    public ResponseEntity<Void> clearCart() {
        cart.clear();
        return ResponseEntity.ok().build();
    }

    // DTO classes for REST API
    public static class AddItemRequest {
        private String productId;
        private int quantity;

        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public static class AddItemResponse {
        private boolean success;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
    }

    public static class GetCartResponse {
        private java.util.List<CartItem> cartItems = new java.util.ArrayList<>();

        public java.util.List<CartItem> getCartItems() { return cartItems; }
        public void setCartItems(java.util.List<CartItem> cartItems) { this.cartItems = cartItems; }

        public static class CartItem {
            private String productId;
            private int quantity;

            public String getProductId() { return productId; }
            public void setProductId(String productId) { this.productId = productId; }
            public int getQuantity() { return quantity; }
            public void setQuantity(int quantity) { this.quantity = quantity; }
        }
    }
}