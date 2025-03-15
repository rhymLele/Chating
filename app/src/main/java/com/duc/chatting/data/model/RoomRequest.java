package com.duc.chatting.data.model;

public class RoomRequest {
    private String roomName;

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public RoomRequest(String roomName) {
        this.roomName = roomName;
    }
}
