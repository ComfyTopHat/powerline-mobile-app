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
import com.comfy.powerline.utils.MessagesRecyclerListAdapter;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MessagesMenu extends AppCompatActivity {
    String baseUrl = MainActivity.baseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_menu);
        try {
            setRecyclerView(getMessageThreads());
        } catch (JSONException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private MessageDataList[] getMessageThreads() throws InterruptedException {
        ApiHandler api = new ApiHandler();
        return(api.getLatestMessageThreads(getClientID(), getToken()));
    }


    protected String getToken() {
        return "Bearer " + getSharedPreferences("AUTH", MODE_PRIVATE).getString("jwt", "-");
    }

    protected String getClientID() {
        return getSharedPreferences("AUTH", MODE_PRIVATE).getString("clientID", "-");
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

    private void setRecyclerView(MessageDataList[] mdl) throws JSONException {
        RecyclerView rv = findViewById(R.id.message_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        MessagesRecyclerListAdapter adapter = new MessagesRecyclerListAdapter(mdl);
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
}
