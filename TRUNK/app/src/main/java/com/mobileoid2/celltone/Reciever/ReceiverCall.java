package com.mobileoid2.celltone.Reciever;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.mobileoid2.celltone.Service.ServicePlayMusicOnCall;
import com.mobileoid2.celltone.Util.Constant;

import java.util.Date;

public class ReceiverCall extends PhonecallReceiver {

    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start) {
        Toast.makeText(ctx, "onIncomingCallReceived", Toast.LENGTH_SHORT).show();
        Constant.PHONENUMBER = number;
        Constant.isIncoming = true;
        if (!isMyServiceRunning(ctx, ServicePlayMusicOnCall.class))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                ctx.startForegroundService(new Intent(ctx, ServicePlayMusicOnCall.class));
            else
                ctx.startService(new Intent(ctx, ServicePlayMusicOnCall.class));
    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start) {
        Toast.makeText(ctx, "onIncomingCallAnswered", Toast.LENGTH_SHORT).show();
        if (isMyServiceRunning(ctx, ServicePlayMusicOnCall.class))
            ctx.stopService(new Intent(ctx, ServicePlayMusicOnCall.class));
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Toast.makeText(ctx, "onIncomingCallEnded", Toast.LENGTH_SHORT).show();
        if (isMyServiceRunning(ctx, ServicePlayMusicOnCall.class))
            ctx.stopService(new Intent(ctx, ServicePlayMusicOnCall.class));
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Toast.makeText(ctx, "onOutgoingCallStarted", Toast.LENGTH_SHORT).show();
        Constant.PHONENUMBER = number;
        Constant.isIncoming = false;
        if (!isMyServiceRunning(ctx, ServicePlayMusicOnCall.class))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                ctx.startForegroundService(new Intent(ctx, ServicePlayMusicOnCall.class));
            else
                ctx.startService(new Intent(ctx, ServicePlayMusicOnCall.class));

    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Toast.makeText(ctx, "onOutgoingCallEnded", Toast.LENGTH_SHORT).show();
        if (isMyServiceRunning(ctx, ServicePlayMusicOnCall.class))
            ctx.stopService(new Intent(ctx, ServicePlayMusicOnCall.class));
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Toast.makeText(ctx, "onMissedCall", Toast.LENGTH_SHORT).show();
        if (isMyServiceRunning(ctx, ServicePlayMusicOnCall.class))
            ctx.stopService(new Intent(ctx, ServicePlayMusicOnCall.class));
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

}