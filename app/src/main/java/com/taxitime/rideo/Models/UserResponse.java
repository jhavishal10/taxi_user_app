package com.taxitime.rideo.Models;

public class UserResponse {
    private boolean error;
    private String message;
    private User user;

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public UserResponse(boolean error, String message, User user) {
        this.error = error;
        this.message = message;
        this.user = user;
    }
}
