package com.duc.chatting.chat.models;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;

public class ChatMessage implements Serializable {
    private String messageID;
    private String senderID;
    private String senderName;
    private String senderImage;
    private String receiverID;
    private String message;
    private Date dateTime;
    private Date dateObject;
    private String conservationID;
    private String conservationName;
    private String conservationImage;
    private String fileName;
    private String urlFile;
    private String urlImage;
    private Bitmap imageBitmapFromUrl;

    //rep ib
    private String messageRepLocal;
    private String urlFileRepLocal;
    private String urlImageRepLocal;


    private String statusMessage;
    private String conId;

    public ChatMessage(String messageID, String statusMessage, String senderID, String senderName, String senderImage, String receiverID, String fileName, String urlFile, Date dateTime, Date dateObject) {
        this.messageID = messageID;
        this.statusMessage = statusMessage;
        this.senderID = senderID;
        this.senderName = senderName;
        this.senderImage = senderImage;
        this.receiverID = receiverID;
        this.fileName = fileName;
        this.urlFile = urlFile;
        this.dateTime = dateTime;
        this.dateObject = dateObject;
    }

    public ChatMessage(String messageID, String statusMessage, String senderID, String senderName, String senderImage, String receiverID, String message, Date dateTime, Date dateObject, String messageRepLocal, String urlFileRepLocal, String urlImageRepLocal) {
        this.messageID = messageID;
        this.statusMessage = statusMessage;
        this.senderID = senderID;
        this.senderName = senderName;
        this.senderImage = senderImage;
        this.receiverID = receiverID;
        this.message = message;
        this.dateTime = dateTime;
        this.dateObject = dateObject;
        this.messageRepLocal = messageRepLocal;
        this.urlFileRepLocal = urlFileRepLocal;
        this.urlImageRepLocal = urlImageRepLocal;
    }

    public ChatMessage(String senderID, String senderName, String receiverID, String message, Date dateObject, String conservationID, String conservationName, String conservationImage) {
        this.senderID = senderID;
        this.senderName = senderName;
        this.receiverID = receiverID;
        this.message = message;
        this.dateObject = dateObject;
        this.conservationID = conservationID;
        this.conservationName = conservationName;
        this.conservationImage = conservationImage;
    }

    public ChatMessage(String senderID, String senderName, String receiverID, String message, Date dateObject,
                       String conservationID, String conservationName, String conservationImage, String conId) {
        this.senderID = senderID;
        this.senderName = senderName;
        this.receiverID = receiverID;
        this.message = message;
        this.dateObject = dateObject;
        this.conservationID = conservationID;
        this.conservationName = conservationName;
        this.conservationImage = conservationImage;
        this.conId = conId;
    }

