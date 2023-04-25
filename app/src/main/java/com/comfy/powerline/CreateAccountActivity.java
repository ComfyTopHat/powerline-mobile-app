package com.comfy.powerline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.github.javafaker.Faker;

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

public class CreateAccountActivity extends AppCompatActivity {
    String base_url = "https://powerline.azurewebsites.net/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }
    public void generateUsername(View v) {
        Faker faker = new Faker();
        String user = faker.superhero().prefix()+faker.name().firstName()+faker.address().buildingNumber();
        EditText et = findViewById(R.id.usernameInput);
        et.setText(user);
    }

    public void onBack(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private Thread getPOSTHTTPThread(Editable username, Editable password) {
        Runnable httpThread = () -> {
            try {
                URL url = new URL(base_url + "clients/");
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
                BufferedReader bR = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder responseStrBuilder = new StringBuilder();
                while ((line = bR.readLine()) != null) {
                    responseStrBuilder.append(line);
                }
                is.close();
                JSONObject result = new JSONObject(responseStrBuilder.toString());
                //  token = ("Token: " +  (String) result.get("token"));
                con.disconnect();
            } catch (IOException | JSONException e) {
            }
        };
        return new Thread(httpThread);
    }

    public void submitNewUser(View v) throws InterruptedException {
        EditText userNameWidget = findViewById(R.id.usernameInput);
        Editable username = userNameWidget.getText();
        EditText emailWidget = findViewById(R.id.emailInput);
       // Editable email = emailWidget.getText();
        EditText passwordWidget = findViewById(R.id.inputPassword);
        Editable password = passwordWidget.getText();
        EditText passwordConfirmWidget = findViewById(R.id.inputConfirmPassword);
        Editable passwordConfirm = passwordConfirmWidget.getText();
        Thread run = getPOSTHTTPThread(username, password);
        run.start();
        run.join();
    }
}