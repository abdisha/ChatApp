package com.toshi.aerke.model;

import java.util.Map;

public class User {
    private  String fullName;
    private String nickName;
    private String image;
    private String bio;
    private String uid;

    private  UserState userState;
    public UserState getUserState() {
        return userState;
    }

    public void setUserState(UserState userState) {
        this.userState = userState;
    }




    public String getUid() {
        return uid;
    }



    public void setUid(String uid) {
        this.uid = uid;
    }

    public User() {
    }

    public User(String fullName, String nickName, String bio,String Uid ) {
        this.fullName = fullName;
        this.nickName = nickName;
        this.bio = bio;
        this.uid = Uid;
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

    public User(String fullName, String nickName, String image, String bio,String uid,UserState state) {
        this.fullName = fullName;
        this.nickName = nickName;
        this.image = image;
        this.bio = bio;
        this.uid =uid;
        this.userState = state;
    }


}
