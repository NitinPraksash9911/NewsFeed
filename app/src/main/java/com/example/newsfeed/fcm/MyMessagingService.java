package com.example.newsfeed.fcm;

import android.app.Notification;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.newsfeed.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.example.newsfeed.utils.MyApplication.CHANNEL_ID;

public class MyMessagingService extends FirebaseMessagingService {

    public static final String TAG = "MyMessagingService";
    String title = "";
    String body = "";
    String dataPayload = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        /*1. With  data payload  when app in foreground
          :-  data payload comes in notification

        2. Without  data payload when app in foreground
          ;- notification comes without custom data payload

         3.With custom data payload when app in background
          :-  payload data not come in notification

          NOTE: if we want to handle  data payload  we can only handle it when app will be in foreground

          */


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            /*
             * here handle custom data payload
             * */
            dataPayload = remoteMessage.getData().get("customData");

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        sendNotification(title, body + " : " + dataPayload);


    }

    public void sendNotification(String title, String msg) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_down_arrow)
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

    }


}
