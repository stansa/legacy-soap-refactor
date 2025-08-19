package com.example.legacysoap.service;

import com.example.legacysoap.dto.CartItem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe cart service using Java 17 features
 */
@Service
public class CartService {
    
    private final ConcurrentHashMap<String, Integer> cart = new ConcurrentHashMap<>();
    
    /**
     * Add item to cart or increment existing quantity
     * @param productId the product identifier
     * @param quantity the quantity to add
     * @return CartItem with updated quantity
     * @throws IllegalArgumentException if quantity is not positive
     */
    public CartItem addItem(String productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        
        int newQuantity = cart.merge(productId, quantity, Integer::sum);
        return new CartItem(productId, newQuantity);
    }
    
    /**
     * Get all cart items
     * @return List of cart items
     */
    public List<CartItem> getCartItems() {
        return cart.entrySet().stream()
            .map(entry -> new CartItem(entry.getKey(), entry.getValue()))
            .toList();
    }
    
    /**
     * Update quantity of existing item
     * @param productId the product identifier
     * @param quantity the new quantity
     * @return Optional CartItem if product exists
     */
    public Optional<CartItem> updateQuantity(String productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        return cart.containsKey(productId) 
            ? Optional.of(new CartItem(productId, cart.put(productId, quantity)))
            : Optional.empty();
    }
    
    /**
     * Remove item from cart
     * @param productId the product identifier
     * @return true if item was removed, false if not found
     */
    public boolean removeItem(String productId) {
        return cart.remove(productId) != null;
    }
    
    /**
     * Clear all items from cart
     */
    public void clearCart() {
        cart.clear();
    }
    
    /**
     * Get total number of items in cart
     * @return total item count
     */
    public int getTotalItems() {
        return cart.size();
    }
    
    /**
     * Get total quantity of all items
     * @return total quantity
     */
    public int getTotalQuantity() {
        return cart.values().stream().mapToInt(Integer::intValue).sum();
    }
}
