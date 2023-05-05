package com.comfy.powerline;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;

public class MessagesMenu extends AppCompatActivity {
    String baseUrl = MainActivity.baseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_menu);
        Thread thMessages = getMessages();
        thMessages.start();
        try {
            thMessages.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected String getToken() {
        return getSharedPreferences("AUTH", MODE_PRIVATE).getString("jwt", "-");
    }

    private void openMessage(JSONObject jsonMessage) {
        Intent intent = new Intent(MessagesMenu.this, ViewMessage.class);
        addToSharedPreferences(String.valueOf(jsonMessage));
        startActivity(intent);
    }

    public void sendMessage(View v) {
        Intent intent = new Intent(MessagesMenu.this, sendMessage_v1.class);
        startActivity(intent);
    }

    @SuppressLint("CommitPrefEdits")
    private void addToSharedPreferences(String value) {
        SharedPreferences.Editor editor = getSharedPreferences("AUTH", MODE_PRIVATE).edit();
        editor.putString("json", value);
        editor.apply();
    }

    private LocalDate getDate(String longDateTime) {
        String date = longDateTime.split("T")[0];
        LocalDate dateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dateTime = LocalDate.parse(date);
        }
        return dateTime;
    }

    private TextView getMessageTV(JSONObject jsonMessage) throws JSONException {
        TextView tv = new TextView(this);
        String row = (jsonMessage.getString("sender") + ": " + jsonMessage.getString("text"));
        tv.setText(row);
        tv.setTextSize(25);
        View.OnClickListener oc = view -> openMessage(jsonMessage);
        tv.setOnClickListener(oc);
        return tv;
    }


    /**
     * POTENTIALLY REDUNDANT? Re-visit this
     * @return an Int clientID
     */
    protected String getUser() {
        try {
            String username = getSharedPreferences("AUTH", MODE_PRIVATE).getString("user", "-");
            URL url = new URL(baseUrl + "clients/get/clientID/" + username);
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
            return responseStrBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Thread getMessages() {
        Runnable httpThread = () -> {
            try {
                String token = "Bearer " + getToken();
                String user = getUser();
                URL url = new URL(baseUrl + "contacts/get/thread/{recipient_id}?recipientID=" + user);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Authorization", token);
                InputStream is = con.getInputStream();
                BufferedReader bR = new BufferedReader( new InputStreamReader(is));
                String line;
                StringBuilder responseStrBuilder = new StringBuilder();
                while((line =  bR.readLine()) != null){
                    responseStrBuilder.append(line);
                }
                is.close();
                JSONArray result = new JSONArray(responseStrBuilder.toString());
                LinearLayout ll = findViewById(R.id.linear_layout);
                for (int i=0; i< result.length(); i++) {
                    JSONObject jsonMessage = (JSONObject) result.opt(i);
                    TextView tv = getMessageTV(jsonMessage);
                    ll.addView(tv);
                }
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        };
        return new Thread(httpThread);
    }
}
