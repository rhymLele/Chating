package com.duc.chatting.data.model;

import androidx.annotation.NonNull;

public class Image {
    int id;
    String name;
    String path;

    @NonNull
    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

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

    public Image(int id, String name, String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }

    public Image(String name, String path) {
        this.name = name;
        this.path = path;
    }
}
