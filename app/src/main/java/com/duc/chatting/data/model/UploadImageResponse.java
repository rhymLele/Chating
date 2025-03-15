package com.duc.chatting.data.model;

public class UploadImageResponse {
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public UploadImageResponse(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
