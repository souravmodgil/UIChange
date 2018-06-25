
package com.mobileoid2.celltone.Service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.mobileoid2.celltone.utility.Utils;

/**
 * Created by mobileoid2 on 13/10/17.
 */

public class ServiceCallScreenChanged  extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            AccessibilityNodeInfo info = event.getSource();
            if (info != null && info.getText() != null) {
                String duration = info.getText().toString();
              //  Log.e("myaccess", "in window changed :: mycompanyapplicationserchtext " + duration + " mycompanyapplicationserchtext");


                String zeroSeconds = String.format("%02d:%02d", new Object[]{Integer.valueOf(0), Integer.valueOf(0)});
                String firstSecond = String.format("%02d:%02d", new Object[]{Integer.valueOf(0), Integer.valueOf(1)});
                String secondSecond = String.format("%02d:%02d", new Object[]{Integer.valueOf(0), Integer.valueOf(2)});
                String thirdSecond = String.format("%02d:%02d", new Object[]{Integer.valueOf(0), Integer.valueOf(3)});
                String fourthSecond = String.format("%02d:%02d", new Object[]{Integer.valueOf(0), Integer.valueOf(4)});
                String fifthSecond = String.format("%02d:%02d", new Object[]{Integer.valueOf(0), Integer.valueOf(5)});
                //Log.d("myaccess","after calculation - "+ zeroSeconds + " --- "+ firstSecond + " --- " + duration);

                if (zeroSeconds.equals(duration)
                        || firstSecond.equals(duration)
                        || secondSecond.equals(duration)
                        || thirdSecond.equals(duration)
                        || fourthSecond.equals(duration)
                        || fifthSecond.equals(duration)) {

                    if (Utils.isMyServiceRunning(getApplicationContext(), OverlayShowingService.class)) {
                        stopService(new Intent(this, OverlayShowingService.class));
                        Log.e("myaccess", "Main Service called");
                    }
                }
                info.recycle();
            }
        }
    }

   /* private boolean isMyServiceRunning(Context ctx, Class<?> serviceClass) {
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
    }*/


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        //Toast.makeText(this,"Service connected",Toast.LENGTH_SHORT).show();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.notificationTimeout = 0;
        info.packageNames = null;
        setServiceInfo(info);
    }

    @Override
    public void onInterrupt() {

    }
}