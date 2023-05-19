package com.comfy.powerline;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.comfy.powerline.utils.MessageDataList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiHandler extends AppCompatActivity  {
    MessageDataList[] contacts;
    String version;
    public MessageDataList[] getContacts(String clientID, String baseURL, String jwt) throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                try {
                    URL url = new URL(baseURL + "contacts/get/{recipient_id}?recipientID=" + clientID);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Authorization", jwt);
                    InputStream is = con.getInputStream();
                    BufferedReader bR = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuilder responseStrBuilder = new StringBuilder();
                    while ((line = bR.readLine()) != null) {
                        responseStrBuilder.append(line);
                    }
                    JSONArray result = new JSONArray(responseStrBuilder.toString());
                    contacts = jsonArrayToStringArray(result);
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        thread.join();
        return contacts;
    }
    
    private MessageDataList[] jsonArrayToStringArray(JSONArray ja) throws JSONException {
        MessageDataList[] dataList = new MessageDataList[ja.length()];
        for (int i =0; i < ja.length(); i++) {
            dataList[i] = new MessageDataList(ja.getString(i), android.R.drawable.ic_dialog_info);
        }
        return dataList;
    }

    protected void setVersion() throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                URL url = new URL(MainActivity.baseUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                InputStream is = con.getInputStream();
                BufferedReader bR = new BufferedReader( new InputStreamReader(is));
                String line;
                StringBuilder responseStrBuilder = new StringBuilder();
                while((line =  bR.readLine()) != null){
                    responseStrBuilder.append(line);
                }
                is.close();
                JSONObject result = new JSONObject(responseStrBuilder.toString());
                version = ("Powerline Version: " + result.get("Powerline Version"));
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        thread.join();
    }
}
