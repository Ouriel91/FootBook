package com.app.galnoriel.footbook.model;

public class User {

    private String userId; //for chat
    private String userName;
    private String userImageUrl;
    private String userResidence;

    public User(){}

    public User(String userId, String userName, String userImageUrl, String userResidence) {
        this.userId = userId;
        this.userName = userName;
        this.userImageUrl = userImageUrl;
        this.userResidence = userResidence;
    }

    public User(String userName, String userImageUrl, String userResidence) {
        this.userName = userName;
        this.userImageUrl = userImageUrl;
        this.userResidence = userResidence;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getUserResidence() {
        return userResidence;
    }

    public void setUserResidence(String userResidence) {
        this.userResidence = userResidence;
    }

}
