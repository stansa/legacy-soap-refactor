package com.example.legacysoap.dto;

public class AddItemRestResponse {
    private boolean success;

    public AddItemRestResponse() {}

    public AddItemRestResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}