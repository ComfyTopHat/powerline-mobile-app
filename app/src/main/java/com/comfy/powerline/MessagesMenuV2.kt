package com.comfy.powerline

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources.Theme
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.comfy.powerline.data.Conversation
import com.comfy.powerline.ui.theme.BluePrimary
import com.comfy.powerline.ui.theme.PowerlineTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MessagesMenuV2 : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val jwt = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2ODkxMzI1NDgsImlhdCI6MTY4ODUyNzc0OCwic3ViIjoiQ29tZnkiLCJ1aWQiOjF9.g38E3_16ZxRAfMEcUJzhwnsrsFb3sQp-A7Tmti1DnEM"
        val api = ApiHandler()
        messagesList = api.getLatestMessageThreads(jwt)
        setContent {
            MessagesScaffold()
        }
    }
}

private lateinit var messagesList: ArrayList<Conversation>

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScaffold() {
    Scaffold(
        topBar = {
            TopAppBar(colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = BluePrimary),
                title = { Text(
                text = "Messages",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White
            ) },
                navigationIcon = {
                    IconButton(onClick = {
                       // Toast.makeText(context, "Nav Button", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Go back",
                        )
                    }
                })
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = BluePrimary,
                onClick = { /*TODO*/ },
                content = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_person_24),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            )
        },
        content = {
                Column {
                    Messages1()
                }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppbarMessages(context: Context = LocalContext.current.applicationContext) {
    TopAppBar(
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = BluePrimary),
        title = {
            Text(
                text = "Messages",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                Toast.makeText(context, "Nav Button", Toast.LENGTH_SHORT).show()
            }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Go back",
                )
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Messages1() {

    // This is to check if the messagesList has data or not
    // Initially it is false
    var listPrepared by remember {
        mutableStateOf(false)
    }

    if (listPrepared) {
        TopAppbarMessages()
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(messagesList) { itemObject ->
                MessagesItemStyle(item = itemObject)
            }
        }
    }

    // This is called when the user first opens the activity
    // So, add data to messagesList here
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            listPrepared = true
        }
    }

}

@Composable
fun MessagesItemStyle(
    item: Conversation,
    context: Context = LocalContext.current
) {
    Box(
        modifier = Modifier
            .clickable(
                onClick = {
                    context.startActivity(Intent(context, MessageThreadV2::class.java))
                    //Toast.makeText(context, item.senderName, Toast.LENGTH_SHORT).show()
                }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Profile image
            Image(
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .size(56.dp),
                painter = painterResource(id = R.drawable.baseline_person_24),
                contentDescription = item.senderName
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // Text that shows the name
                    Text(
                        text = item.senderName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            //fontFamily = FontFamily(Font(R.font.roboto_medium, FontWeight.Medium)),
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    )

                    // Text that shows the time
                    Text(
                        text = item.timestamp,
                        style = TextStyle(
                            //fontFamily = FontFamily(Font(R.font.roboto_regular, FontWeight.Normal)),
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    )

                }
                // Text that shows the message
                Text(
                    modifier = Modifier
                        .padding(top = 2.dp),
                    text = item.body,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                    //    fontFamily = FontFamily(Font(R.font.roboto_regular, FontWeight.Normal)),
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                )

            }
        }
    }
}
