package com.comfy.powerline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.comfy.powerline.utils.ContactDataList;
import com.comfy.powerline.utils.ContactRecyclerListAdapter;

import java.util.Arrays;

public class sendMessage_v1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message_v1);
        Parcelable[] myParcelableObject = getIntent().getParcelableArrayExtra("contacts");
        ContactDataList[] contacts = parcelToCDLArray(myParcelableObject);
    }

    private ContactDataList[] parcelToCDLArray(Parcelable[] parcel) {
        ContactDataList[] contacts = new ContactDataList[parcel.length];
        for (int i = 0; i < parcel.length; i++) {
            contacts[i] = (ContactDataList) parcel[i];
        }
        return contacts;
    }

    private void setRecyclerView(ContactDataList[] contacts) {

    }
}