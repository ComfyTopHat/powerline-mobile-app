package com.comfy.powerline;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comfy.powerline.utils.ConversationDataList;
import com.comfy.powerline.utils.ConversationDataListAdapter;

import org.json.JSONException;
import java.util.List;

public class MessagesMenu extends AppCompatActivity {
    String baseUrl = MainActivity.baseUrl;

    @RequiresApi(api = Build.VERSION_CODES.O)
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<ConversationDataList> getMessageThreads() throws InterruptedException {
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

    private void setRecyclerView(List<ConversationDataList> CDL) throws JSONException {
        RecyclerView rv = findViewById(R.id.message_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        ConversationDataListAdapter adapter = new ConversationDataListAdapter(CDL);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }
}
