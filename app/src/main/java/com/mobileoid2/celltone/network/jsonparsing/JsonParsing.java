package com.mobileoid2.celltone.network.jsonparsing;

import com.google.gson.Gson;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.NetworkCallBack;


import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by Sourav on 23/03/18.
 */

public class JsonParsing<T> {
    private static NetworkCallBack callBack;
    private static int type;

    public static JsonResponse getJsonResponse(String response, int type) {
        try {
            Gson gsonObj = new Gson();
            JSONObject jsonobject = null;

            //  Object object = new JSONObject(response);
            Object object = new JSONTokener(response).nextValue();
            if (object instanceof JSONArray) {
                JSONArray jsonarray = new JSONArray(response);
                jsonobject = jsonarray.getJSONObject(0);
            } else {
                jsonobject = (JSONObject) object;
            }

            JsonResponse response1 = new JsonResponse();
            response1.setObject(jsonobject.toString());
            response1.setErrorString("");
            return response1;

        } catch (Exception ex) {

            JsonResponse response1 = new JsonResponse();
            response1.setObject(null);
            response1.setErrorString(ApiConstant.INVALIDAPIFORMAT);
            return response1;

        }


    }


}
