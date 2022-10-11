package com.spring.payload.response;

public class FailureResponse extends Response {
    private String status="FAILURE";
    private String message="Fail: Requested action not completed";

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
