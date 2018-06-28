package com.mobileoid2.celltone.FirebaseMessaging;

import android.app.Notification;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
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
import com.mobileoid2.celltone.network.ApiConstant;
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

public class MyMessagingService extends FirebaseMessagingService {

    final String TAG = this.getClass().getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        int size = remoteMessage.getData().size();
        if (remoteMessage.getData().size() > 0) {
            if(remoteMessage.getData().get("type").equals("MEDIA_SET"))
            {
                refreshMediaSetByOther();
            }
//            Notification notification = new NotificationCompat.Builder(this)
//                    .setContentTitle(remoteMessage.getData().get("title"))
//                    .setContentText(remoteMessage.getData().get("body"))
//                    .build();
//            NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
//            manager.notify(123, notification);
//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "com.mobileoid2.celltone")
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setContentTitle(remoteMessage.getData().get("title"))
//                    .setContentText(remoteMessage.getData().get("body"))
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }
    }
    private void refreshMediaSetByOther() {
        System.out.println("TOKEN :" + SharedPrefrenceHandler.getInstance().getUSER_TOKEN());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, Config_URL.URL_GET_MEDIA, null, response -> {
            try {
                //    {"status":1000,"body":[{"userId":{"_id":"5b1fc62e326f61489e495c66","mobile":"+919560254331"},"outgoing":{"originalFileUrl":"video\/media.1525439585351.mp4","sampleFileUrl":"video\/sample1525439585432.mp4","clipArtUrl":"image\/clipart.1527858406033.png","_id":"5aec55b7bfcf7255e1c4c227","title":"star","contentType":"video"},"createdAt":"2018-06-12T13:11:20.300Z","updatedAt":"2018-06-12T13:12:26.272Z"}],"message":"success"}
                PojoGETMediaResponse pojoContactsUploadResonse = Arrays.asList(new Gson().fromJson(response.toString(), PojoGETMediaResponse.class)).get(0);

                if (pojoContactsUploadResonse.getStatus() == 1000) {
                    SharedPrefrenceHandler.getInstance().setGET_MEDIA_RESPONSE(response.toString());
                    new DownloadTask(getApplicationContext()).execute(pojoContactsUploadResonse.getBody());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }, error -> {
            VolleyLog.d(TAG, "Error: " + error.getMessage());
            // hide the progress dialog
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("token", SharedPrefrenceHandler.getInstance().getUSER_TOKEN());
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                //headers.put("token", SharedPrefrenceHandler.getInstance().getUSER_TOKEN());
                return headers;
            }
        };

// Adding request to request queue
        CelltoneApplication.getInstance().addToRequestQueue(jsonObjReq, Config_URL.tag_json_obj);
    }
    private class DownloadTask extends AsyncTask<List<Body>, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(List<com.mobileoid2.celltone.pojo.getmedia.Body>... sUrl) {


            File directoryToZip = new File(Utils.getFilePath(context));
            List<com.mobileoid2.celltone.pojo.getmedia.Body> bodyList = sUrl[0];


            for (int i = 0; i < bodyList.size(); i++) {

                File file = null;

                if (bodyList.get(i).getOutgoing() != null) {
                    file = new File(directoryToZip.getPath() + "/" + bodyList.get(i).getOutgoing().getSampleFileUrl());
                    System.out.println("DownloadTask.doInBackground-----" + file.exists() + "\t" + file.getPath());
                    if (!file.exists())
                        downloadFiles(directoryToZip, bodyList, i);
                }


            }

            return null;
        }


        private boolean downloadFiles(File directoryToZip, List<com.mobileoid2.celltone.pojo.getmedia.Body> bodyList, int i) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(ApiConstant.MEDIA_URL + bodyList.get(i).getOutgoing().getSampleFileUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return true;
                }
                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();
                // download the file
                input = connection.getInputStream();

                File file = new File(directoryToZip.getPath() + File.separator + "" + bodyList.get(i).getOutgoing().getSampleFileUrl().split("/")[0]);
                file.mkdirs();
                File outputFile = new File(file, bodyList.get(i).getOutgoing().getSampleFileUrl().split("/")[1]);
                output = new FileOutputStream(outputFile);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        break;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            } finally {
                try {
                    if (output != null) output.close();
                    if (input != null) input.close();
                } catch (IOException ignored) {
                }

                if (connection != null) connection.disconnect();
            }
            return false;
        }
    }


}
