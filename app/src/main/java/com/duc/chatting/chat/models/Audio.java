package com.duc.chatting.chat.models;

import java.io.Serializable;

public class Audio implements Serializable {
    int id;
    String name;
    String path;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Audio(int id, String name, String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }

    public Audio(String name, String path) {
        this.name = name;
        this.path = path;
    }
}
