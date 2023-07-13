package com.comfy.powerline.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.comfy.powerline.CreateContactActivity
import com.comfy.powerline.MessageThreadV2
import com.comfy.powerline.R
import com.comfy.powerline.TopAppbarMessages
import com.comfy.powerline.data.Contact
import com.comfy.powerline.ui.theme.BluePrimary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PLTopAppBar(title : String) {
    TopAppBar(colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = BluePrimary),
        title = { Text(
            text = title,
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
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScaffold(topbarTitle : String,
                     contactList: ArrayList<Contact>,
                     context: Context = LocalContext.current) {
    Scaffold(
        topBar = {
            PLTopAppBar(title = topbarTitle)
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = BluePrimary,
                onClick = { context.startActivity(Intent(context, CreateContactActivity::class.java)) },
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add,"")
            }
        },
        content = {
            Column {
                GetContacts(contactList)
            }
        }
    )
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GetContacts(contactList : ArrayList<Contact>) {
    // This is to check if the contact has data or not
    // Initially it is false
    var listPrepared by remember {
        mutableStateOf(false)
    }
    if (listPrepared) {
        TopAppbarMessages()
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(contactList) { itemObject ->
                ContactsItemStyle(item = itemObject)
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
fun ContactsItemStyle(
    item: Contact,
    context: Context = LocalContext.current
) {
    Box(
        modifier = Modifier
            .clickable(
                onClick = {
                    context.startActivity(Intent(context, MessageThreadV2::class.java))
                }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Contact image
            // TODO: Update this to a dynamically generated image
            Image(
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .size(56.dp),
                painter = painterResource(id = R.mipmap.powerline),
                contentDescription = "ABC"
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
                    // Text that shows the  contact name
                    Text(
                        text = item.contactName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    )
                }
            }
        }
    }
}


