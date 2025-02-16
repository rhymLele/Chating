package com.duc.chatting.chat.models;

import java.io.Serializable;
import java.util.Date;

public class ImageClass implements Serializable {
    private String conservationID;
    private String chatID;
    private String senderID;
    private String receiverID;
    private String urlImage;
    private Date timestamp;
    private String statusImage;

    public ImageClass(String conservationID, String chatID, String senderID, String receiverID, String urlImage, Date timestamp, String statusImage) {
        this.conservationID = conservationID;
        this.chatID = chatID;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.urlImage = urlImage;
        this.timestamp = timestamp;
        this.statusImage = statusImage;
    }

    public ImageClass(String urlImage, String statusImage) {
        this.urlImage = urlImage;
        this.statusImage = statusImage;
    }

    public String getConservationID() {
        return conservationID;
    }

    public void setConservationID(String conservationID) {
        this.conservationID = conservationID;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatusImage() {
        return statusImage;
    }

    public void setStatusImage(String statusImage) {
        this.statusImage = statusImage;
    }
}
