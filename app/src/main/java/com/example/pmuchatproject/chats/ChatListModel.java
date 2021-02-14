package com.example.pmuchatproject.chats;

public class ChatListModel {
    private String userId;
    private String username;
    private String photoName;
    private String unreadCount;
    private String lastMessage;
    private String lastMessageTime;

    public ChatListModel(String userId, String username, String photoName, String unreadCount, String lastMessage, String lastMessageTime) {
        this.userId = userId;
        this.username = username;
        this.photoName = photoName;
        this.unreadCount = unreadCount;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhotoName() {
        return userId + ".jpg";
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(String unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
}
