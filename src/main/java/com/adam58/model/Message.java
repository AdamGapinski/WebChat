package com.adam58.model;

/**
 * @author Adam Gapiński
 */
public class Message {
    private String sender = "unknown";
    private String content;

    public Message(String sender, String content) {
        this.content = content;
        this.sender = sender;
    }

    public Message(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }
}
