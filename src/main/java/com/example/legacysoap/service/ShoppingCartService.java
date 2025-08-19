package com.example.legacysoap.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ShoppingCartService {
    private Map<String, Integer> cart = new HashMap<>(); // In-memory cart

    public void addItem(String productId, int quantity) {
        cart.put(productId, cart.getOrDefault(productId, 0) + quantity);
    }

    public Map<String, Integer> getCart() {
        return new HashMap<>(cart);
    }

    public void removeItem(String productId) {
        cart.remove(productId);
    }

    public boolean updateQuantity(String productId, int quantity) {
        if (cart.containsKey(productId)) {
            cart.put(productId, quantity);
            return true;
        }
        return false;
    }

    public void clearCart() {
        cart.clear();
    }
}