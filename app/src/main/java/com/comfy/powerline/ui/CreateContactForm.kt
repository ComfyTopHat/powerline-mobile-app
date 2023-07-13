package com.comfy.powerline.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.comfy.powerline.ApiHandler

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateContactForm() {
    var validClient by remember { mutableStateOf(false) }
    Scaffold(
        topBar = { PLTopAppBar(title = "New Contact") }
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .padding(top = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        )
        {
            var displayName by remember { mutableStateOf("") }
            Text(
                text = "Create new contact",
                style = TextStyle(fontSize = 40.sp)
            )

            Spacer(modifier = Modifier.height(15.dp))

            Row() {
            var username by remember{ mutableStateOf("") }
                TextField(modifier = Modifier.width(260.dp),
                    label = { Text(text = "Email/Username*") },
                    value = username,
                    onValueChange = { username = it },
                )
                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val api = ApiHandler()
                        validClient = api.verifyClientExists(username, "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2ODkxMzI1NDgsImlhdCI6MTY4ODUyNzc0OCwic3ViIjoiQ29tZnkiLCJ1aWQiOjF9.g38E3_16ZxRAfMEcUJzhwnsrsFb3sQp-A7Tmti1DnEM" )
                    }) {
                    Text(text = "Search")
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            TextField(
                label = { Text(text = "Display Name") },
                value = displayName,
                enabled = validClient,
                onValueChange = { displayName = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Spacer(modifier = Modifier.height(15.dp))

            TextField(
                label = { Text(text = "First Name") },
                enabled = validClient,
                value = displayName,
                onValueChange = { displayName = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(15.dp))

            TextField(
                label = { Text(text = "Last Name") },
                value = displayName,
                enabled = validClient,
                onValueChange = { displayName = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(15.dp))

            Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "Add Contact")
                }
            }
        }
    }
}