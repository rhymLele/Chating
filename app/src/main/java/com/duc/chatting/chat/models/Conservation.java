package com.duc.chatting.chat.models;

import java.io.Serializable;
import java.util.Date;

public class Conservation implements Serializable {
    private String conservationID;
    private String senderID;
    private String senderName;
    private String senderImage;
    private String receiverID;
    private String receiverName;
    private String receiverImage;
    private String lastMessage;
    private Date timestamp;

    public Conservation(String senderID, String senderName, String senderImage, String receiverID, String receiverName, String receiverImage, String lastMessage, Date timestamp) {
        this.senderID = senderID;
        this.senderName = senderName;
        this.senderImage = senderImage;
        this.receiverID = receiverID;
        this.receiverName = receiverName;
        this.receiverImage = receiverImage;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public Conservation(String conservationID, String receiverID) {
        this.conservationID = conservationID;
        this.receiverID = receiverID;
    }

    public String getConservationID() {
        return conservationID;
    }

    public void setConservationID(String conservationID) {
        this.conservationID = conservationID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverImage() {
        return receiverImage;
    }

    public void setReceiverImage(String receiverImage) {
        this.receiverImage = receiverImage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
