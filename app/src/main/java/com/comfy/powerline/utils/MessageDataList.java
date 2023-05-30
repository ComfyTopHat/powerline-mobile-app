package com.comfy.powerline.utils;

public class MessageDataList {
    private String author;
    private Boolean selfAuthored;
    private String date;
    private String senderID;
    private String message;
    private int imgId;
    public MessageDataList(String description, String message, int imgId, String date, String senderID, Boolean selfAuthored) {
        this.author = description;
        this.date = date;
        this.message = message;
        if (selfAuthored) {
            this.imgId = 0;
        }
        else {
            this.imgId = imgId;
        }
        this.senderID = senderID;
        this.selfAuthored = selfAuthored;
    }

    public MessageDataList(String description, int imgId) {
        this.author = description;
        this.imgId = imgId;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public int getImgId() {
        return imgId;
    }
    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getDate() { return date; }
    public Boolean getSelfAuthored() { return selfAuthored; }

    public void setSelfAuthored(Boolean selfAuthored) {
        this.selfAuthored = selfAuthored;
    }

    public String getSenderID() { return senderID; }
    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {return message; }
}