package com.comfy.powerline;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comfy.powerline.utils.MessageDataList;
import com.comfy.powerline.utils.RecyclerViewListAdapter;

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
        addToSharedPreferences("json", String.valueOf(jsonMessage));
        startActivity(intent);
    }

    public void sendMessage(View v) {
        Intent intent = new Intent(MessagesMenu.this, Contacts.class);
        intent.putExtra("url", baseUrl);
        startActivity(intent);
    }

    @SuppressLint("CommitPrefEdits")
    private void addToSharedPreferences(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences("AUTH", MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void setRecyclerView(JSONArray ja) throws JSONException {
        MessageDataList[] newList = new MessageDataList[ja.length()];
        RecyclerView rv = findViewById(R.id.message_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        for (int i=0; i< ja.length(); i++) {
            JSONObject jsonMessage = (JSONObject) ja.opt(i);
            MessageDataList convoThread =  new MessageDataList(jsonMessage.getString("sender"), android.R.drawable.ic_dialog_info, "01-01-2020");
            newList[i] = convoThread;
        }
        RecyclerViewListAdapter adapter = new RecyclerViewListAdapter(newList);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
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
                setRecyclerView(result);
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        };
        return new Thread(httpThread);
    }
}
