package com.comfy.powerline.network;

import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.comfy.powerline.R;

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

public class apiHandler extends AppCompatActivity {
    String baseUrl = "https://powerline.azurewebsites.net/";
    String token = "";
    private Thread getPOSTHTTPThread(String key1, Editable value1, String key2, Editable value2, String function) {
        Runnable httpThread = () -> {
            String token = "";
            try {
                URL url = new URL(baseUrl + function);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                String payload = "{\""+key1+"\":\"" + value1 + "\",\""+key2+"\":\"" + value2 + "\"}";
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
                token = ("Token: " +  (String) result.get("token"));
                con.disconnect();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                token = "Invalid login";
            }
        };
        return new Thread(httpThread);
    }
    public Thread getToken(Editable username, Editable password) throws InterruptedException {
        return getPOSTHTTPThread("username", username, "password", password, "clients/login/");
    }


}
