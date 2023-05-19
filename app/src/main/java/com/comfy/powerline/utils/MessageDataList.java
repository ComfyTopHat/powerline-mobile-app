package com.comfy.powerline.utils;

public class MessageDataList{
    private String description;
    private String date;
    private int imgId;
    public MessageDataList(String description, int imgId, String date) {
        this.description = description;
        this.date = date;
        this.imgId = imgId;
    }

    public MessageDataList(String description, int imgId) {
        this.description = description;
        this.imgId = imgId;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getImgId() {
        return imgId;
    }
    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getDate() { return date; }
    public void setDate(String date) {
        this.date = date;
    }
}