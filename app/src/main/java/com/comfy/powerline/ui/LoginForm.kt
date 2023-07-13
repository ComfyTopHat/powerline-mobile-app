package com.comfy.powerline.ui
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.comfy.powerline.MessagesMenuV2
import com.comfy.powerline.R
import com.comfy.powerline.utils.AppToolBox
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login() {
    val context = LocalContext.current
    Box() {
        Column(
            Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Bottom),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_person_24),
                null,
                Modifier.size(80.dp),
                tint = Color.White
            )
            var username by remember{ mutableStateOf("") }
            var password by remember{ mutableStateOf("") }
            var passwordVisible by rememberSaveable { mutableStateOf(false) }
            TextField(
                value = username,
                onValueChange = {
                    username = it
                },
                label = { Text("Username") }
            )
            TextField(
                value = password,
                label = { Text("Password") },
                onValueChange = {
                    password = it
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff
                    IconButton(onClick = {passwordVisible = !passwordVisible}){
                        Icon(imageVector  = image, "Visiblity Icon")
                    }
                })
            Button(onClick = {
                val AppToolBox = AppToolBox(context)
                runBlocking { // this: CoroutineScope
                    launch { // launch a new coroutine and continue
                        AppToolBox.loginAndSetTokenClientID(username, password)
                    }
                }
                val i = Intent(context , MessagesMenuV2::class.java)
                startActivity(context, i, null)
            }, modifier = Modifier.fillMaxWidth()) {
                Text("SIGN IN", Modifier.padding(vertical = 8.dp))
            }
            Divider(
                color = Color.White.copy(alpha = 0.3f),
                thickness = 1.dp,
                modifier = Modifier.padding(top = 48.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account?", color = Color.Black)
                TextButton(onClick = {}) {
                    Text("SIGN UP")
                }
            }
        }
    }
    }