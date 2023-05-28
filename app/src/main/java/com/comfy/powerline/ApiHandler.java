package com.comfy.powerline;

import static com.comfy.powerline.MainActivity.baseUrl;

import androidx.appcompat.app.AppCompatActivity;

import com.comfy.powerline.utils.ContactDataList;
import com.comfy.powerline.utils.MessageDataList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

public class ApiHandler extends AppCompatActivity  {
    ContactDataList[] contactResponse;
    JSONArray response;
    MessageDataList[] messageResponse;
    String version;
    ContactDataList[] getContacts(String clientID, String jwt) throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                try {
                    URL url = new URL(baseUrl + "contacts/get/{recipient_id}?recipientID=" + clientID);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Authorization", jwt);
                    InputStream is = con.getInputStream();
                    BufferedReader bR = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuilder responseStrBuilder = new StringBuilder();
                    while ((line = bR.readLine()) != null) {
                        responseStrBuilder.append(line);
                    }
                    JSONArray result = new JSONArray(responseStrBuilder.toString());
                    contactResponse = jsonArrayToContactDataList(result);
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        thread.join();
        return contactResponse;
    }


     MessageDataList[] getLatestMessageThreads(String clientID, String jwt) throws InterruptedException {
         Thread thread = new Thread(() -> {
            try {
                URL url = new URL(baseUrl + "contacts/get/thread/{recipient_id}?recipientID=" + clientID);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Authorization", jwt);
                InputStream is = con.getInputStream();
                BufferedReader bR = new BufferedReader( new InputStreamReader(is));
                String line;
                StringBuilder responseStrBuilder = new StringBuilder();
                while((line =  bR.readLine()) != null){
                    responseStrBuilder.append(line);
                }
                is.close();
                JSONArray result = new JSONArray(responseStrBuilder.toString());
                messageResponse = jsonArraytoMessageThreadDataList(result);
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        thread.join();
        return messageResponse;
    }
    
    private MessageDataList[] jsonArraytoMessageThreadDataList(JSONArray ja) throws JSONException {
        MessageDataList[] dataList = new MessageDataList[ja.length()];
        for (int i =0; i < ja.length(); i++) {
            String date = ja.getJSONObject(i).getString("timestamp");
            String sender = ja.getJSONObject(i).getString("senderName");
            String text = ja.getJSONObject(i).getString("text");
            String senderID = ja.getJSONObject(i).getString("senderID");
            date = date.substring(0, (date.length() - 10));
            dataList[i] = new MessageDataList(sender, text, android.R.drawable.ic_dialog_info, date, senderID);
        }
        return dataList;
    }


    private ContactDataList[] jsonArrayToContactDataList(JSONArray ja) throws JSONException {
        ContactDataList[] dataList = new ContactDataList[ja.length()];
        for (int i = 0; i < ja.length(); i++) {
            dataList[i] = new ContactDataList(ja.getString(i), android.R.drawable.ic_dialog_info);
        }
        return dataList;
    }

    protected void setVersion() throws InterruptedException {
        Thread thread = new Thread(() -> {
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
                version = ("Error with API connection");
            }
        });
        thread.start();
        thread.join();
    }

    HttpURLConnection getPOSTHTTPConnection(String func, String jwt) throws IOException {
        URL url = new URL(baseUrl + func);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Authorization", jwt);
        return con;
    }

    public void sendMessage(String jwt, String senderID, String recipientID, String text) {
        String payload = ("{\"senderID\":\"" + senderID + "\",\"recipientID\":\"" + recipientID + "\",\"text\":\"" + text + "\"}");
        Thread thread = new Thread(() -> {
            try {
                URL url = new URL(baseUrl + "clients/messages/send");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Authorization", jwt);
                byte[] out = payload.getBytes(StandardCharsets.UTF_8);
                OutputStream stream = con.getOutputStream();
                stream.write(out);
                int status = con.getResponseCode();
                InputStream is = con.getInputStream();
                BufferedReader bR = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder responseStrBuilder = new StringBuilder();
                while ((line = bR.readLine()) != null) {
                    responseStrBuilder.append(line);
                }
                is.close();
                con.disconnect();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
    }

    MessageDataList[] getThreadMessages(String clientID, String filterID, String jwt) throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                String payload = "";
                if (filterID.equals("NONE")) {
                    payload = "{\"clientID\":\"" + clientID + "\"}";
                }
                else {
                    payload = "{\"clientID\":\"" + clientID + "\",\"filterID\":\"" + filterID + "\"}";
                }
                HttpURLConnection con = getPOSTHTTPConnection("clients/messages/", jwt);
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
                JSONArray result = new JSONArray(responseStrBuilder.toString());
                messageResponse = jsonArraytoMessageThreadDataList(result);
                con.disconnect();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException ignored) {
            }
        });
        thread.start();
        thread.join();
        return messageResponse;
    }

}
