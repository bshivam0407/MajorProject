package com.example.majorproject;

import java.util.ArrayList;
import java.util.List;

public class Image {
    String id;
    List<String> tags;

    public Image(){}

    public Image(String id, List<String> tags) {
        this.id = id;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id='" + id + '\'' +
                ", tags=" + tags +
                '}';
    }
}
