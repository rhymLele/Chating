package com.duc.chatting.chat.models;

public class ThemeItem {
    private Object imageResource;
    private String name;

    public ThemeItem(Object imageResource, String name) {
        this.imageResource = imageResource;
        this.name = name;
    }

    public Object  getImageUrl() {
        return imageResource;
    }

    public String getName() {
        return name;
    }
}
