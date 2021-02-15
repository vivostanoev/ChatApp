package com.example.pmuchatproject.chats;

public class MessageModel {
    private String messageId;
    private String message;
    private String messageFrom;
    private long messageTime;
    private String messageType;

    public MessageModel() {
    }

    public MessageModel(String messageId, String message, String messageFrom, long messageTime, String messageType) {
        this.messageId = messageId;
        this.message = message;
        this.messageFrom = messageFrom;
        this.messageTime = messageTime;
        this.messageType = messageType;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(String messageFrom) {
        this.messageFrom = messageFrom;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
