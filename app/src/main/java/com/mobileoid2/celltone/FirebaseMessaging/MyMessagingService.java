package com.mobileoid2.celltone.FirebaseMessaging;

import android.app.Notification;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mobileoid2.celltone.R;


import java.util.Map;


/**
 * Created by root on 19/12/17.
 */

public class MyMessagingService extends FirebaseMessagingService {

    final String TAG = this.getClass().getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        int size = remoteMessage.getData().size();
        if (remoteMessage.getData().size() > 0) {
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle(remoteMessage.getData().get("title"))
                    .setContentText(remoteMessage.getData().get("body"))
                    .build();
            NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
            manager.notify(123, notification);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "com.mobileoid2.celltone")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(remoteMessage.getData().get("title"))
                    .setContentText(remoteMessage.getData().get("body"))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }
    }
}
