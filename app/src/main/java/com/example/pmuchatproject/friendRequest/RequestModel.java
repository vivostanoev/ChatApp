package com.example.pmuchatproject.friendRequest;

public class RequestModel {
    private String userId;
    private String username;
    private String photoName;


    public RequestModel(String userId, String username, String photoName) {
        this.userId = userId;
        this.username = username;
        this.photoName = photoName;
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
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }
}
