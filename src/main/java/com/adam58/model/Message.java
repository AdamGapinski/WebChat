package com.adam58.model;

import java.util.Date;

/**
 * @author Adam Gapiński
 */
public class Message {
    private String sender = "unknown";
    private String content;
    private Date datetime;

    public Message(String sender, String content, Date datetime) {
        this.content = content;
        this.sender = sender;
        this.datetime = datetime;
    }

    public Message(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public Date getDatetime() {
        return datetime;
    }

    public String getContent() {
        return content;
    }
}
