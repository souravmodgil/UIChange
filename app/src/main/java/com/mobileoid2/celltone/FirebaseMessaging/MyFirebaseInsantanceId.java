package com.mobileoid2.celltone.FirebaseMessaging;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.mobileoid2.celltone.network.APIClient;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.ApiInterface;
import com.mobileoid2.celltone.network.NetworkCallBack;
import com.mobileoid2.celltone.network.SendRequest;
import com.mobileoid2.celltone.network.jsonparsing.JsonResponse;
import com.mobileoid2.celltone.pojo.FCMMODELREQUEST;
import com.mobileoid2.celltone.utility.SharedPrefrenceHandler;

import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by root on 19/12/17.
 */

public class MyFirebaseInsantanceId extends FirebaseInstanceIdService implements NetworkCallBack {

    final String TAG = this.getClass().getSimpleName();

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);


        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        SharedPrefrenceHandler.getInstance().setFCPTOKEN(refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        if (SharedPrefrenceHandler.getInstance().getLoginState())
        {
          //  cDNmAO6DTIc:APA91bHBq8t9wp33M6Mgrq1VdFlM9rtVxQXmhoctUwNFpm6JDsCbcjw838U5SiWNXC5wZbc7v7fxZOPg6msy
            // 3O6aBXtUVKw5VPT3iH67JKWWF5o1id3uY8HpwgseNRMKWQcHsxaw0F8T
            ApiInterface apiInterface  = (ApiInterface) APIClient.getClient().create(ApiInterface.class);
            FCMMODELREQUEST fcmmodelrequest = new FCMMODELREQUEST();
            fcmmodelrequest.setFcpId(refreshedToken);

            SendRequest.sendRequest(ApiConstant.FCM_API,apiInterface.uploadFcm(SharedPrefrenceHandler.getInstance().getUSER_TOKEN(),fcmmodelrequest),MyFirebaseInsantanceId.this);
        }

    }

    @Override
    public void getResponse(JsonResponse response, int type) {
        if(response.getObject()!=null)
            SharedPrefrenceHandler.getInstance().setFCMID_SEND(true);


    }
}
