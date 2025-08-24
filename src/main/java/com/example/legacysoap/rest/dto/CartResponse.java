package com.example.legacysoap.rest.dto;

import java.util.List;

/**
 * REST DTO for cart contents.
 * Replaces the SOAP-generated GetCartResponse class.
 */
public class CartResponse {
    
    private List<CartItem> items;
    private int totalItems;
    
    // Default constructor
    public CartResponse() {}
    
    public CartResponse(List<CartItem> items) {
        this.items = items;
        this.totalItems = items != null ? items.stream().mapToInt(CartItem::getQuantity).sum() : 0;
    }
    
    public List<CartItem> getItems() {
        return items;
    }
    
    public void setItems(List<CartItem> items) {
        this.items = items;
        this.totalItems = items != null ? items.stream().mapToInt(CartItem::getQuantity).sum() : 0;
    }
    
    public int getTotalItems() {
        return totalItems;
    }
    
    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }
    
    @Override
    public String toString() {
        return "CartResponse{" +
                "items=" + items +
                ", totalItems=" + totalItems +
                '}';
    }
}