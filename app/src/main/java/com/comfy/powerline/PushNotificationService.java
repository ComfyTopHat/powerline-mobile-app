package com.comfy.powerline;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

public class PushNotificationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        final String CHANNEL_ID = "HEADS_UP_NOTIFICATION";
        String notificationText = decodeMessage(remoteMessage, "data");
        String notificationAuthor = decodeMessage(remoteMessage, "author");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
            new NotificationChannel(CHANNEL_ID, "Heads Up Notification", NotificationManager.IMPORTANCE_HIGH);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle(notificationAuthor)
                    .setContentText(notificationText)
                    .setSmallIcon(R.drawable.baseline_person_24)
                    .setAutoCancel(true);

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            NotificationManagerCompat.from(this).notify(1, notification.build());
        }
    }

    public String decodeMessage(RemoteMessage remoteMessage, String dataItem) {
        String payloadData;
        try
        {
            Map<String, String> params = remoteMessage.getData();
            JSONObject object = new JSONObject(params);
            payloadData = object.getString(dataItem);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return payloadData;
    }
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }
}