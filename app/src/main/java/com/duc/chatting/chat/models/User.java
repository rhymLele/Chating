package com.duc.chatting.chat.models;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String email;
    private String name;
    private String phoneNumber;
    private String imgProfile;
    private String imgBanner;
    private String story;
    private String isAdd;
    private String nameAdd;
    private Boolean isChecked;
    public User(String id){
        this.id=id ;
    }

    public User(String id, String name, String imgProfile) {
        this.id = id;
        this.name = name;
        this.imgProfile = imgProfile;
    }
    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImgProfile() {
        return imgProfile;
    }

    public void setImgProfile(String imgProfile) {
        this.imgProfile = imgProfile;
    }

    public String getImgBanner() {
        return imgBanner;
    }

    public void setImgBanner(String imgBanner) {
        this.imgBanner = imgBanner;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getIsAdd() {
        return isAdd;
    }

    public void setIsAdd(String isAdd) {
        this.isAdd = isAdd;
    }

    public String getNameAdd() {
        return nameAdd;
    }

    public void setNameAdd(String nameAdd) {
        this.nameAdd = nameAdd;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public User(String id, String name, String imgProfile, String isAdd, String nameAdd) {
        this.id = id;
        this.name = name;
        this.imgProfile = imgProfile;
        this.isAdd = isAdd;
        this.nameAdd = nameAdd;
    }

    public User(String id, String email, String name, String phoneNumber, String imgProfile, String imgBanner, String story) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.imgProfile = imgProfile;
        this.imgBanner = imgBanner;
        this.story = story;
    }
}
