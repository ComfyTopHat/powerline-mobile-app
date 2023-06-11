package com.comfy.powerline.utils

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

class ContactDataList  {
    var description: String?
    var date: String? = null
    var contactID: String?
    var imgId: Int

    constructor(description: String?, imgId: Int, date: String?, contactID: String?) {
        this.description = description
        this.date = date
        this.imgId = imgId
        this.contactID = contactID
    }

    constructor(description: String?, imgId: Int, contactID: String?) {
        this.description = description
        this.imgId = imgId
        this.contactID = contactID
    }

    protected constructor(`in`: Parcel) {
        description = `in`.readString()
        date = `in`.readString()
        contactID = `in`.readString()
        imgId = `in`.readInt()
    }






}