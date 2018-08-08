package com.mobileoid2.celltone.Service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.AppUtils;
import com.mobileoid2.celltone.utility.AudioPlayerTest;
import com.mobileoid2.celltone.utility.Utils;

import java.util.Iterator;

public class NotificationListener extends NotificationListenerService {
    private String TAG = this.getClass().getSimpleName();

    int iSamsungCallUIAppeared = 0;

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        Bundle extras = sbn.getNotification().extras;
        String ss = extras.getString(Notification.EXTRA_TEXT);



        if (ss.indexOf(getResources().getString(R.string.outgoing_call))!=-1) {

            if (Utils.isMyServiceRunning(getApplicationContext(), OverlayShowingService.class)) {
                stopService(new Intent(this, OverlayShowingService.class));
                Log.e("myaccess", "Main Service called");
            }
        }



    }

    private boolean isMyServiceRunning(Context ctx, Class<?> serviceClass) {
        try {
            ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    Log.i("isMyServiceRunning?", true + "");
                    return true;
                }
            }
            Log.i("isMyServiceRunning?", false + "");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG, "********** onNotificationRemoved");
        Log.i(TAG, "ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
    }



//    private void showToast() {
//
//        if (isMyServiceRunning(getApplicationContext(), ServicePlayMusicOnCall.class)) {
//            stopService(new Intent(this, ServicePlayMusicOnCall.class));
//            Log.e(TAG, "Main Service called");
//
//            Handler handler = new Handler(Looper.getMainLooper());
//            handler.post(new Runnable() {
//
//                @Override
//                public void run() {
//                    Toast.makeText(getApplicationContext(),
//                            "Call Answered",
//                            Toast.LENGTH_SHORT).show();
//                }
//            });
//
//        }
//
//        AudioPlayerTest.stop();
//    }

    private boolean checkCall(String find, String s2) {
        try {
            if (s2.toLowerCase().contains("android.text") || s2.toLowerCase().contains("chrono")

                    || s2.toString().toLowerCase().contains("On-going call".toLowerCase())
                    || s2.toString().toLowerCase().contains("incallui")
                    ) {
                return true;
            }

            if (s2.toString().toLowerCase().contains(find.toLowerCase())) {
                //System.out.println("NotificationService.onNotificationPosted--------   call answered");
                return true;
            }
            if (s2.toLowerCase().contains("chronometer") && s2.toString().toLowerCase().contains("true")) {
                // System.out.println("NotificationService.onNotificationPosted--------   call answered");
                return true;
            }

            String text = s2;
            text = text.trim();
            text = text.replaceAll("-", "");
            text = text.replaceAll(" ", "");
            text = text.toLowerCase();

            if (text.equals("ongoingcall")) {
                return true;
               /* if (isMyServiceRunning(getApplicationContext(), ServicePlayMusicOnCall.class)) {

                    stopService(new Intent(this, ServicePlayMusicOnCall.class));
                    Log.e(TAG, "Main Service called");

                }*/
            }


            return false;

        } catch (Exception ex2) {
            ex2.printStackTrace();
            Log.e(TAG, AppUtils.instance.getExceptionString(ex2));
        }

        return false;
    }

}
