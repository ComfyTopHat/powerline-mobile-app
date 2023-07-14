package com.comfy.powerline;

import static java.time.temporal.ChronoUnit.DAYS;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.comfy.powerline.data.Contact;
import com.comfy.powerline.data.Conversation;
import com.comfy.powerline.data.Message;

import org.json.JSONArray;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ApiHandler extends AppCompatActivity  {
    ArrayList<Contact> contactResponse;
    ArrayList<Conversation> conversationDataList;
    volatile String strResponse;
    String baseUrl = LoginActivity.getBaseURL();
    List<Message> messageResponse;
    String version;

    /**
     *
     * @param jwt String JWT that contains a valid JWT in the format "Bearer _TOKEN_"
     * @return Returns an ArrayList of Contact objects to be displayed via the compose form
     * @throws InterruptedException
     * NOTE: This will be replaced with a Kotlin coroutine at some stage
     */
    ArrayList<Contact> getContacts(String jwt) throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                try {
                    URL url = new URL(baseUrl + "contacts/get/{recipient_id}");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Authorization", jwt);
                    JSONArray result = readResponseObject(con, JSONArray.class);
                    contactResponse = convertJSONArrayToComposeContactList(result);
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

    /**
     * Method to convert a JSONArray to an ArrayList of Contact objects
     * @param resultList Contains the JSONArray of Contacts received from the server
     * @return Returns an ArrayList of Contact objects
     * @throws JSONException If the JSON is malformed, likely if the API schema has changed
     */
    ArrayList<Contact> convertJSONArrayToComposeContactList(JSONArray resultList) throws JSONException {
        ArrayList<Contact> contactList = new ArrayList<>();
        for (int i=0; i < resultList.length(); i++) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Contact newContact = new Contact(
                        resultList.getJSONObject(i).getString("name"),
                        resultList.getJSONObject(i).getInt("contactID"));
                contactList.add(newContact);
            }
        }
        return contactList;
    }

    /**
     * Method to retreieve message threads to be display on the messages menu as unique conversations
     * @param jwt String JWT that contains a valid JWT in the format "Bearer _TOKEN_"
     * @return An ArrayList of Conversation objects to be displayed on the Compose form
     * @throws InterruptedException
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    ArrayList<Conversation> getLatestMessageThreads(String jwt) throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                URL url = new URL(baseUrl + "contacts/get/thread/{recipient_id}");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Authorization", jwt);
                JSONArray result = readResponseObject(con, JSONArray.class);
                conversationDataList = convertJSONArrayToComposeConversationList(result);
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        thread.join();
        return conversationDataList;
    }


    /**
     * A method to convert a JSON array to an ArrayList of Conversation Compose objects
     * @param resultList A JSON Array of Conversations retrieved from the server
     * @return Returns an ArrayList of Conversation objects converted from the JSON Array
     * @throws JSONException
     */
    ArrayList<Conversation> convertJSONArrayToComposeConversationList(JSONArray resultList) throws JSONException {
        ArrayList<Conversation> conversationList = new ArrayList<>();
        for (int i=0; i < resultList.length(); i++) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDateTime dateTime = LocalDateTime.parse(resultList.getJSONObject(i).getString("timestamp"));
                Conversation newConversation = new Conversation(
                        resultList.getJSONObject(i).getString("senderName"),
                        resultList.getJSONObject(i).getInt("senderID"),
                        resultList.getJSONObject(i).getInt("recipientID"),
                        resultList.getJSONObject(i).getString("text"),
                        formatTimeStamp(dateTime),
                        resultList.getJSONObject(i).getString("recipientName"));
                conversationList.add(newConversation);
            }
        }
        return conversationList;
    }

    /**
     *
     * @param client String value containing the username of the client
     * @param jwt String value containing the JWT
     * @return Returns an Int value of the client, if it does not exist return 0
     * @throws IOException
     * @throws InterruptedException
     * @throws JSONException
     */
    public int verifyClientExists(String client, String jwt) throws IOException, InterruptedException, JSONException {
        HttpURLConnection con = getPOSTHTTPConnection("clients/get/clientID/?username=" + (client), jwt);
        String response = sendData(con, "");
        JSONObject responseObj = new JSONObject(response);
        if (responseObj.getString("status").contains("success")) {
            return (responseObj.getInt("clientID"));
        }
        else return 0;
    }

    /**
     * Basic method to convert a DateTime object to the required format
     * @param messageDateTime A dateTime object to be formatted to the required standard
     * @return Returns a String datetime in the updated format
     */
    private static String formatTimeStamp(LocalDateTime messageDateTime) {
        String displayedTimestamp = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DayOfWeek DoW = LocalDate.now().getDayOfWeek();
            DayOfWeek messageDoW = messageDateTime.getDayOfWeek();
            long daysSinceMessage = DAYS.between(messageDateTime.toLocalDate(), LocalDate.now());
            // If the message was sent same day, then display time
            if (DoW.equals(messageDoW))
            {
                String amPM = "am";
                if (messageDateTime.getHour() >= 12){
                    amPM = "pm";
                }
                displayedTimestamp = messageDateTime.getHour() +  ":" + messageDateTime.getMinute() + amPM;
            } else if (daysSinceMessage < 7) {
                displayedTimestamp = messageDoW.toString().substring(0, 1).toUpperCase() + messageDoW.toString().substring(1, 3).toLowerCase();
            }
            else {
                String messageMonth = messageDateTime.getMonth().toString();
                displayedTimestamp = messageDateTime.getDayOfMonth() + " " +
                        messageMonth.substring(0, 1).toUpperCase() +
                        messageMonth.substring(1, 3).toLowerCase();
            }
        }
        return displayedTimestamp;
    }


    /**
     * Universal method to convert the response from the server to the required format.
     * I.e. JSONArray/JSONObject/String
     * @param con A HTTPConnection object with all the required parameters
     * @param target The target class for the resposne format
     * @return Returns a generic object cast to the target class
     * @param <T> Contains the class target (must be generic as there are 3 possible options)
     * @throws IOException
     * @throws JSONException
     */
    private <T> T readResponseObject(HttpURLConnection con, Class<T> target) throws IOException, JSONException {
        InputStream is = con.getInputStream();
        BufferedReader bR = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder responseStrBuilder = new StringBuilder();
        while ((line = bR.readLine()) != null) {
            responseStrBuilder.append(line);
        }
        is.close();
        if (JSONArray.class.equals(target)) {
            return target.cast(new JSONArray(responseStrBuilder.toString()));
        } else if (JSONObject.class.equals(target)) {
            return target.cast(new JSONObject(responseStrBuilder.toString()));
        } else {
            return target.cast(responseStrBuilder.toString());
        }
    }

    /**
     *  Method to check the current API version and display it on the UI
     *  This is currently unused since converting to Kotlin but will be reworked
     * @throws InterruptedException
     */
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

    /**
     * Method to retrieve a HTTP connection based on the function
     * @param func The required API function to be called by the connection
     * @param jwt String JWT that contains a valid JWT in the format "Bearer _TOKEN_"
     * @return Returns a HttpURLConnection with the required headers
     * @throws IOException
     */
    public HttpURLConnection getPOSTHTTPConnection(String func, String jwt) throws IOException {
        URL url = new URL(baseUrl + func);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        if (!jwt.equals("-")) {
            con.setRequestProperty("Authorization", jwt);
        }
        return con;
    }

    /**
     *
     * @param con
     * @param payload
     * @return
     * @throws InterruptedException
     */
    public String sendData(HttpURLConnection con, String payload) throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                byte[] out = payload.getBytes(StandardCharsets.UTF_8);
                OutputStream stream = con.getOutputStream();
                stream.write(out);
                stream.close();
                strResponse = readResponseObject(con, String.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        thread.join();
        return strResponse;
    }


    public void sendMessage(String jwt, String recipientID, String text) throws IOException, InterruptedException {
        String payload = ("{\"recipientID\":\"" + recipientID + "\",\"text\":\"" + text + "\"}");
        HttpURLConnection con = getPOSTHTTPConnection("clients/messages/send", jwt);
        sendData(con, payload);
    }

    List<Message> getThreadMessages(String filterID, String jwt) throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                String payload;
                if (filterID.equals("NONE")) {
                    payload = "{}";
                }
                else {
                    payload = "{\"filterID\":\"" + filterID + "\"}";
                }
                HttpURLConnection con = getPOSTHTTPConnection("clients/messages/", jwt);
                String response = sendData(con, payload);
                JSONArray result = new JSONArray(response);
                messageResponse = convertJSONArrayToComposeMessageList(result);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException | InterruptedException ignored) {
            }
        });
        thread.start();
        thread.join();
        return messageResponse;
    }

    ArrayList<Message> convertJSONArrayToComposeMessageList(JSONArray resultList) throws JSONException {
        ArrayList<Message> messageList = new ArrayList<>();
        for (int i=0; i < resultList.length(); i++) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
               // LocalDateTime dateTime = LocalDateTime.parse(resultList.getJSONObject(i).getString("timestamp"));
                Message newMessage = new Message(
                        resultList.getJSONObject(i).getString("senderName"),
                        resultList.getJSONObject(i).getInt("senderID"),
                        resultList.getJSONObject(i).getInt("recipientID"),
                        resultList.getJSONObject(i).getString("text"),
                        resultList.getJSONObject(i).getString("timestamp"),
                        null,
                        resultList.getJSONObject(i).getBoolean("selfAuthored")
                      );
                messageList.add(newMessage);
            }
        }
        return messageList;
    }
}