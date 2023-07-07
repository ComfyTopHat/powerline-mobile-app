package com.comfy.powerline.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

class AppToolBox : AppCompatActivity() {
    private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
        name = "auth"
    )
    private val JWT = stringPreferencesKey("jwt")

    suspend fun saveJWTToPreferencesStore(jwt: String) {
        userPreferencesDataStore.edit { preferences ->
            preferences[JWT] = jwt
        }
    }

    fun getUserFromPreferencesStore() {
        userPreferencesDataStore.data
        println("A")
    }



    }
