package com.sda5.double2app.models;

import java.util.UUID;

public class Notification {


    private String from;
    private String group;
    private String message;
    private String notificationId;
    private String tokenId;


    public Notification() {
    }

    public Notification(String from, String group, String message, String tokenId) {
        this.from = from;
        this.group = group;
        this.message = message;
        this.notificationId = UUID.randomUUID().toString();
        this.tokenId = tokenId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }
}


