package com.comfy.powerline;

import static com.comfy.powerline.MainActivity.baseUrl;

import android.text.Editable;

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
    volatile String strResponse;
    JSONObject response;
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
                messageResponse = jsonArraytoMessageThreadDataList(result, clientID, true);
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        thread.join();
        return messageResponse;
    }
    
    private MessageDataList[] jsonArraytoMessageThreadDataList(JSONArray ja, String clientID, Boolean messagePreview) throws JSONException {
        MessageDataList[] dataList = new MessageDataList[ja.length()];
        boolean selfAuthored;
        for (int i =0; i < ja.length(); i++) {
            String date = ja.getJSONObject(i).getString("timestamp");
            String sender = ja.getJSONObject(i).getString("senderName");
            String text = ja.getJSONObject(i).getString("text");
            String senderID = ja.getJSONObject(i).getString("senderID");
            date = date.substring(0, (date.length() - 10));
            if (senderID.equals(clientID)) {
                selfAuthored = true;
            }
            else {
                selfAuthored = false;
            }
            dataList[i] = new MessageDataList(sender, text, android.R.drawable.ic_dialog_info, date, senderID, selfAuthored, messagePreview);
        }
        return dataList;
    }


    private ContactDataList[] jsonArrayToContactDataList(JSONArray ja) throws JSONException {
        ContactDataList[] dataList = new ContactDataList[ja.length()];
        for (int i = 0; i < ja.length(); i++) {
            JSONObject jo = ja.getJSONObject(i);
            String name = jo.getString("name");
            String contactID = jo.getString("contactID");
            dataList[i] = new ContactDataList(name, android.R.drawable.ic_dialog_info, contactID);
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

    private String sendData(HttpURLConnection con, String payload) throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
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
                con.disconnect();
                strResponse = responseStrBuilder.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        thread.join();
        return strResponse;
        }


    public void sendMessage(String jwt, String senderID, String recipientID, String text) throws IOException, InterruptedException {
        String payload = ("{\"senderID\":\"" + senderID + "\",\"recipientID\":\"" + recipientID + "\",\"text\":\"" + text + "\"}");
        HttpURLConnection con = getPOSTHTTPConnection("clients/messages/send", jwt);
        sendData(con, payload);
    }

    MessageDataList[] getThreadMessages(String clientID, String filterID, String jwt) throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                String payload;
                if (filterID.equals("NONE")) {
                    payload = "{\"clientID\":\"" + clientID + "\"}";
                }
                else {
                    payload = "{\"clientID\":\"" + clientID + "\",\"filterID\":\"" + filterID + "\"}";
                }
                HttpURLConnection con = getPOSTHTTPConnection("clients/messages/", jwt);
                String response = sendData(con, payload);
                JSONArray result = new JSONArray(response);
                messageResponse = jsonArraytoMessageThreadDataList(result, clientID, false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException | InterruptedException ignored) {
            }
        });
        thread.start();
        thread.join();
        return messageResponse;
    }
}
