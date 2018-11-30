package com.toshi.aerke.model;

public class Message {
    private String message;
    private String to;
    private String seen;
    private String time;
    private String type;

    public String getMessage() {
        return message;
    }

    public Message() {
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Message(String message, String to, String seen, String time, String type) {
        this.message = message;
        this.to = to;
        this.seen = seen;
        this.time = time;
        this.type = type;
    }
}
