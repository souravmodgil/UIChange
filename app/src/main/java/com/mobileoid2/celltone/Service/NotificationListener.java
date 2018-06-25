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

import com.mobileoid2.celltone.Util.AppUtils;
import com.mobileoid2.celltone.utility.AudioPlayerTest;

import java.util.Iterator;

public class NotificationListener extends NotificationListenerService {
    private String TAG = this.getClass().getSimpleName();

    int iSamsungCallUIAppeared = 0;

    /*@Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        String pack = sbn.getPackageName();
        String ticker = "";
        String title = "";
        String text = "";
        String category = "";


        Notification notification = sbn.getNotification();
        if (notification != null) {
            ticker = "" + notification.tickerText;
            Bundle extras = sbn.getNotification().extras;
            title = extras.getString("android.title");
            text = "" + extras.getCharSequence("android.text");
            category = "" + notification.category;

            System.out.println("NotificationListener.onNotificationPosted" +
                    sbn.getPackageName() + "\t" +
                    sbn.getTag() + "\t" +
                    ticker + "\t" +
                    text + "\t" +
                    category + "\t"
            );
        }
        Log.e(TAG, "text :: mycompanyapplicationserchtext " + text + " mycompanyapplicationserchtext");


        if (ticker != null) {
            StringBuffer stringBuffer = new StringBuffer(ticker);
            StringBuffer newStringBuffer = new StringBuffer(stringBuffer.toString().replaceAll("[\r\n]+", ""));
            ticker = newStringBuffer.toString();
        }

        if (title != null) {
            StringBuffer stringBuffer = new StringBuffer(title);
            StringBuffer newStringBuffer = new StringBuffer(stringBuffer.toString().replaceAll("[\r\n]+", ""));
            title = newStringBuffer.toString();
        }

        if (text != null) {
            StringBuffer stringBuffer = new StringBuffer(text);
            StringBuffer newStringBuffer = new StringBuffer(stringBuffer.toString().replaceAll("[\r\n]+", ""));
            text = newStringBuffer.toString();
        }

        Log.e(TAG, "ticker :: mycompanyapplicationserchtext " + ticker + " mycompanyapplicationserchtext");
        Log.e(TAG, "title :: mycompanyapplicationserchtext " + title + " mycompanyapplicationserchtext");
        Log.e(TAG, "text :: mycompanyapplicationserchtext " + text + " mycompanyapplicationserchtext");
        Log.e(TAG, "category :: mycompanyapplicationserchtext " + category + " mycompanyapplicationserchtext");


        text = text.trim();
        text = text.replaceAll("-", "");
        text = text.replaceAll(" ", "");
        text = text.toLowerCase();

        if (text.equals("ongoingcall")) {
            if (isMyServiceRunning(getApplicationContext(), ServicePlayMusicOnCall.class)) {
                stopService(new Intent(this, ServicePlayMusicOnCall.class));
                Log.e(TAG, "Main Service called");
            }
        }


    }*/

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


    @Override

    public void onNotificationPosted(StatusBarNotification sbn) {


//        if (!isMyServiceRunning(getApplicationContext(), ServicePlayMusicOnCall.class)) {
//            return;
//        }

        String pack = sbn.getPackageName();
//        String ticker = sbn.getNotification().tickerText.toString();
        Bundle extras = sbn.getNotification().extras;

        String title = extras.getString("android.title");

        Log.e(TAG, "NotificationService.onNotificationPosted-----" + pack + "\tcom.android.incallui+\t" + "\t");
        //if ("Ongoing call".equals(extras.getString(Notification.EXTRA_TEXT)))
        if (pack.contains("com.android.incallui")) {
            ++iSamsungCallUIAppeared;
        }

        if (iSamsungCallUIAppeared >= 4) {

         //   showToast();
            iSamsungCallUIAppeared = 0;
        }
      /*
        String text = extras.getCharSequence("android.text").toString();

       */
        final Iterator<String> iterator = (Iterator<String>) extras.keySet().iterator();

        while (iterator.hasNext()) {

//            System.out.println("NotificationService.onNotificationPosted-------"+s2+"\t\t"+iterator.next());

            final String s2 = iterator.next();//""+extras.getCharSequence(iterator.next());


            boolean isCallAnswered = checkCall("ongoing call", s2);

            if (isCallAnswered) {
                ///////call is answered
                Log.e(TAG, "NotificationService.onNotificationPosted-------" + s2);
                //showToast();
            }


            if (!isCallAnswered) {
                if (checkCall("com.samsung.android.incallui", s2)) {
                    ///////call is answered
                    Log.e(TAG, "NotificationService.onNotificationPosted-------" + s2);
                   // showToast();
                }

            }


        }
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
