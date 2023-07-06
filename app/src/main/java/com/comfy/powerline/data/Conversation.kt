package com.comfy.powerline.data

import android.media.Image
import androidx.compose.runtime.Composable

data class Conversation(val senderName : String,
                        val senderID : Int,
                        val recipientID : Int,
                        val body : String,
                        val timestamp : String,
                        val recipientName : String)


@Composable
private fun ConversationRow(conversation: Conversation) = Unit
