package com.comfy.powerline

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.comfy.powerline.data.Conversation
import com.comfy.powerline.data.SharedPreferencesHelper
import com.comfy.powerline.ui.theme.PowerlineTheme
import com.comfy.powerline.utils.ConversationDataList

class MessagesMenuV2 : ComponentActivity() {
    val jwt =
        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2ODkwNDg0MTIsImlhdCI6MTY4ODQ0MzYxMiwic3ViIjoiQ29tZnkiLCJ1aWQiOjF9.QGaSxq97328lqelMUzhZSgz_R9r3MrbZs8V8lsIPQDs"
    val api = ApiHandler()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val conversationList = api.getLatestMessageThreads(jwt)

        val conversation = getDummyConversation()

        setContent {
            LazyColumn {
                item{ ConversationRow(conversation = conversation)}
                }
            }
        }
    }

    private fun getDummyConversation(): Conversation {
        val newConversation = Conversation(
            senderName = "John",
            senderID = 1,
            recipientID = 1,
            body = "Nutsack",
            timestamp = "2021",
            recipientName = "Ben")
        //val Conversation = ConversationRow(newConversation)
        return newConversation
    }

    @Composable
    private fun ConversationRow(conversation: Conversation) {

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(conversation.senderName)
            Text(conversation.body)
        }
    }




