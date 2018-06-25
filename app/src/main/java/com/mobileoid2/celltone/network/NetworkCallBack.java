package com.mobileoid2.celltone.network;

import com.mobileoid2.celltone.network.jsonparsing.JsonResponse;

public interface NetworkCallBack {
    public void getResponse(JsonResponse response, int type);
}
