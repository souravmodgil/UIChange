package com.mobileoid2.celltone.FirebaseMessaging;

import android.app.Notification;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.CelltoneApplication;
import com.mobileoid2.celltone.Util.Constant;
import com.mobileoid2.celltone.database.AppDatabase;
import com.mobileoid2.celltone.network.APIClient;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.ApiInterface;
import com.mobileoid2.celltone.network.NetworkCallBack;
import com.mobileoid2.celltone.network.SendRequest;
import com.mobileoid2.celltone.network.jsonparsing.JsonResponse;
import com.mobileoid2.celltone.pojo.getmedia.Body;
import com.mobileoid2.celltone.pojo.getmedia.PojoGETMediaResponse;
import com.mobileoid2.celltone.utility.Config_URL;
import com.mobileoid2.celltone.utility.SharedPrefrenceHandler;
import com.mobileoid2.celltone.utility.Utils;
import com.mobileoid2.celltone.view.activity.HomeActivity;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by root on 19/12/17.
 */

public class MyMessagingService extends FirebaseMessagingService implements NetworkCallBack {

    final String TAG = this.getClass().getSimpleName();
     private ApiInterface apiInterface ;
//    String{}} notifiactionType
//
//    enum notifiactionType {
//        media_set,
//        query_answered,
//        upload_media_accepted,
//        upload_media_rejected,
//        new_category
//    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String from =  remoteMessage.getFrom();
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        int size = remoteMessage.getData().size();
        if (remoteMessage.getData().size() > 0) {

          //  Boolean isSilent =Boolean.parseBoolean();

            if(remoteMessage.getData().get("isSilent").equals("1")) {
                switch (remoteMessage.getData().get("type")) {
                    case Constant.MEDIA_SET:
                        refreshMediaSetByOther();
                        break;
                    case Constant.MEDIA_UNSET:
                        String[] splitArray = remoteMessage.getData().get("info").split(",");
                        String mobileNumber  = splitArray[0];
                        String  mediaId = splitArray[1];
                        Utils utils = new Utils();
                        if (apiInterface == null)
                            apiInterface = APIClient.getClient().create(ApiInterface.class);
                        AppDatabase appDatabase =AppDatabase.getAppDatabase(this);
                        utils.unsetIncomingothers(this,mediaId,appDatabase,mobileNumber,apiInterface,this);
                      //  refreshMediaSetByOther();
                        break;
                }




            }
            else
            {
                Notification notification = new NotificationCompat.Builder(this)
                        .setContentTitle(remoteMessage.getData().get("title"))
                        .setContentText(remoteMessage.getData().get("info"))
                        .setSmallIcon(R.drawable.ic_check)
                        .build();
                NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
                manager.notify(123, notification);

            }



        }

    }

    private void refreshMediaSetByOther() {

        if (apiInterface == null)
            apiInterface = APIClient.getClient().create(ApiInterface.class);
        Utils.getMediForMe(apiInterface, this);
    }

    @Override
    public void getResponse(JsonResponse response, int type) {
        if (response.getObject() != null) {
            Utils utils = new Utils();
            AppDatabase appDatabase =AppDatabase.getAppDatabase(this);
            utils.parseRequest(response.getObject(),this,appDatabase);

        }


    }
//        System.out.println("TOKEN :" + SharedPrefrenceHandler.getInstance().getUSER_TOKEN());

// Adding request to request queue


}


