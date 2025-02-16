package com.duc.chatting.chat.models;

import java.io.Serializable;
import java.util.Date;

public class PDFClass implements Serializable {
    private String conservationID;
    private String chatID;
    private String senderID;
    private String receiverID;
    private String fileName;
    private String urlFile;
    private Date timestamp;
    private String statusFile;

    public PDFClass(String conservationID, String chatID, String senderID, String receiverID, String fileName, String urlFile, Date timestamp, String statusFile) {
        this.conservationID = conservationID;
        this.chatID = chatID;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.fileName = fileName;
        this.urlFile = urlFile;
        this.timestamp = timestamp;
        this.statusFile = statusFile;
    }

    public PDFClass(String fileName, String urlFile) {
        this.fileName = fileName;
        this.urlFile = urlFile;
    }

    public PDFClass(String fileName, String urlFile, String statusFile) {
        this.fileName = fileName;
        this.urlFile = urlFile;
        this.statusFile = statusFile;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrlFile() {
        return urlFile;
    }

    public void setUrlFile(String urlFile) {
        this.urlFile = urlFile;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatusFile() {
        return statusFile;
    }

    public void setStatusFile(String statusFile) {
        this.statusFile = statusFile;
    }

}
