package com.comfy.powerline;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;

import com.comfy.powerline.utils.ContactDataList;
import com.comfy.powerline.utils.ContactRecyclerListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

import com.comfy.powerline.databinding.ActivityContactsBinding;

import java.util.List;

public class Contacts extends AppCompatActivity implements SearchView.OnQueryTextListener {
List<ContactDataList> contacts;
ContactRecyclerListAdapter adapter;
SearchView editSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.comfy.powerline.databinding.ActivityContactsBinding binding = ActivityContactsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        try {
            contacts = getContacts();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        setRecyclerView(contacts);
        editSearch = findViewById(R.id.contact_search_view);
        editSearch.setOnQueryTextListener(this);
    }

    private void setRecyclerView(List<ContactDataList> contacts) {
        RecyclerView rv = findViewById(R.id.contact_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        adapter = new ContactRecyclerListAdapter(contacts);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    public void switchToSendMessage(View v) {
        Intent intent = new Intent(Contacts.this, sendMessage_v1.class);
        intent.putExtra("contacts", (CharSequence) contacts);
        startActivity(intent);
    }

    private List<ContactDataList> getContacts() throws InterruptedException {
        ApiHandler api = new ApiHandler();
        String clientID = getSharedPreferences("AUTH", MODE_PRIVATE).getString("clientID", "-");
        String jwt = "Bearer " + getSharedPreferences("AUTH", MODE_PRIVATE).getString("jwt", "-");
        return api.getContacts(clientID, jwt);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
         adapter.filter(contacts, s);
        return false;
    }
}