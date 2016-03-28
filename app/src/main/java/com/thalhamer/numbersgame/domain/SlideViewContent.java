package com.thalhamer.numbersgame.domain;

/**
 * Created by Brian on 3/27/2016.
 */
public class SlideViewContent {

    private String title;
    private String description;
    private int imageResourceId;

    public SlideViewContent() {
    }

    public SlideViewContent(String title, String description, int imageResourceId) {
        this.title = title;
        this.description = description;
        this.imageResourceId = imageResourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }
}
