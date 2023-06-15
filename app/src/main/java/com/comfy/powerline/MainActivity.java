package com.comfy.powerline;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
ApiHandler api = new ApiHandler();
int clientID = 0;
String version = "";
String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            getPowerlineVer();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveFCMToken() {
        api.saveFCMTokenToDB(token, String.valueOf(clientID));
    }

    private Thread getPOSTHTTPThread(Editable username, Editable password) {
        Runnable httpThread = () -> {
            try {
                URL url = new URL(baseUrl + "clients/login/");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                String payload = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
                byte[] out = payload.getBytes(StandardCharsets.UTF_8);
                OutputStream stream = con.getOutputStream();
                stream.write(out);
                InputStream is = con.getInputStream();
                BufferedReader bR = new BufferedReader( new InputStreamReader(is));
                String line;
                StringBuilder responseStrBuilder = new StringBuilder();
                while((line =  bR.readLine()) != null){
                    responseStrBuilder.append(line);
                }
                is.close();
                JSONObject result = new JSONObject(responseStrBuilder.toString());
                token = ((String) result.get("token"));
                clientID = (int) result.get("clientID");
                con.disconnect();
            } catch (IOException e) {
                TextView tv = findViewById(R.id.invalidInput);
                tv.setText("Error communicating with server, please try again later");
                tv.setVisibility(View.VISIBLE);
                throw new RuntimeException(e);
            } catch (JSONException e) {
                token = "Invalid login";
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
        String jwt = getFromSharedPreferences("jwt");
        EditText tv = findViewById(R.id.emailInput);
        Editable user = tv.getText();
        addToSharedPreferences("clientID", String.valueOf(clientID));
        intent.putExtra("jwt", jwt);
        intent.putExtra("user", user);
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

    private String getFromSharedPreferences(String key) {
        SharedPreferences prefs = getSharedPreferences("AUTH", MODE_PRIVATE);
        return prefs.getString(key, "-");
    }

    public void getToken(View v) throws InterruptedException, IOException {
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

        Thread run = getPOSTHTTPThread(username, password);
        run.start();
        run.join();
        if (!Objects.equals(token, "Invalid login")){
            addToSharedPreferences("jwt",token);
            addToSharedPreferences("user", String.valueOf(username));
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

    static String baseUrl = "https://powerline.azurewebsites.net/";
}
