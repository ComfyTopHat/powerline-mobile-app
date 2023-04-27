package com.comfy.powerline;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.github.javafaker.Faker;
import com.comfy.powerline.utils.Auth;

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

    private void displayInvalidPWAlert(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Invalid Password")
                .setMessage("Password must contain:\n1. A lowercase letter\n2. An uppercase letter\n3. A number\n4. At least 7 characters")
                .setNegativeButton("Okay", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void displaySuccessMessage(Context context, String username, String passLastDigs) {
        new AlertDialog.Builder(context)
                .setTitle("Account Created")
                .setMessage("Please note details as accounts CANNOT be recovered: \nUsername: " + username +"\nPassword: ******" + passLastDigs)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    public void submitNewUser(View v) throws InterruptedException {
        EditText passwordWidget = findViewById(R.id.inputPassword);
        Editable password = passwordWidget.getText();
        String passwordStr = passwordWidget.getText().toString().trim();
        EditText passwordConfirmWidget = findViewById(R.id.inputConfirmPassword);
        String passwordConfirm = passwordConfirmWidget.getText().toString().trim();
        if (Auth.isValid(passwordStr) && passwordStr.equals(passwordConfirm) && (passwordStr.length() > 6)) {
            EditText userNameWidget = findViewById(R.id.usernameInput);
            Editable username = userNameWidget.getText();
            EditText emailWidget = findViewById(R.id.emailInput);
            // Editable email = emailWidget.getText();
            Thread run = getPOSTHTTPThread(username, password);
            run.start();
            run.join();
            this.displaySuccessMessage(CreateAccountActivity.this, String.valueOf(username), passwordStr.substring(passwordStr.length()-3));
        }
        else {
            this.displayInvalidPWAlert(CreateAccountActivity.this);
        }
    }
}