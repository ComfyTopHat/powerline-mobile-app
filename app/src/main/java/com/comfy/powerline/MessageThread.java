package com.comfy.powerline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comfy.powerline.utils.MessageDataList;
import com.comfy.powerline.utils.MessagesRecyclerListAdapter;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.io.IOException;

public class MessageThread extends AppCompatActivity {
    MessageDataList[] messageThread;
    String clientID;
    String senderID;
    ApiHandler api = new ApiHandler();
    String jwt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_thread);
        TextView tv = findViewById(R.id.contactName);
        jwt = "Bearer " + getSharedPreferences("AUTH", MODE_PRIVATE).getString("jwt", "-");
        String contactName = getIntent().getStringExtra("contact");
        clientID = getSharedPreferences("AUTH", MODE_PRIVATE).getString("clientID", "-");
        senderID = getIntent().getStringExtra("senderID");
        deleteSharedPreferences("contact");
        deleteSharedPreferences("senderID");
        tv.setText(contactName);
        try {
            messageThread = api.getThreadMessages(clientID, senderID, jwt);
            setRecyclerView();
        } catch (Exception e) {
            // TODO: Display error for invalid message return
        }
    }

    public void sendMessage(View v) throws InterruptedException, IOException {
        EditText et = findViewById(R.id.messageInput);
        String messageText = String.valueOf(et.getText());
        api.sendMessage(jwt, clientID, senderID, messageText);
        messageThread = api.getThreadMessages(clientID, senderID, jwt);
        setRecyclerView();
    }

    private void setRecyclerView() {
        RecyclerView rv = findViewById(R.id.message_thread_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        MessagesRecyclerListAdapter adapter = new MessagesRecyclerListAdapter(messageThread);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }
    }
