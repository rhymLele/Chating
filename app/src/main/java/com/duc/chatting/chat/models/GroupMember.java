package com.duc.chatting.chat.models;

import java.io.Serializable;
import java.util.Date;

public class GroupMember implements Serializable {
    private String groupChatID;
    private String userID;
    private String name;
    private String image;
    private Date timeAdd;

    private String userIdAdd;
    private String userNameAdd;

    public GroupMember(String groupChatID, String userID, String name, String image, Date timeAdd, String userIdAdd, String userNameAdd) {
        this.groupChatID = groupChatID;
        this.userID = userID;
        this.name = name;
        this.image = image;
        this.timeAdd = timeAdd;
        this.userIdAdd = userIdAdd;
        this.userNameAdd = userNameAdd;
    }

    public GroupMember(String groupChatID, String userID, String name, Date timeAdd, String userIdAdd, String userNameAdd) {
        this.groupChatID = groupChatID;
        this.userID = userID;
        this.name = name;
        this.timeAdd = timeAdd;
        this.userIdAdd = userIdAdd;
        this.userNameAdd = userNameAdd;
    }

    public String getGroupChatID() {
        return groupChatID;
    }

    public void setGroupChatID(String groupChatID) {
        this.groupChatID = groupChatID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getTimeAdd() {
        return timeAdd;
    }

    public void setTimeAdd(Date timeAdd) {
        this.timeAdd = timeAdd;
    }

    public String getUserIdAdd() {
        return userIdAdd;
    }

    public void setUserIdAdd(String userIdAdd) {
        this.userIdAdd = userIdAdd;
    }

    public String getUserNameAdd() {
        return userNameAdd;
    }

    public void setUserNameAdd(String userNameAdd) {
        this.userNameAdd = userNameAdd;
    }
}
