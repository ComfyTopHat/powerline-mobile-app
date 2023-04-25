package com.comfy.powerline;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.util.LogPrinter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

String baseUrl = "https://powerline.azurewebsites.net/";
String version = "";
String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            getPowerlineVer();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
// TO-DO add shared preferences listener
    private Thread getHTTPThread() {
        Runnable httpThread = () -> {
            try {
                URL url = new URL(baseUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                InputStream is = con.getInputStream();
                BufferedReader bR = new BufferedReader( new InputStreamReader(is));
                String line;
                StringBuilder responseStrBuilder = new StringBuilder();
                while((line =  bR.readLine()) != null){
                    responseStrBuilder.append(line);
                }
                is.close();
                JSONObject result = new JSONObject(responseStrBuilder.toString());
                version = ("Powerline Version: " + result.get("Powerline Version"));
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        };
        return new Thread(httpThread);
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
                con.disconnect();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                token = "Invalid login";
            }
        };
        return new Thread(httpThread);
    }
    private void getPowerlineVer() throws InterruptedException {
        TextView tv = findViewById(R.id.VersionNumber);
        Thread run = getHTTPThread();
        run.start();
        run.join();
        tv.setText(version);
    }

    private void openSuccessfulLogin() {
        Intent intent = new Intent(MainActivity.this, MessagesMenu.class);
        String jwt = getFromSharedPreferences();
        intent.putExtra("jwt", jwt);
        startActivity(intent);
    }

    private void decodeToken(String jwt) {
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = null;
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            decoder = Base64.getUrlDecoder();
            String header = new String(decoder.decode(chunks[0]));
            String payload = new String(decoder.decode(chunks[1]));
        }
    }

    @SuppressLint("CommitPrefEdits")
    private void addToSharedPreferences(String token) {
        SharedPreferences.Editor editor = getSharedPreferences("AUTH", MODE_PRIVATE).edit();
        editor.putString("jwt", token);
        editor.apply();
    }

    private String getFromSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences("AUTH", MODE_PRIVATE);
        String jwt = prefs.getString("jwt", "-");
        return jwt;
    }

    public void getToken(View v) throws InterruptedException {
        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Editable username = emailInput.getText();
        Editable password = passwordInput.getText();
        Thread run = getPOSTHTTPThread(username, password);
        run.start();
        run.join();
        if (!Objects.equals(token, "Invalid login")){
            addToSharedPreferences(token);
            openSuccessfulLogin();
        }
    }
    public void createAccount(View view) {
        Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
        startActivity(intent);
    }
}
