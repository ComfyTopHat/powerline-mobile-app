package com.comfy.powerline.utils

class MessageDataList {
    var author: String
    var selfAuthored: Boolean? = null
    var date: String? = null
    var senderID: String? = null
        private set
    var isMessagePreview = false
        private set
    var message: String? = null
        private set
    var imgId = 0

    constructor(
        description: String,
        message: String?,
        imgId: Int,
        date: String?,
        senderID: String?,
        selfAuthored: Boolean,
        messagePreview: Boolean
    ) {
        author = description
        this.date = date
        this.message = message
        isMessagePreview = messagePreview
        if (selfAuthored) {
            this.imgId = 0
        } else {
            this.imgId = imgId
        }
        this.senderID = senderID
        this.selfAuthored = selfAuthored
    }

    constructor(description: String, imgId: Int) {
        author = description
        this.imgId = imgId
    }
}