package com.comfy.powerline

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.comfy.powerline.ui.CreateContactForm
import com.comfy.powerline.ui.theme.PowerlineTheme

class CreateContactActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PowerlineTheme {
                CreateContactForm()
            }
        }
    }
}

