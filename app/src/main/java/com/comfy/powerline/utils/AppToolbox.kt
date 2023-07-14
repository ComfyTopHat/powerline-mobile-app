package com.comfy.powerline.utils

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.comfy.powerline.ApiHandler
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.net.HttpURLConnection

class AppToolBox(private val context: Context) {
    // Create the dataStore and give it a name same as user_pref
    // Create some keys we will use them to store and retrieve the data
    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_prefs")
        val JWT_KEY = stringPreferencesKey("jwt")
        var token = ""
        val api = ApiHandler()
    }

    suspend fun addContact(contactID : String, displayName : String, firstName : String, lastName : String) {
        val con: HttpURLConnection = api.getPOSTHTTPConnection("contacts/get/", token)
        val payload =
            "{\"contactID\":\"$contactID\",\"displayName\":\"$displayName\",\"firstName\":\"$firstName\", " +
                    "\"lastName\":\"$lastName\"}"
        // TODO: Add error handling
        val response: String = api.sendData(con, payload) }

    suspend fun loginAndSetTokenClientID(username: String, password: String): String {
        try {
            val con: HttpURLConnection = api.getPOSTHTTPConnection("clients/login/", "-")
            val payload =
                "{\"username\":\"$username\",\"password\":\"$password\"}"
            val response: String = api.sendData(con, payload)
            val result = JSONObject(response)
            token = result["token"] as String
            saveJWTToPreferencesStore(token)
            con.disconnect()
        } catch (e: Exception) {
        }
        return token
    }

    fun retrieveJWT(): String {
        var jwt = ""
        runBlocking { // this: CoroutineScope
            launch { // launch a new coroutine and continue
                val apptoolBox = AppToolBox(context)
                jwt = apptoolBox.getJWTFromPreferencesStore()
            }
        }
        return jwt
    }
    suspend fun saveJWTToPreferencesStore(jwt: String) {
        context.dataStore.edit { preferences ->
            preferences[JWT_KEY] = jwt
        }
    }

    fun saveFCMTokenToDB(jwt: String?) {
        var fcmToken = ""
        val ApiHandler = ApiHandler()
        runBlocking { // this: CoroutineScope
            launch { // launch a new coroutine and continue
                FirebaseMessaging.getInstance().token
                    .addOnCompleteListener { task: Task<String> ->
                        if (!task.isSuccessful) {
                            Log.w(
                                ContentValues.TAG,
                                "Fetching FCM registration token failed",
                                task.exception
                            )
                            return@addOnCompleteListener
                        }
                        try {
                            fcmToken = task.result
                            val payload = "{\"fcmToken\":\"$fcmToken\"}"
                            val con: HttpURLConnection =
                                ApiHandler.getPOSTHTTPConnection("fcm/", jwt)
                            ApiHandler.sendData(con, payload)
                        } catch (e: java.lang.Exception) {
                        }
                    }
            }
        }
    }

    suspend fun getJWTFromPreferencesStore(): String {
        val userNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
            preferences[JWT_KEY] ?: ""
        }
        token = "Bearer " + userNameFlow.first()
        return token
    }
}