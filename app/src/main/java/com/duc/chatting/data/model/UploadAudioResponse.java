package com.duc.chatting.data.model;

public class UploadAudioResponse {
    private String audioUrl;

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public UploadAudioResponse(String audioFileName) {
        this.audioUrl = audioFileName;
    }
}
