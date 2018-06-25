package com.mobileoid2.celltone.Reciever;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.mobileoid2.celltone.Service.OverlayShowingService;
import com.mobileoid2.celltone.Util.Constant;
import com.mobileoid2.celltone.utility.Utils;

import java.util.Date;

public class ReceiverCall extends PhonecallReceiver {

    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start) {
        Toast.makeText(ctx, "onIncomingCallReceived", Toast.LENGTH_SHORT).show();
        Constant.PHONENUMBER = number;
        Constant.isIncoming = true;
        startServiceMedia(ctx);
    }


    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start) {
        Toast.makeText(ctx, "onIncomingCallAnswered", Toast.LENGTH_SHORT).show();
        stopServiceForMedia(ctx);
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Toast.makeText(ctx, "onIncomingCallEnded", Toast.LENGTH_SHORT).show();
        stopServiceForMedia(ctx);
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Toast.makeText(ctx, "onOutgoingCallStarted", Toast.LENGTH_SHORT).show();
        Constant.PHONENUMBER = number;
        Constant.isIncoming = false;
        startServiceMedia(ctx);

    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Toast.makeText(ctx, "onOutgoingCallEnded", Toast.LENGTH_SHORT).show();
        stopServiceForMedia(ctx);
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Toast.makeText(ctx, "onMissedCall", Toast.LENGTH_SHORT).show();
        stopServiceForMedia(ctx);
    }

    private void stopServiceForMedia(Context ctx) {
        if (Utils.isMyServiceRunning(ctx, OverlayShowingService.class))
            ctx.stopService(new Intent(ctx, OverlayShowingService.class));
    }


    private void startServiceMedia(Context ctx) {
        if (!Utils.isMyServiceRunning(ctx, OverlayShowingService.class)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                ctx.startForegroundService(new Intent(ctx, OverlayShowingService.class));
            else ctx.startService(new Intent(ctx, OverlayShowingService.class));
        }
    }




}