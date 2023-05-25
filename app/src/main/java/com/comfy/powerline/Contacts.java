package com.comfy.powerline;

import android.os.Bundle;

import com.comfy.powerline.utils.ContactDataList;
import com.comfy.powerline.utils.ContactRecyclerListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

import com.comfy.powerline.databinding.ActivityContactsBinding;

public class Contacts extends AppCompatActivity {
ContactDataList[] contacts;
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
        ContactRecyclerListAdapter adapter = new ContactRecyclerListAdapter(contacts);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        fab.setOnClickListener(view -> Snackbar.make(view, "TBD", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

    private ContactDataList[] getContacts() throws InterruptedException {
        ApiHandler api = new ApiHandler();
        String clientID = getSharedPreferences("AUTH", MODE_PRIVATE).getString("clientID", "-");
        String jwt = "Bearer " + getSharedPreferences("AUTH", MODE_PRIVATE).getString("jwt", "-");
        return api.getContacts(clientID, jwt);
    }
}