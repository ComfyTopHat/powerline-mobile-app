package com.comfy.powerline;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.comfy.powerline.utils.MessageDataList;
import com.comfy.powerline.utils.MessagesRecyclerListAdapter;
import com.comfy.powerline.utils.AppToolbox;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class MessageThread extends AppCompatActivity {
    List<MessageDataList> messageThread;
    String clientID;
    String senderID;
    ApiHandler api = new ApiHandler();
    String jwt;

    @Override
    //TODO: Cleanup this onCreate mess
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_thread);
        TextView tv = findViewById(R.id.contactName);
        jwt = "Bearer " + getSharedPreferences("AUTH", MODE_PRIVATE).getString("jwt", "-");
        clientID = getSharedPreferences("AUTH", MODE_PRIVATE).getString("clientID", "-");
        senderID = getIntent().getStringExtra("senderID");
        deleteSharedPreferences("contact");
        getIntent().removeExtra("senderID");
        String contactExtra = getIntent().getStringExtra("contact");
        try {
            // If the message is getting sent to a new person then:
            if (senderID.equals("NEW")) {
                contactExtra = contactExtra.split(":")[1].trim();
                EditText et = findViewById(R.id.messageInput);
                et.setText("User does not exist");
                et.setEnabled(false);
                senderID = api.getClientID(contactExtra);
            }
            tv.setText(contactExtra);
            messageThread = api.getThreadMessages(clientID, senderID, jwt);
        } catch (Exception e) {
            // TODO: Display error for invalid message return
        }
        setRecyclerView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendMessage(View v) throws InterruptedException, IOException {
        EditText et = findViewById(R.id.messageInput);
        String messageText = String.valueOf(et.getText());
        api.sendMessage(jwt, clientID, senderID, messageText);
        // After the message is sent, populate a local copy and notify the adapter
        // When the user reloads the activity then it will pull the server-side version
        RecyclerView rv = findViewById(R.id.message_thread_recycler);
        messageThread.add(new MessageDataList("", messageText, 0, String.valueOf(LocalDate.now()), "Self", true, false));
        AppToolbox.notifyNewMessageAdded(Objects.requireNonNull(rv.getAdapter()));
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
