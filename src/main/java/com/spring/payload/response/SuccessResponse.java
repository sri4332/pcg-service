package com.spring.payload.response;

public class SuccessResponse extends Response {
    private String status="SUCCESS";
    private String message="Requested action completed successfully";

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }
}
