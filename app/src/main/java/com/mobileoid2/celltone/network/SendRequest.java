package com.mobileoid2.celltone.network;

import android.os.AsyncTask;


import com.mobileoid2.celltone.network.jsonparsing.JsonParsing;
import com.mobileoid2.celltone.network.jsonparsing.JsonResponse;

import java.net.ProtocolException;
import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sourav on 23/03/18.
 */

public class SendRequest {
    public static void sendRequest(final int type, Call<String> sendRequestMethod,
                                   final NetworkCallBack callBack) {

        sendRequestMethod.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null) {

                    SendRequest sendRequest = new SendRequest();
                    sendRequest.parseRequest(type,response,callBack);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                JsonResponse response1 = new JsonResponse();
                if(t instanceof ProtocolException)
                    response1.setErrorString("SomeThing went wrong");
                  else if (t instanceof SocketTimeoutException)
                    response1.setErrorString("Request Timeout");
                else
                response1.setErrorString("Please Check Internet Connectivity.");
                response1.setObject(null);
                callBack.getResponse(response1, type);

            }
        });


    }
    private  void parseRequest(final int type, Response<String> response,
                               final NetworkCallBack callBack)
    {

        new DoSomeTask(type,response,callBack).execute(new String[]{""});
    }

    class DoSomeTask extends AsyncTask<String, Void, String> {
        private int type;
        private  JsonResponse jsonResponse;
        private Response<String> response;
        private NetworkCallBack callBack;


        DoSomeTask(int type, Response<String> response,
                   NetworkCallBack callBack) {
            this.type = type;
            this.callBack = callBack;
            this.response =response;
        }

        protected String doInBackground(String... paramVarArgs) {
            if(response!=null && response.body()!=null) {
                jsonResponse = JsonParsing.getJsonResponse(response.body().toString(), type);
            }
            else
            {
                jsonResponse = new JsonResponse();
                jsonResponse.setObject(null);
                jsonResponse.setErrorString(ApiConstant.INTERNELSERVEREROR);


            }
            return "";
        }

        protected void onPostExecute(String paramString) {
            callBack.getResponse(jsonResponse, type);

        }

        protected void onPreExecute() {
            super.onPreExecute();
        }
    }


}
