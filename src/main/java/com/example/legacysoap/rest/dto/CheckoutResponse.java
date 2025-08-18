package com.example.legacysoap.rest.dto;

/**
 * REST DTO for checkout response.
 * Replaces the SOAP-generated CheckoutResponse class with enhanced functionality.
 */
public class CheckoutResponse {
    
    private boolean success;
    private String message;
    private double total;
    private String orderId;
    
    // Default constructor
    public CheckoutResponse() {}
    
    public CheckoutResponse(boolean success, String message, double total, String orderId) {
        this.success = success;
        this.message = message;
        this.total = total;
        this.orderId = orderId;
    }
    
    // Convenience factory methods
    public static CheckoutResponse success(double total, String orderId) {
        return new CheckoutResponse(true, "Checkout completed successfully", total, orderId);
    }
    
    public static CheckoutResponse failure(String message) {
        return new CheckoutResponse(false, message, 0.0, null);
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public double getTotal() {
        return total;
    }
    
    public void setTotal(double total) {
        this.total = total;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    @Override
    public String toString() {
        return "CheckoutResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", total=" + total +
                ", orderId='" + orderId + '\'' +
                '}';
    }
}