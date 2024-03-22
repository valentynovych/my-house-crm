package com.example.myhouse24rest.model.error;

import io.swagger.v3.oas.annotations.media.Schema;

public class CustomErrorResponse {
    @Schema(description = "Status code")
    private final int status;
    @Schema(description = "Error message")
    private String message;

    public CustomErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }
}
