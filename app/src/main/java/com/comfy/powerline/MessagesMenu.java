package com.comfy.powerline;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.UserHandle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.comfy.powerline.ui.main.SectionsPagerAdapter;
import com.comfy.powerline.databinding.ActivityMessagesMenuBinding;

public class MessagesMenu extends AppCompatActivity {
String user;
    private ActivityMessagesMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_menu);
        binding = ActivityMessagesMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.newMessageButton;
        String user = getUser();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Welcome back, " + user, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    protected String getUser() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = extras.getString("user");
        }
        return user;
    }
}