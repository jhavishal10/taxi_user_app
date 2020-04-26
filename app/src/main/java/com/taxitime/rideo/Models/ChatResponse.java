package com.taxitime.rideo.Models;

import java.util.List;

public class ChatResponse {
    private boolean error;
    private List<Chat> chat;

    public boolean isError() {
        return error;
    }

    public List<Chat> getChat() {
        return chat;
    }

    public ChatResponse(boolean error, List<Chat> chat) {
        this.error = error;
        this.chat = chat;
    }
}
