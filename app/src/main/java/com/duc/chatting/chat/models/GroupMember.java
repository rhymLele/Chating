package com.duc.chatting.chat.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class GroupMember implements Serializable {
    private String groupChatID;
    @SerializedName("userId")
    private String userId;
    private String name;
    private String image;
    private Date timeAdd;
    private int cntMembers;
    private String userIdAdd;
    private String userNameAdd;

    public GroupMember(String groupChatID, String userId, String name, String image, Date timeAdd, String userIdAdd, String userNameAdd) {
        this.groupChatID = groupChatID;
        this.userId = userId;
        this.name = name;
        this.image = image;
        this.timeAdd = timeAdd;
        this.userIdAdd = userIdAdd;
        this.userNameAdd = userNameAdd;
    }

    public GroupMember(String groupChatID, String userId, String name, Date timeAdd, String userIdAdd, String userNameAdd) {
        this.groupChatID = groupChatID;
        this.userId = userId;
        this.name = name;
        this.timeAdd = timeAdd;
        this.userIdAdd = userIdAdd;
        this.userNameAdd = userNameAdd;
    }
    public int getCntMembers() {
        return cntMembers;
    }

    public void setCntMembers(int cntMembers) {
        this.cntMembers = cntMembers;
    }
    public String getGroupChatID() {
        return groupChatID;
    }

    public void setGroupChatID(String groupChatID) {
        this.groupChatID = groupChatID;
    }

    public String getUserID() {
        return userId;
    }

    public void setUserID(String userId) {
        this.userId = userId;
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
