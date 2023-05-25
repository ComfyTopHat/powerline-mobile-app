package com.comfy.powerline.utils;

public class MessageDataList {
    private String description;
    private String date;
    private String senderID;
    private String message;
    private int imgId;
    public MessageDataList(String description, String message, int imgId, String date, String senderID) {
        this.description = description;
        this.date = date;
        this.message = message;
        this.imgId = imgId;
        this.senderID = senderID;
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

    public String getSenderID() { return senderID; }
    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {return message; }
}