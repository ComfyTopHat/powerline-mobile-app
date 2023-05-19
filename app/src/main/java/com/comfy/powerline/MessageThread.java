package com.comfy.powerline;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.comfy.powerline.databinding.ActivityContactsBinding;

public class MessageThread extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_thread);
        TextView tv = findViewById(R.id.contactName);
        String contactName = getIntent().getStringExtra("contact");
        deleteSharedPreferences("contact");
        tv.setText(contactName);
    }
}