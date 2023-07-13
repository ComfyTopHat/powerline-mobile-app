package com.comfy.powerline

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.comfy.powerline.ui.ContactsScaffold
import com.comfy.powerline.ui.theme.PowerlineTheme
import com.comfy.powerline.utils.AppToolBox

class ContactsActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appToolBox = AppToolBox(applicationContext)
        val jwt = appToolBox.retrieveJWT()
        val api = ApiHandler()
        setContent {
            PowerlineTheme {
                ContactsScaffold(topbarTitle = "Contacts", contactList = api.getContacts(jwt))
            }
        }
    }
}