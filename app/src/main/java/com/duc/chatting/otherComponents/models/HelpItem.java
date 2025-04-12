package com.duc.chatting.otherComponents.models;

public class HelpItem {
    private String title, content,category;

    public String getCategory() {
        return category;
    }

    public HelpItem(String title, String content, String category) {
        this.title = title;
        this.category=category;
        this.content = content;
    }
    public HelpItem(String title, String content) {
        this.title = title;
        this.content = content;
    }
    public String getTitle() { return title; }
    public String getContent() { return content; }
}
