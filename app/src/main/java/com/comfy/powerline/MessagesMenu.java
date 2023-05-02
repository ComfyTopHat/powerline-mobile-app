package com.comfy.powerline;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
        addToSharedPreferences("json", String.valueOf(jsonMessage));
        startActivity(intent);
    }

    public void sendMessage(View v) {
        Intent intent = new Intent(MessagesMenu.this, sendMessage_v1.class);
        startActivity(intent);
    }

    @SuppressLint("CommitPrefEdits")
    private void addToSharedPreferences(String name, String value) {
        SharedPreferences.Editor editor = getSharedPreferences("AUTH", MODE_PRIVATE).edit();
        editor.putString(name, value);
        editor.apply();
    }

    private TextView getMessageTV(JSONObject jsonMessage) throws JSONException {
        TextView tv = new TextView(this);
        String row = (jsonMessage.getString("sentDateTime") + ": " + jsonMessage.getString("text"));
        tv.setText(row);
        View.OnClickListener oc = view -> openMessage(jsonMessage);
        tv.setOnClickListener(oc);
        return tv;
    }


    /**
     * Takes a String username
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
                JSONArray jsonResponse = new JSONArray();
                JSONObject jsonMessage = new JSONObject();
                String token = getToken();
                String user = getUser();
                URL url = new URL(baseUrl + "clients/messages/");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Authorization", "Bearer " + token);
                String payload = "{\"clientID\" : "+ user + "}";
                // Send the payload
                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = payload.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                        jsonResponse = new JSONArray(response.toString());
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                con.disconnect();
                LinearLayout ll = findViewById(R.id.linear_layout);
                for (int i =0; i < jsonResponse.length(); i++) {
                    jsonMessage = (JSONObject) jsonResponse.get(i);
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
