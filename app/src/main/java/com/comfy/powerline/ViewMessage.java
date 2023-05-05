package com.comfy.powerline;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.TextView;

import com.comfy.powerline.databinding.ActivityViewMessageBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class ViewMessage extends AppCompatActivity {
JSONObject jsonMessage = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityViewMessageBinding binding = ActivityViewMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FloatingActionButton fab = binding.fab;
        try {
            getJSONFromExtras();
            setTextFields(binding);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        fab.setOnClickListener(view -> Snackbar.make(view, "Reply function in progress", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

    private void setTextFields(ActivityViewMessageBinding binding) throws JSONException {
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(jsonMessage.getString("sender"));
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        TextView tv = findViewById(R.id.directMessageText);
        String dateTime = jsonMessage.getString("timestamp").split("\\.", 1)[0];
        tv.setText(dateTime.substring(0, dateTime.length()-8));
        tv.append(System.getProperty("line.separator"));
        tv.append(jsonMessage.getString("text"));

    }

    private void getJSONFromExtras() throws JSONException {
        SharedPreferences prefs = getSharedPreferences("AUTH", MODE_PRIVATE);
        jsonMessage = new JSONObject(prefs.getString("json", "-"));
    }
}