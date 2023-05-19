package com.comfy.powerline;

import android.content.Intent;
import android.os.Bundle;

import com.comfy.powerline.utils.MessageDataList;
import com.comfy.powerline.utils.RecyclerViewListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.comfy.powerline.ui.main.SectionsPagerAdapter;
import com.comfy.powerline.databinding.ActivityContactsBinding;

public class Contacts extends AppCompatActivity {
MessageDataList[] contacts;
    private ActivityContactsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FloatingActionButton fab = binding.addContactFab;
        try {
            contacts = getContacts();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        RecyclerView rv = findViewById(R.id.contact_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        RecyclerViewListAdapter adapter = new RecyclerViewListAdapter(contacts);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        fab.setOnClickListener(view -> Snackbar.make(view, "TBD", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

    private MessageDataList[] getContacts() throws InterruptedException {
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        ApiHandler api = new ApiHandler();
        String clientID = getSharedPreferences("AUTH", MODE_PRIVATE).getString("clientID", "-");
        String jwt = "Bearer " + getSharedPreferences("AUTH", MODE_PRIVATE).getString("jwt", "-");
        return api.getContacts(clientID, url, jwt);
    }
}