package com.comfy.powerline;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
ApiHandler api = new ApiHandler();
static String baseUrl = "https://powerline.azurewebsites.net/";
int clientID = 0;
private static final int NOTIFICATION_PERMISSION_CODE = 100;
String version = "";
String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            getPowerlineVer();

            checkPermission(NOTIFICATION_PERMISSION_CODE);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveFCMToken() {
        api.saveFCMTokenToDB(token, String.valueOf(clientID));
    }

    //TODO: Remove this and migrate to AppToolbox
    private Thread loginAndSetTokenClientID(Editable username, Editable password) {
        Runnable httpThread = () -> {
            try {
                HttpURLConnection con = api.getPOSTHTTPConnection("clients/login/", "-");
                String payload = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
                String response = api.sendData(con, payload);
                JSONObject result = new JSONObject(response);
                token = ((String) result.get("token"));
                clientID = (int) result.get("clientID");
                con.disconnect();
            } catch (IOException | InterruptedException | JSONException e) {
                TextView tv = findViewById(R.id.invalidInput);
                tv.setText(R.string.error_communicating_with_server_please_try_again_later);
                tv.setVisibility(View.VISIBLE);
                throw new RuntimeException(e);
            }
        };
        return new Thread(httpThread);
    }

    private void getPowerlineVer() throws InterruptedException {
        TextView tv = findViewById(R.id.VersionNumber);
        api.setVersion();
        version = api.version;
        tv.setText(version);
    }

    private void openSuccessfulLogin() {
        Intent intent = new Intent(MainActivity.this, MessagesMenu.class);
        addToSharedPreferences("clientID", String.valueOf(clientID));
        saveFCMToken();
        startActivity(intent);
        finish();
    }

    @SuppressLint("CommitPrefEdits")
    protected void addToSharedPreferences(String name, String value) {
        SharedPreferences.Editor editor = getSharedPreferences("AUTH", MODE_PRIVATE).edit();
        editor.putString(name, value);
        editor.apply();
    }


    public void getToken(View v) throws InterruptedException {
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Editable username = emailInput.getText();
        Editable password = passwordInput.getText();
        Thread run = loginAndSetTokenClientID(username, password);
        run.start();
        run.join();
        if (!Objects.equals(token, "Invalid login")){
            addToSharedPreferences("jwt",token);
            //TODO: Remove this if its no longer used
            //addToSharedPreferences("user", String.valueOf(username));
            openSuccessfulLogin();
        }
        else {
            TextView tv = findViewById(R.id.invalidInput);
            tv.setVisibility(View.VISIBLE);
        }
    }

    public void createAccount(View view) {
        Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
        startActivity(intent);
    }



    // Function to check and request permission.
    public void checkPermission(int requestCode)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.POST_NOTIFICATIONS }, requestCode);
        }
        else {
            Log.e("Powerline", "Notification permission already granted");
        }
    }
}


