package com.example.majorproject;

import java.util.ArrayList;

public class Image {
    String id;
    String[] tags;

    public Image(){}

    public Image(String id, String[] tags) {
        this.id = id;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }
}
