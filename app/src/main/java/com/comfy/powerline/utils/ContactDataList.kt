package com.comfy.powerline.utils;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ContactDataList implements Parcelable {
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

    protected ContactDataList(Parcel in) {
        description = in.readString();
        date = in.readString();
        contactID = in.readString();
        imgId = in.readInt();
    }

    public static final Creator<ContactDataList> CREATOR = new Creator<ContactDataList>() {
        @Override
        public ContactDataList createFromParcel(Parcel in) {
            return new ContactDataList(in);
        }

        @Override
        public ContactDataList[] newArray(int size) {
            return new ContactDataList[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(description);
        parcel.writeString(date);
        parcel.writeString(contactID);
        parcel.writeInt(imgId);
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