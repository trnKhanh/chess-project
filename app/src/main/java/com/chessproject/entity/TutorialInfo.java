package com.chessproject.entity;

public class TutorialInfo {
    private String description;
    private int imageResourceId;

    public TutorialInfo(String description, int imageResourceId){
        this.description = description;
        this.imageResourceId = imageResourceId;
    }

    public String getDescription(){
        return description;
    }

    public int getImageResourceId(){
        return imageResourceId;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setImageResourceId(int imageResourceId){
        this.imageResourceId = imageResourceId;
    }
}
