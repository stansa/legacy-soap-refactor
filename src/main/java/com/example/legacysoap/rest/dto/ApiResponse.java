package com.example.legacysoap.rest.dto;

/**
 * Standard API response for operations that return success/failure.
 * Replaces SOAP response objects like AddItemResponse, RemoveItemResponse, etc.
 */
public class ApiResponse {
    
    private boolean success;
    private String message;
    
    // Default constructor
    public ApiResponse() {}
    
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    // Convenience factory methods
    public static ApiResponse success(String message) {
        return new ApiResponse(true, message);
    }
    
    public static ApiResponse failure(String message) {
        return new ApiResponse(false, message);
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
    
    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}