    public ChatMessage(String senderID, String receiverID, String message, Date dateTime, String senderName, String senderImage, String messageRepLocal, String urlFileRepLocal, String urlImageRepLocal) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.message = message;
        this.dateTime = dateTime;
        this.senderName = senderName;
        this.senderImage = senderImage;
        this.messageRepLocal = messageRepLocal;
        this.urlFileRepLocal = urlFileRepLocal;
        this.urlImageRepLocal = urlImageRepLocal;
    }

    public ChatMessage(String senderID, String receiverID, String message, Date dateTime, Date dateObject, String messageID, String statusMessage, String messageRepLocal, String urlFileRepLocal, String urlImageRepLocal) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.message = message;
        this.dateTime = dateTime;
        this.dateObject = dateObject;
        this.messageID = messageID;
        this.statusMessage = statusMessage;
        this.messageRepLocal = messageRepLocal;
        this.urlFileRepLocal = urlFileRepLocal;
        this.urlImageRepLocal = urlImageRepLocal;
    }

    public ChatMessage(String senderID, String receiverID, String fileName, String urlFile, Date dateTime, Date dateObject, String messageID, String statusMessage) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.fileName = fileName;
        this.urlFile = urlFile;
        this.dateTime = dateTime;
        this.dateObject = dateObject;
        this.messageID = messageID;
        this.statusMessage = statusMessage;
    }

    public ChatMessage(String messageID, String statusMessage, String senderID, String receiverID, String message, Date dateTime, Date dateObject, String messageRepLocal, String urlFileRepLocal, String urlImageRepLocal) {
        this.messageID = messageID;
        this.statusMessage = statusMessage;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.message = message;
        this.dateTime = dateTime;
        this.dateObject = dateObject;
        this.messageRepLocal = messageRepLocal;
        this.urlFileRepLocal = urlFileRepLocal;
        this.urlImageRepLocal = urlImageRepLocal;
    }

    public ChatMessage(String messageID, String statusMessage, String senderID, String receiverID, String fileName, String urlFile, Date dateTime, Date dateObject) {
        this.messageID = messageID;
        this.statusMessage = statusMessage;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.fileName = fileName;
        this.urlFile = urlFile;
        this.dateTime = dateTime;
        this.dateObject = dateObject;
    }

    public ChatMessage(String senderID, String receiverID, String fileName, String urlFile, Date dateTime, String senderName, String senderImage) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.fileName = fileName;
        this.urlFile = urlFile;
        this.dateTime = dateTime;
        this.senderName = senderName;
        this.senderImage = senderImage;
    }

    public ChatMessage(String senderID, String senderName, String senderImage, String receiverID, Date dateTime, String urlImage) {
        this.senderID = senderID;
        this.senderName = senderName;
        this.senderImage = senderImage;
        this.receiverID = receiverID;
        this.dateTime = dateTime;
        this.urlImage = urlImage;
    }

    public ChatMessage(String senderID, String receiverID, String message, Date dateObject, String conservationID, String conservationName, String conservationImage, String senderName) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.message = message;
        this.dateObject = dateObject;
        this.conservationID = conservationID;
        this.conservationName = conservationName;
        this.conservationImage = conservationImage;
        this.senderName = senderName;
    }

    public ChatMessage(String senderID, String receiverID, String fileName, String urlFile, Date dateTime, Date dateObject, String senderName, String senderImage, String messageID, String statusMessage) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.fileName = fileName;
        this.urlFile = urlFile;
        this.dateTime = dateTime;
        this.dateObject = dateObject;
        this.senderName = senderName;
        this.senderImage = senderImage;
        this.messageID = messageID;
        this.statusMessage = statusMessage;
    }

    public ChatMessage(String senderID, String receiverID, String message, Date dateTime, Date dateObject, String senderName, String senderImage, String messageID, String statusMessage, String messageRepLocal, String urlFileRepLocal, String urlImageRepLocal) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.message = message;
        this.dateTime = dateTime;
        this.dateObject = dateObject;
        this.senderName = senderName;
        this.senderImage = senderImage;
        this.messageID = messageID;
        this.statusMessage = statusMessage;
        this.messageRepLocal = messageRepLocal;
        this.urlFileRepLocal = urlFileRepLocal;
        this.urlImageRepLocal = urlImageRepLocal;
    }

    public ChatMessage(String messageID, String senderID, String receiverID, Date dateTime, Date dateObject, String urlImage, String statusMessage) {
        this.messageID = messageID;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.dateTime = dateTime;
        this.dateObject = dateObject;
        this.urlImage = urlImage;
        this.statusMessage = statusMessage;
    }

    public ChatMessage(String messageID, String senderID, String senderName, String senderImage, String receiverID, Date dateTime, Date dateObject, String urlImage, String statusMessage) {
        this.messageID = messageID;
        this.senderID = senderID;
        this.senderName = senderName;
        this.senderImage = senderImage;
        this.receiverID = receiverID;
        this.dateTime = dateTime;
        this.dateObject = dateObject;
        this.urlImage = urlImage;
        this.statusMessage = statusMessage;
    }

    public ChatMessage(String senderID, String senderName, String senderImage, String receiverID, String message, Date dateTime) {
        this.senderID = senderID;
        this.senderName = senderName;
        this.senderImage = senderImage;
        this.receiverID = receiverID;
        this.message = message;
        this.dateTime = dateTime;
    }

    public ChatMessage(String senderID, String senderName, String senderImage, String receiverID, String message, String messageRepLocal, String urlFileRepLocal, String urlImageRepLocal, Date dateTime) {
        this.senderID = senderID;
        this.senderName = senderName;
        this.senderImage = senderImage;
        this.receiverID = receiverID;
        this.message = message;
        this.messageRepLocal = messageRepLocal;
        this.urlFileRepLocal = urlFileRepLocal;
        this.urlImageRepLocal = urlImageRepLocal;
        this.dateTime = dateTime;
    }

    public ChatMessage(String senderID, String senderName, String senderImage, String receiverID, String fileName, String urlFile) {
        this.senderID = senderID;
        this.senderName = senderName;
        this.senderImage = senderImage;
        this.receiverID = receiverID;
        this.fileName = fileName;
        this.urlFile = urlFile;
    }

    public ChatMessage(String senderID, String senderName, String senderImage, String receiverID, String fileName, String urlFile, Date dateTime) {
        this.senderID = senderID;
        this.senderName = senderName;
        this.senderImage = senderImage;
        this.receiverID = receiverID;
        this.fileName = fileName;
        this.urlFile = urlFile;
        this.dateTime = dateTime;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Date getDateObject() {
        return dateObject;
    }

    public void setDateObject(Date dateObject) {
        this.dateObject = dateObject;
    }

    public String getConservationID() {
        return conservationID;
    }

    public void setConservationID(String conservationID) {
        this.conservationID = conservationID;
    }

    public String getConservationName() {
        return conservationName;
    }

    public void setConservationName(String conservationName) {
        this.conservationName = conservationName;
    }

    public String getConservationImage() {
        return conservationImage;
    }

    public void setConservationImage(String conservationImage) {
        this.conservationImage = conservationImage;
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

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public Bitmap getImageBitmapFromUrl() {
        return imageBitmapFromUrl;
    }

    public void setImageBitmapFromUrl(Bitmap imageBitmapFromUrl) {
        this.imageBitmapFromUrl = imageBitmapFromUrl;
    }

    public String getMessageRepLocal() {
        return messageRepLocal;
    }

    public void setMessageRepLocal(String messageRepLocal) {
        this.messageRepLocal = messageRepLocal;
    }

    public String getUrlFileRepLocal() {
        return urlFileRepLocal;
    }

    public void setUrlFileRepLocal(String urlFileRepLocal) {
        this.urlFileRepLocal = urlFileRepLocal;
    }

    public String getUrlImageRepLocal() {
        return urlImageRepLocal;
    }

    public void setUrlImageRepLocal(String urlImageRepLocal) {
        this.urlImageRepLocal = urlImageRepLocal;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getConId() {
        return conId;
    }

    public void setConId(String conId) {
        this.conId = conId;
    }
}
