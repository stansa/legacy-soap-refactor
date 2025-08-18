package com.example.legacysoap.rest.dto;

// NOTE: For Java 17/Spring Boot 3.x, this would be:
// import jakarta.validation.constraints.Min;

/**
 * REST DTO for updating item quantity in cart.
 * Replaces the SOAP-generated UpdateQuantityRequest class.
 * 
 * NOTE: This version compiles with Java 8/Spring Boot 2.x
 * For production use with Java 17/Spring Boot 3.x, uncomment validation annotation above
 */
public class UpdateQuantityRequest {
    
    // @Min(value = 0, message = "Quantity cannot be negative") // Enable in Spring Boot 3.x
    private int quantity;
    
    // Default constructor for JSON deserialization
    public UpdateQuantityRequest() {}
    
    public UpdateQuantityRequest(int quantity) {
        this.quantity = quantity;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    @Override
    public String toString() {
        return "UpdateQuantityRequest{" +
                "quantity=" + quantity +
                '}';
    }
}