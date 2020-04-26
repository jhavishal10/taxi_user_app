package com.taxitime.rideo.Models;

public class Chat {
    private String type, message;

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public Chat(String type, String message) {
        this.type = type;
        this.message = message;
    }
}
