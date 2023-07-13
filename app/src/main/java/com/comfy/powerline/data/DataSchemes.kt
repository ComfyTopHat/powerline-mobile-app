package com.comfy.powerline.data

import android.media.Image
import androidx.compose.runtime.Immutable

data class Conversation(val senderName : String,
                        val senderID : Int,
                        val recipientID : Int,
                        val body : String,
                        val timestamp : String,
                        val recipientName : String)

data class Contact(val contactName : String,
                        val contactID : Int)

@Immutable
data class Message(
    val senderName: String,
    val senderID: Int,
    val recipientID: Int,
    val body: String,
    val timestamp: String,
    val image: Image?,
    val selfAuthored : Boolean)



