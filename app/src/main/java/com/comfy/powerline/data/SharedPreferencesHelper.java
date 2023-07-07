package com.comfy.powerline.data;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPreferencesHelper(Context context) {
        this.sharedPreferences = context.getSharedPreferences("AUTH", MODE_PRIVATE);
        this.editor = sharedPreferences.edit(); }

    public String GetPreferences(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void SavePreferences(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }
}