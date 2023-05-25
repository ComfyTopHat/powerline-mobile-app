package com.comfy.powerline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.comfy.powerline.utils.MessageDataList;
import com.comfy.powerline.utils.MessagesRecyclerListAdapter;

public class MessageThread extends AppCompatActivity {
    MessageDataList[] messageThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_thread);
        TextView tv = findViewById(R.id.contactName);
        String contactName = getIntent().getStringExtra("contact");
        String clientID = getSharedPreferences("AUTH", MODE_PRIVATE).getString("clientID", "-");
        String senderID = getIntent().getStringExtra("senderID");
        deleteSharedPreferences("contact");
        tv.setText(contactName);
        String jwt = "Bearer " + getSharedPreferences("AUTH", MODE_PRIVATE).getString("jwt", "-");
        try {
            ApiHandler api = new ApiHandler();
            // TO-DO: Need to get the clientID of the sender here
            messageThread = api.getThreadMessages(clientID, senderID, jwt);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
