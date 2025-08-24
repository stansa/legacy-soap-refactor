package com.example.legacysoap.rest.dto;

/**
 * Represents a single item in the shopping cart.
 * Used within CartResponse to replace SOAP CartItems nested class.
 */
public class CartItem {
    
    private String productId;
    private int quantity;
    
    // Default constructor
    public CartItem() {}
    
    public CartItem(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    @Override
    public String toString() {
        return "CartItem{" +
                "productId='" + productId + '\'' +
                ", quantity=" + quantity +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        CartItem cartItem = (CartItem) o;
        
        if (quantity != cartItem.quantity) return false;
        return productId != null ? productId.equals(cartItem.productId) : cartItem.productId == null;
    }
    
    @Override
    public int hashCode() {
        int result = productId != null ? productId.hashCode() : 0;
        result = 31 * result + quantity;
        return result;
    }
}