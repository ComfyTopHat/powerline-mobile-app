package com.comfy.powerline.utils;

public class ContactDataList {
    private String description;
    private String date;
    private String contactID;
    private int imgId;
    public ContactDataList(String description, int imgId, String date, String contactID) {
        this.description = description;
        this.date = date;
        this.imgId = imgId;
        this.contactID = contactID;
    }

    public ContactDataList(String description, int imgId, String contactID) {
        this.description = description;
        this.imgId = imgId;
        this.contactID = contactID;
    }
    public String getDescription() {
        return description;
    }

    public void setContactID(String contactID) {
        this.contactID = contactID;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactID() {
        return contactID;
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