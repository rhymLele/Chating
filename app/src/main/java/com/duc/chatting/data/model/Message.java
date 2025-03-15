package com.duc.chatting.data.model;

public class Message {
    String username;
    String message;
    int status;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public void setStatus(int status) {
        this.status = status;
    }

    public void setSending(int status) {
        this.status = status;
    }

    public Message(String username, String message, int status) {
        this.username = username;
        this.message = message;
        this.status = status;
    }
}
