package com.comfy.powerline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.comfy.powerline.ui.Login
import com.comfy.powerline.ui.theme.PowerlineTheme

class LoginActivity : ComponentActivity() {

    companion object {
        @JvmStatic
        val baseURL = "https://powerline-app.com/"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PowerlineTheme {
                Login()
            }
        }
    }
}