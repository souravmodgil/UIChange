package com.mobileoid2.celltone.FirebaseMessaging;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


/**
 * Created by root on 19/12/17.
 */

public class MyMessagingService extends FirebaseMessagingService {

    final String TAG = this.getClass().getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

    }
}
