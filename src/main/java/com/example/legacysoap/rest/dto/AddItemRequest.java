package com.example.legacysoap.rest.dto;

// NOTE: For Java 17/Spring Boot 3.x, these would be:
// import jakarta.validation.constraints.Min;
// import jakarta.validation.constraints.NotBlank;

/**
 * REST DTO for adding items to cart.
 * Replaces the SOAP-generated AddItemRequest class.
 * 
 * NOTE: This version compiles with Java 8/Spring Boot 2.x
 * For production use with Java 17/Spring Boot 3.x, uncomment validation annotations above
 */
public class AddItemRequest {
    
    // @NotBlank(message = "Product ID is required") // Enable in Spring Boot 3.x
    private String productId;
    
    // @Min(value = 1, message = "Quantity must be positive") // Enable in Spring Boot 3.x
    private int quantity;
    
    // Default constructor for JSON deserialization
    public AddItemRequest() {}
    
    public AddItemRequest(String productId, int quantity) {
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
        return "AddItemRequest{" +
                "productId='" + productId + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}