package com.toshi.aerke.model;

public class User {
    private  String fullName;
    private String nickName;
    private String image;
    private String bio;;

    public User() {
    }

    public User(String fullName, String nickName, String bio) {
        this.fullName = fullName;
        this.nickName = nickName;
        this.bio = bio;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public User(String fullName, String nickName, String image, String bio) {
        this.fullName = fullName;
        this.nickName = nickName;
        this.image = image;
        this.bio = bio;
    }
}
