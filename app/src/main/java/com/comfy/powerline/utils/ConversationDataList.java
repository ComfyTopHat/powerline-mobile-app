package com.comfy.powerline.utils;


public class ConversationDataList {
    private String contactName;
    private String contactID;
    private int contactImage;
    private String messagePreview;
    private String date;

    public ConversationDataList(String contactName, String contactID, String messagePreview, String date, int contactImage) {
        this.messagePreview = messagePreview;
        this.contactID = contactID;
        this.contactImage = contactImage;
        this.contactName = contactName;
        this.date = date;
    }

    public String getMessagePreview() {
        return this.messagePreview;
    }

    public int getContactImage() {
        return this.contactImage;
    }
    public String getContactID() { return this.contactID; }

    public String getContactName() {
        return this.contactName;
    }

    public String getDate() {
        return this.date;
    }
}

